package org.pinapp.notification.providers.sms;

import lombok.extern.slf4j.Slf4j;
import org.pinapp.notification.api.ChannelType;
import org.pinapp.notification.api.NotificationProvider;
import org.pinapp.notification.api.record.NotificationData;

/**
 * Implementación del proveedor <b>Twilio</b> para el envío de mensajes de texto (SMS).
 * <p>
 * Esta clase integra las capacidades de la API de Twilio para procesar notificaciones
 * cuyo canal de comunicación sea {@link ChannelType#SMS}.
 * </p>
 * * @author PinApp Team
 * @version 1.0
 * @see NotificationProvider
 */
@Slf4j
public class TwilioSmsProvider implements NotificationProvider {

    /** Clave de API necesaria para la autenticación con los servicios de Twilio. */
    private final String apiKey;

    /**
     * Construye una nueva instancia del proveedor con las credenciales necesarias.
     * * @param apiKey La clave de autenticación (Token) de la cuenta de Twilio.
     */
    public TwilioSmsProvider(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Realiza el despacho de la notificación de SMS a través de la plataforma Twilio.
     * <p>
     * Nota: La implementación actual requiere la configuración previa de las credenciales
     * de Twilio (Account SID y Auth Token).
     * </p>
     * * @param notification El objeto de notificación que contiene el número destino
     * y el cuerpo del mensaje.
     */
    @Override
    public void send(NotificationData notification) {
        // Lógica de envío pendiente de implementar
        log.info("SMS enviado a través de Twilio: {}", notification);
    }

    /**
     * Verifica si este proveedor es compatible con el tipo de canal solicitado.
     * * @param type El tipo de canal a validar.
     * @return {@code true} si el tipo de canal es {@link ChannelType#SMS},
     * {@code false} en cualquier otro caso.
     */
    @Override
    public boolean supports(ChannelType type) {
        return type == ChannelType.SMS;
    }

    /**
     * Proporciona el nombre técnico del proveedor para fines de registro y auditoría.
     * * @return Una cadena constante que representa a este proveedor: "TwilioSmsProvider".
     */
    @Override
    public String getProviderName() {
        return "TwilioSmsProvider";
    }
}