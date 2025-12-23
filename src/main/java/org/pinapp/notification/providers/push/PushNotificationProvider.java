package org.pinapp.notification.providers.push;

import lombok.extern.slf4j.Slf4j;
import org.pinapp.notification.api.ChannelType;
import org.pinapp.notification.api.NotificationProvider;
import org.pinapp.notification.api.record.NotificationData;

/**
 * Implementación del proveedor de notificaciones para <b>Push Notification</b>.
 * <p>
 * Esta clase permite la integración con servicios de mensajería Push para dispositivos
 * móviles o navegadores web. Se encarga de la comunicación con la infraestructura
 * de entrega mediante protocolos HTTP/REST.
 * </p>
 *
 * @author Carlos Catacora
 * @version 1.0
 * @see NotificationProvider
 */
@Slf4j
public class PushNotificationProvider implements NotificationProvider {

    /** Token de autorización o API Key para autenticar las peticiones ante el servidor Push. */
    private final String apiKey;

    /**
     * Construye una nueva instancia del proveedor Push.
     * * @param apiKey La clave de acceso necesaria para interactuar con el servicio.
     */
    public PushNotificationProvider(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Ejecuta el envío de la notificación a la infraestructura de Push Notification.
     * <p>
     * El método procesa los metadatos contenidos en {@link NotificationData} para
     * generar el payload requerido por el servidor de destino (como Firebase Cloud Messaging
     * o OneSignal).
     * </p>
     *
     * @param notification El objeto que contiene el mensaje, destinatario y metadatos del envío.
     * @throws RuntimeException si la conexión falla o el {@code apiKey} es rechazado por el servidor.
     */
    @Override
    public void send(NotificationData notification) {
        // Lógica de envío pendiente de implementar
        log.info("Notificación Push enviada a través de la infraestructura con APIKEY: {}", apiKey);
    }

    /**
     * Determina si este proveedor es capaz de gestionar el canal solicitado.
     * * @param type El tipo de canal (Email, SMS, PUSH_NOTIFICATION, etc.) a verificar.
     * @return {@code true} únicamente si el tipo es {@link ChannelType#PUSH_NOTIFICATION}.
     */
    @Override
    public boolean supports(ChannelType type) {
        return type == ChannelType.PUSH_NOTIFICATION;
    }

    /**
     * Devuelve el nombre identificador único de este proveedor dentro del sistema.
     * * @return Una cadena constante con el valor "PushNotificationProvider".
     */
    @Override
    public String getProviderName() {
        return "PushNotificationProvider";
    }
}