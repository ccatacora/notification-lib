package org.pinapp.notification.core;

import lombok.extern.slf4j.Slf4j;
import org.pinapp.notification.api.ChannelType;
import org.pinapp.notification.api.NotificationProvider;
import org.pinapp.notification.api.record.NotificationData;
import org.pinapp.notification.api.record.RetryConfig;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Servicio central encargado de la gestión y despacho de notificaciones.
 * <p>
 * Esta clase implementa una arquitectura de envío asíncrono utilizando <b>Virtual Threads</b>
 * (disponibles desde Java 21) para maximizar el rendimiento sin bloquear el hilo principal.
 * Incluye un mecanismo de resiliencia mediante políticas de reintento con <b>backoff exponencial</b>.
 * </p>
 * * @author Carlos Catacora
 * @version 1.1
 */
@Slf4j
public class NotificationService {

    /** Lista thread-safe de proveedores registrados. */
    private final List<NotificationProvider> providers = new CopyOnWriteArrayList<>();

    /** Ejecutor configurado para usar hilos virtuales (Project Loom). */
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    /** Configuración de la política de reintentos (intentos máximos y delay base). */
    private final RetryConfig retryConfig;

    /**
     * Construye un nuevo servicio de notificaciones con una configuración de reintento específica.
     * * @param retryConfig Objeto con los parámetros para la gestión de fallos.
     */
    public NotificationService(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    /**
     * Registra un nuevo proveedor de servicios (Email, SMS, Push, etc.) en el sistema.
     * * @param provider Instancia del proveedor que implementa {@link NotificationProvider}.
     */
    public void registerProvider(NotificationProvider provider) {
        this.providers.add(provider);
    }

    /**
     * Lógica interna de ejecución con reintentos y retroceso exponencial.
     * <p>
     * El tiempo de espera entre intentos se calcula como: {@code delayMillis * 2^(intentos-1)}.
     * </p>
     * * @param provider El proveedor seleccionado para el envío.
     * @param notification Los datos de la notificación a enviar.
     */
    private void executeWithRetry(NotificationProvider provider, NotificationData notification) {
        int attempts = 0;

        while (attempts < retryConfig.maxAttempts()) {
            try {
                attempts++;
                provider.send(notification);
                log.info("Notificación enviada exitosamente en el intento {} vía {}", attempts, provider.getProviderName());
                return;
            } catch (Exception e) {
                log.warn("Intento {} fallido para el proveedor {}: {}", attempts, provider.getProviderName(), e.getMessage());

                if (attempts >= retryConfig.maxAttempts()) {
                    log.error("Se agotaron los reintentos ({}) para la notificación a: {}", retryConfig.maxAttempts(), notification.to());
                    break;
                }

                waitBeforeRetry(attempts);
            }
        }
    }

    /**
     * Pausa la ejecución del hilo actual antes de realizar un nuevo intento.
     * * @param attempts Número de intentos realizados hasta el momento, usado para calcular el backoff.
     */
    private void waitBeforeRetry(int attempts) {
        try {
            // Backoff exponencial simple: base * 2^(intentos-1)
            long sleepTime = retryConfig.delayMillis() * (1L << (attempts - 1));
            log.debug("Esperando {} ms antes del próximo intento (Intento: {})", sleepTime, attempts);
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("El hilo fue interrumpido durante el backoff en el intento {}: {}", attempts, ie.getMessage());
        }
    }

    /**
     * Despacha una notificación de forma asíncrona.
     * <p>
     * Este método busca el primer proveedor que soporte el {@link ChannelType} solicitado
     * y delega la tarea al {@code ExecutorService} de hilos virtuales. Si no se encuentra
     * un proveedor adecuado, se registra un error en el log.
     * </p>
     * * @param type El tipo de canal (ej. EMAIL, SMS).
     * @param notification El contenido y destinatario de la notificación.
     */
    public void sendAsync(ChannelType type, NotificationData notification) {
        if (executor.isShutdown()) {
            log.error("CRÍTICO: El ejecutor está cerrado.");
            return;
        }

        executor.submit(() -> {
            try {
                providers.stream()
                        .filter(p -> p.supports(type))
                        .findFirst()
                        .ifPresentOrElse(
                                p -> executeWithRetry(p, notification),
                                () -> log.error("ERROR: No se encontró proveedor para el canal: " + type)
                        );
            } catch (Exception e) {
                log.error("ERROR en hilo virtual: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}