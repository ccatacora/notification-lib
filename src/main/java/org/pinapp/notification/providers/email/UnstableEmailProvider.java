package org.pinapp.notification.providers.email;

import lombok.extern.slf4j.Slf4j;
import org.pinapp.notification.api.ChannelType;
import org.pinapp.notification.api.NotificationProvider;
import org.pinapp.notification.api.record.NotificationData;

/**
 * Implementación de {@link NotificationProvider} diseñada para simular fallos intermitentes.
 * <p>
 * Esta clase es útil para validar mecanismos de tolerancia a fallos y políticas de reintento (retries).
 * El proveedor fallará sistemáticamente en las primeras llamadas y tendrá éxito a partir del
 * tercer intento.
 * </p>
 * * @author Carlos Catacora
 * @version 1.0
 */
@Slf4j
public class UnstableEmailProvider implements NotificationProvider {

    /** Contador interno para trackear el número de intentos de envío. */
    private int count = 0;

    /**
     * Intenta enviar una notificación de correo electrónico.
     * <p>
     * <b>Comportamiento de inestabilidad:</b>
     * Las primeras dos llamadas lanzarán una {@link RuntimeException}.
     * A partir de la tercera llamada ({@code count >= 3}), el envío se realizará con éxito.
     * </p>
     * * @param notification El objeto de notificación que contiene el destinatario y mensaje.
     * @throws RuntimeException Si el contador de intentos es menor a 3, simulando un error de red.
     */
    @Override
    public void send(NotificationData notification) {
        count++;
        if (count < 3) {
            log.warn("[UnstableEmailProvider] Intento fallido {} para enviar email a: {}", count, notification.to());
            throw new RuntimeException("Error temporal de red (Simulado)");
        }
        log.info("[UnstableEmailProvider] Enviando email exitosamente a: {}", notification.to());
    }

    /**
     * Determina si este proveedor puede manejar el tipo de canal especificado.
     * * @param type El tipo de canal a verificar.
     * @return {@code true} si el canal es {@link ChannelType#EMAIL}, de lo contrario {@code false}.
     */
    @Override
    public boolean supports(ChannelType type) {
        return type == ChannelType.EMAIL;
    }

    /**
     * Retorna el identificador único del proveedor.
     * * @return El nombre del servicio: "UnstableEmailProvider".
     */
    @Override
    public String getProviderName() {
        return "UnstableEmailProvider";
    }
}