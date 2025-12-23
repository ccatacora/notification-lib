package org.pinapp.notification;

import org.pinapp.notification.api.ChannelType;
import org.pinapp.notification.api.NotificationPriority;
import org.pinapp.notification.api.record.EmailData;
import org.pinapp.notification.api.record.NotificationData;
import org.pinapp.notification.api.record.RetryConfig;
import org.pinapp.notification.core.NotificationService;
import org.pinapp.notification.providers.email.UnstableEmailProvider;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        //como la utilizaria un desarrollador::
        NotificationService service = new NotificationService(new RetryConfig(3, 2000));
        service.registerProvider(new UnstableEmailProvider());

        NotificationData note = new EmailData("dev@test.com", "Librería Java 21", "Funciona!","un mensaje",null ,NotificationPriority.HIGH);
        // Envío eficiente usando hilos virtuales
        service.sendAsync(ChannelType.EMAIL, note);
        Thread.sleep(10000); // Espera para ver los resultados antes de finalizar la aplicación
    }
}