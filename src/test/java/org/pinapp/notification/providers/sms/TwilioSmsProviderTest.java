package org.pinapp.notification.providers.sms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pinapp.notification.api.ChannelType;
import org.pinapp.notification.api.record.RetryConfig;
import org.pinapp.notification.api.record.SmsData;
import org.pinapp.notification.core.NotificationService;
import java.time.Duration;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la lógica de reintentos y comportamiento del proveedor de SMS.
 * <p>
 * Esta clase verifica que el {@link NotificationService} interactúe correctamente con los
 * proveedores de Twilio y gestione adecuadamente el ciclo de vida de las notificaciones asíncronas.
 * </p>
 * * @author Carlos Catacora
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class TwilioSmsProviderTest {

    /** Servicio de notificaciones bajo prueba. */
    private NotificationService notificationService;

    /** Configuración de reintentos utilizada en los escenarios de prueba. */
    private RetryConfig retryConfig;

    /** Mock del proveedor para simular respuestas de red y fallos técnicos. */
    @Mock
    private TwilioSmsProvider mockProvider;

    /** Datos de prueba constantes para asegurar consistencia en los tests. */
    private final SmsData testData = new SmsData("22113", "221331", "Mundo",  null, null);

    /**
     * Configuración inicial antes de cada caso de prueba.
     * <p>
     * Se inicializa el servicio con una configuración de reintentos agresiva (10ms)
     * para optimizar el tiempo de ejecución de la suite de pruebas.
     * </p>
     */
    @BeforeEach
    void setUp() {
        retryConfig = new RetryConfig(3, 10);
        notificationService = new NotificationService(retryConfig);
    }

    /**
     * Verifica que la librería respete el límite máximo de intentos definidos en la configuración.
     * <p>
     * <b>Escenario:</b> El proveedor externo lanza una excepción en cada intento.<br>
     * <b>Resultado esperado:</b> El servicio debe reintentar la operación exactamente
     * {@code retryConfig.maxAttempts()} veces antes de desistir.
     * </p>
     */
    @Test
    @DisplayName("Debe agotar todos los reintentos si el proveedor siempre falla")
    void shouldExhaustRetriesWhenProviderAlwaysFails() {
        // Configurar el mock para que soporte SMS y simule un fallo
        when(mockProvider.supports(ChannelType.SMS)).thenReturn(true);
        when(mockProvider.getProviderName()).thenReturn("FailingProvider");
        doThrow(new RuntimeException("Error persistente")).when(mockProvider).send(any());

        notificationService.registerProvider(mockProvider);

        // Act: Ejecución asíncrona de la notificación
        notificationService.sendAsync(ChannelType.SMS, testData);

        // Assert: Validación asíncrona mediante Awaitility
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            verify(mockProvider, times(retryConfig.maxAttempts())).send(testData);
        });
    }
}