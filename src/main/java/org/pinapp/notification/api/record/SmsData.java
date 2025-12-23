package org.pinapp.notification.api.record;

import org.pinapp.notification.api.NotificationPriority;
import java.time.LocalDateTime;

public record SmsData(
        String from,
        String to,
        String body,
        LocalDateTime createdAt,
        NotificationPriority priority
) implements NotificationData {
    public SmsData {
        createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }
}