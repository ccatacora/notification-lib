package org.pinapp.notification.providers.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pinapp.notification.api.ChannelType;
import org.pinapp.notification.api.record.EmailData;
import org.pinapp.notification.api.record.NotificationData;
import org.pinapp.notification.api.record.RetryConfig;
import org.pinapp.notification.core.NotificationService;
import java.time.Duration;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias y de integración para el flujo de envío de correos vía Mailgun.
 * <p>
 * Esta clase verifica la lógica de despacho asíncrono, la gestión de reintentos (retry policy)
 * y la correcta selección de proveedores basada en el soporte de canal.
 * </p>
 * * @author PinApp
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class MailgunEmailProviderTest {

    /** Servicio bajo prueba que orquestra el envío. */
    private NotificationService notificationService;

    /** Configuración de reintentos utilizada para las pruebas. */
    private RetryConfig retryConfig;

    /** Mock del proveedor Mailgun para simular respuestas de red o fallos. */
    @Mock
    private MailgunEmailProvider mockProvider;

    /** Datos de prueba inmutables compartidos entre escenarios. */
    private final NotificationData testData = new EmailData(
            "test@pinapp.com",
            "destino@pinapp.com",
            "Mundo",
            "Contenido de prueba",
            null,
            null
    );

    /**
     * Inicializa el entorno de prueba antes de cada ejecución.
     * Configura una política de reintentos agresiva (10ms) para minimizar la latencia de los tests.
     */
    @BeforeEach
    void setUp() {
        retryConfig = new RetryConfig(3, 10);
        notificationService = new NotificationService(retryConfig);
    }

    /**
     * Verifica que si el proveedor responde correctamente, el mensaje se envía exactamente una vez.
     * Utiliza {@link org.awaitility.Awaitility} para manejar la naturaleza asíncrona del envío.
     */
    @Test
    @DisplayName("Debe enviar exitosamente al primer intento")
    void shouldSendSuccessfullyOnFirstAttempt() {
        // Arrange
        when(mockProvider.supports(ChannelType.EMAIL)).thenReturn(true);
        notificationService.registerProvider(mockProvider);

        // Act
        notificationService.sendAsync(ChannelType.EMAIL, testData);

        // Assert: Esperamos a que el hilo virtual ejecute el envío
        await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            verify(mockProvider, times(1)).send(testData);
        });
    }

    /**
     * Valida la política de reintentos (Retry Policy).
     * Simula dos fallos consecutivos y confirma que el sistema realiza un tercer intento exitoso.
     */
    @Test
    @DisplayName("Debe reintentar y tener éxito al tercer intento")
    void shouldRetryAndSucceedAtThirdAttempt() {

        when(mockProvider.supports(ChannelType.EMAIL)).thenReturn(true);
        when(mockProvider.getProviderName()).thenReturn("MockProvider");

        // Simular 2 fallos y luego un éxito
        doThrow(new RuntimeException("Fallo 1"))
                .doThrow(new RuntimeException("Fallo 2"))
                .doNothing()
                .when(mockProvider).send(any());

        notificationService.registerProvider(mockProvider);

        // Act
        notificationService.sendAsync(ChannelType.EMAIL, testData);

        // Assert: Verificamos que se llamó exactamente 3 veces
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            verify(mockProvider, times(3)).send(testData);
        });
    }

    /**
     * Asegura que el servicio ignora a los proveedores que declaran no soportar el canal solicitado.
     * Verifica que el método {@code send()} nunca llegue a ejecutarse.
     */
    @Test
    @DisplayName("No debe llamar al proveedor si no soporta el canal")
    void shouldNotCallProviderIfChannelNotSupported() {
        // Arrange
        when(mockProvider.supports(ChannelType.EMAIL)).thenReturn(false);
        notificationService.registerProvider(mockProvider);

        // Act
        notificationService.sendAsync(ChannelType.EMAIL, testData);

        // Assert: Esperar un poco y verificar que nunca se llamó a send
        await().during(Duration.ofMillis(200)).atMost(Duration.ofMillis(500)).untilAsserted(() -> {
            verify(mockProvider, never()).send(any());
        });
    }
}