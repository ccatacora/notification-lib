package org.pinapp.notification.api;

/**
 * Define los niveles de prioridad para las notificaciones.
 * <p>
 * Estos niveles pueden ser utilizados por los proveedores o por colas
 * internas para determinar la urgencia del transporte.
 */
public enum NotificationPriority {

    /**
     * Notificaciones críticas que deben enviarse de inmediato.
     * Ejemplo: Códigos 2FA, alertas de seguridad, restablecimiento de contraseña.
     */
    URGENT(0, true),

    /**
     * Notificaciones importantes para el usuario pero no críticas para la seguridad.
     * Ejemplo: Confirmación de compra, cambios en una reserva.
     */
    HIGH(1, true),

    /**
     * Notificaciones estándar sobre la actividad de la cuenta.
     * Ejemplo: Nuevo mensaje recibido, menciones en comentarios.
     */
    MEDIUM(2, false),

    /**
     * Notificaciones informativas o de marketing.
     * Ejemplo: Newsletter semanal, sugerencias de contenido.
     */
    LOW(3, false);

    private final int weight;
    private final boolean bypassThrottling;

    /**
     * Constructor del enum de prioridad.
     * * @param weight Valor numérico para algoritmos de ordenamiento en colas.
     * @param bypassThrottling Indica si la notificación debe ignorar límites de tasa (throttling).
     */
    NotificationPriority(int weight, boolean bypassThrottling) {
        this.weight = weight;
        this.bypassThrottling = bypassThrottling;
    }

    public int getWeight() { return weight; }
    public boolean shouldBypassThrottling() { return bypassThrottling; }
}