package org.pinapp.notification.api.record;

/**
 *       Creamos un record para configurar la política de forma inmutable. ----borrar esta linea
 * Configuración para el reintento de envío de notificaciones.
 * * @param maxAttempts Número máximo de intentos permitidos.
 * @param delayMillis Tiempo de espera inicial entre reintentos en milisegundos.
 */
public record RetryConfig(
        int maxAttempts,
        long delayMillis
) {
    /**
     * Crea una configuración por defecto (3 intentos, 1000ms de espera).
     * @return Una instancia de {@link RetryConfig} con valores estándar.
     */
    public static RetryConfig defaultPolicy() {
        return new RetryConfig(3, 1000L);
    }
}