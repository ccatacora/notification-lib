package org.pinapp.notification.providers.email;

import lombok.extern.slf4j.Slf4j;
import org.pinapp.notification.api.ChannelType;
import org.pinapp.notification.api.NotificationProvider;
import org.pinapp.notification.api.record.NotificationData;

/**
 * Implementación del proveedor <b>Mailgun</b> para el envío de correos electrónicos.
 * <p>
 * Esta clase se encarga de la integración con la API de Mailgun para despachar
 * notificaciones de tipo {@link ChannelType#EMAIL}.
 * </p>
 * * @author Carlos Catacora
 * @version 1.0
 * @see NotificationProvider
 */
@Slf4j
public class MailgunEmailProvider implements NotificationProvider {

    /** Credencial de autenticación (API Key) para el servicio de Mailgun. */
    private final String apiKey;

    /**
     * Construye una nueva instancia del proveedor Mailgun.
     * * @param apiKey Clave privada proporcionada por el panel de control de Mailgun.
     */
    public MailgunEmailProvider(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Ejecuta el envío de la notificación utilizando el servicio de Mailgun.
     * * @param notification El objeto que contiene la información del mensaje y destinatario.
     * No debe ser {@code null}.
     */
    @Override
    public void send(NotificationData notification) {
        // Lógica de envío pendiente de implementar
        log.info("Email enviado ,con APIkeY, a través de Mailgun: {}", notification);
    }

    /**
     * Determina si este proveedor puede gestionar el tipo de canal solicitado.
     * * @param type El tipo de canal (EMAIL, SMS, etc.) a verificar.
     * @return {@code true} únicamente si el tipo es {@link ChannelType#EMAIL}.
     */
    @Override
    public boolean supports(ChannelType type) {
        return type == ChannelType.EMAIL;
    }

    /**
     * Devuelve el nombre identificador del proveedor.
     * * @return Una cadena con el nombre "Mailgun".
     */
    @Override
    public String getProviderName() {
        return "Mailgun";
    }
}