package org.pinapp.notification.api.record;

import org.pinapp.notification.api.NotificationPriority;
import org.pinapp.notification.api.exceptions.ValidationArgumentException;
import java.time.LocalDateTime;

public record EmailData(

    String from,
    String to,
    String subject,
    String body,
    LocalDateTime createdAt,
    NotificationPriority priority
) implements NotificationData {
    public EmailData {
        createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }
    public void validateSpecifics() {

        if (subject == null || subject.isBlank()) {
            throw new ValidationArgumentException("El asunto es obligatorio");
        }
    }
}