package org.pinapp.notification.api.record;

import org.pinapp.notification.api.NotificationPriority;
import org.pinapp.notification.api.exceptions.ValidationArgumentException;
import java.time.LocalDateTime;

public sealed interface NotificationData permits EmailData, SmsData, PushNotificationData {

    // --- "Propiedades" obligatorias (definidas como métodos) ---

    /** @return La dirección, número o ID del remitente. */
    String from();
    /** @return La dirección, número o ID del destinatario. */
    String to();
    /** @return El cuerpo de la notificacion. */
    String body();
    /** @return Fecha y hora en la que se creó la solicitud de notificación. */
    LocalDateTime createdAt();
    /** @return La prioridad de la notificación. */
    NotificationPriority priority();

    // --- Métodos de Validación ---

    /**
     * Valida la integridad completa del objeto.
     * @throws ValidationArgumentException si algún campo falla.
     */
    default void validateAll() {
        validateFrom();
        validateTo();
        validateBody();
        validatePriority();
    }

    default void validateFrom() {
        if (from() == null || from().isBlank()) {
            throw new ValidationArgumentException("El remitente es obligatorio");
        }
    }

    default void validateTo() {
        if (to() == null || to().isBlank()) {
            throw new ValidationArgumentException("El destinatario es obligatorio");
        }
    }

    default void validateBody() {
        if (body() == null || body().isBlank()) {
            throw new ValidationArgumentException("El cuerpo del mensaje es obligatorio");
        }
    }

    default void validatePriority() {
        if (priority() == null) {
            throw new ValidationArgumentException("La prioridad del mensaje es obligatoria");
        }
    }
}