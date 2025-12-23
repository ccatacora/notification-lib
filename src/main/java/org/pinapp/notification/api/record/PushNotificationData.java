package org.pinapp.notification.api.record;

import org.pinapp.notification.api.NotificationPriority;
import java.time.LocalDateTime;

public record PushNotificationData(

        String from,
        String to,
        String body,
        LocalDateTime createdAt,
        NotificationPriority priority
) implements NotificationData {
    public PushNotificationData {
        createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }
}