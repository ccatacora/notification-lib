package org.pinapp.notification.api;

import org.pinapp.notification.api.record.NotificationData;

/**
 * Interfaz base para todos los proveedores de notificaciones.
 * <p>
 * Implementar esta interfaz para integrar nuevos servicios como
 * SendGrid, Twilio o Firebase.
 */
public interface NotificationProvider {

    /**
     * Realiza el envío de la notificación.
     * * @param notification El objeto con los datos del mensaje.
     * @throws RuntimeException si ocurre un error durante el transporte.
     */
    void send(NotificationData notification);

    /**
     * Indica si este proveedor es capaz de manejar un tipo de canal específico.
     * * @param type El tipo de canal (EMAIL, SMS, etc.).
     * @return true si el proveedor soporta el canal.
     */
    boolean supports(ChannelType type);

    /**
     * Retorna el nombre comercial del proveedor.
     * * @return Nombre del proveedor (ej. "Mailgun").
     */
    String getProviderName();
}