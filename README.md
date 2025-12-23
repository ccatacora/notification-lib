
# Notification Lib (Java 21) 🚀

Una librería de notificaciones **agnóstica, ligera y altamente escalable** diseñada para ecosistemas Java modernos. Aprovecha el poder de los hilos virtuales (Virtual Threads) para manejar volúmenes masivos de envíos sin penalizar el rendimiento.

---

## ✨ Características Principales

* **Java 21 Ready:** Optimizado para **Virtual Threads (Project Loom)**, permitiendo envíos masivos I/O-bound sin bloquear hilos de plataforma.
* **Agnóstica:** Cero dependencias de frameworks pesados (Spring, Quarkus, Micronaut). Úsala en cualquier proyecto Java.
* **Extensible:** Arquitectura basada en interfaces para añadir canales (Telegram, WhatsApp, Slack) en minutos.
* **Inmutable:** Uso extensivo de `records` para garantizar la integridad de los datos y un código más limpio.


## 🛠️ Estructura del Proyecto

La librería sigue una arquitectura de **Puertos y Adaptadores (Hexagonal)** para mantener el dominio aislado de la tecnología externa.
```
src/main/java/org/pinapp/notification/
├── api/                        # Abstracciones y Contratos
│   ├── ChannelType.java        # Enum: EMAIL, SMS, SLACK, etc.
│   ├── NotificationProvider.java # Interfaz base para proveedores.
│   └── record/                 # Modelos de datos (Records de Java 21)
│       ├── NotificationData.java
│       └── RetryConfig.java
├── core/                       # Lógica de Negocio Central
│   └── NotificationService.java # Gestión de hilos y reintentos.
├── providers/                  # Implementaciones de Canales
│   └── email/
│       └── UnstableEmailProvider.java # Simulador de fallos.
└── Main.java                   # Clase de prueba/ejemplo.

````

## 🛠️ Guía de Uso
1. Inicialización

Configura la política de reintentos y registra los proveedores de servicios que necesites.
```Java

// Configurar reintentos: 3 intentos, delay inicial de 1000ms
RetryConfig retryConfig = new RetryConfig(3, 1000);

// Crear el servicio central
NotificationService notificationService = new NotificationService(retryConfig);

// Registrar los proveedores
notificationService.registerProvider(new MailgunEmailProvider());
notificationService.registerProvider(new TwilioSmsProvider());
````
2. Envío de Notificaciones

Crea un objeto NotificationData para cada correspondiente provider y despáchalo de forma asíncrona.
````Java

NotificationData notice = new EmailData(
    "admin@empresa.com",
    "destino@empresa.com"
    "Alerta de Sistema",
    "El servidor ha superado el 90% de CPU",
    null,
    null
);

// Envío asíncrono - No bloquea, gestionado por hilos virtuales
notificationService.sendAsync(ChannelType.EMAIL, notice);
````
## 🔧 Extensibilidad: ¿Cómo añadir un nuevo canal?

Si necesitas integrar un canal no soportado (ej. Slack), solo debes implementar la interfaz NotificationProvider:
````Java

/**
 * Ejemplo de extensión para Slack.
 */
public class SlackProvider implements NotificationProvider {
    @Override
    public void send(NotificationData n) {
        // Lógica para enviar vía Webhook de Slack usando la API Key
    }

    @Override
    public boolean supports(ChannelType type) {
        return type == ChannelType.SLACK;
    }

    @Override
    public String getProviderName() {
        return "SlackAPI";
    }
}
`````
## 🧪 Pruebas y Resiliencia

La librería incluye un UnstableEmailProvider diseñado para probar la lógica de reintentos. Este proveedor fallará intencionalmente en los primeros dos intentos para demostrar cómo el NotificationService recupera la operación en el tercero.

Ejecución de Tests

Para ejecutar la suite de pruebas unitarias (JUnit 5 + Mockito):
Bash

mvn test

## 🔧 Patrones de Diseño
1. Patrón Strategy (Estrategia)

Es el corazón de la librería. Se usa para definir una familia de algoritmos (los distintos proveedores de envío), encapsular cada uno y hacerlos intercambiables.

    Implementación: La interfaz NotificationProvider es la abstracción de la estrategia, y clases como MailgunEmailProvider o TwilioSmsProvider son las estrategias concretas.

    Beneficio: Permite que el NotificationService envíe notificaciones sin saber cómo se envían realmente, facilitando el intercambio de proveedores de forma transparente.

2. Patrón Adapter (Adaptador)

Se utiliza para que la librería pueda hablar con servicios externos (APIs de terceros) que tienen interfaces diferentes, convirtiéndolas a la interfaz que nuestra librería espera.

    Implementación: Cada clase en el paquete providers actúa como un adaptador que traduce nuestra llamada estándar send(Notification n) a la lógica específica de una API externa (como la de Slack o Twilio).

    Beneficio: Aísla el código del cliente de las complejidades y cambios en las APIs de los proveedores.

3. Patrón Observer / Publish-Subscribe (Variación)

Aunque no es un Observer estricto, el NotificationService actúa como un Bus de Eventos o un despacho central.

    Implementación: El servicio mantiene una lista de proveedores registrados y "publica" la notificación al proveedor que corresponde según el ChannelType.

    Beneficio: Desacopla totalmente al emisor del receptor. El que envía la notificación no sabe quién la va a procesar.

4. Patrón Dependency Injection (Inyección de Dependencias Manual)

Al ser una librería agnóstica, no usamos frameworks como Spring. Sin embargo, aplicamos el principio de inyección de dependencias manualmente.

    Implementación: El método registerProvider(NotificationProvider provider) permite "inyectar" las dependencias en el servicio en tiempo de ejecución.

    Beneficio: Cumple con el requisito de ser configurable mediante código Java puro, permitiendo al usuario decidir qué proveedores activar sin usar archivos XML o YAML.

5. Patrón Value Object (vía Java Records)

Utilizamos records para representar la Notification.

    Implementación: El objeto Notification no tiene identidad propia más allá de sus atributos y es inmutable.

    Beneficio: Garantiza la Thread-Safety. Al usar Virtual Threads, es vital que los objetos que viajan entre hilos no puedan ser modificados, evitando condiciones de carrera (race conditions).

## 🐳 Dockerización

Si deseas probar la librería en un entorno aislado sin configurar Java localmente:

Construir la imagen:
Bash

docker build -t pinapp-notifications .

Ejecutar Demo:
Bash

docker run --rm --name notification-test pinapp-notifications

## 📋 Requisitos

    Java 21 o superior.

    Maven 3.8+.

    Lombok instalado en tu IDE.






