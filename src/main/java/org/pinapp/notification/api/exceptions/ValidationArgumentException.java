package org.pinapp.notification.api.exceptions;
/**
 * Excepción lanzada cuando las validaciones de los argumentos de una notificación fallan.
 * <p>
 * Esta excepción es de tipo {@code RuntimeException}, lo que permite que sea capturada
 * por el manejador global de errores o por la lógica de reintentos del servicio.
 * </p>
 *
 * @author Carlos Catacora
 * @version 1.0
 */
public class ValidationArgumentException extends RuntimeException {

    /** Nombre del proveedor donde se originó la falla de validación. */
    private final String providerName;

    /**
     * Construye una nueva excepción con un mensaje detallado y el nombre del proveedor.
     *
     * @param message      Descripción detallada del error de validación.
     * @param providerName Nombre del proveedor que detectó el argumento inválido.
     */
    public ValidationArgumentException(String message, String providerName) {
        super(message);
        this.providerName = providerName;
    }

    /**
     * Construye una nueva excepción con un mensaje, el nombre del proveedor y la causa raíz.
     *
     * @param message      Descripción detallada del error.
     * @param providerName Nombre del proveedor involucrado.
     * @param cause        La causa original (otra excepción) que provocó este error.
     */
    public ValidationArgumentException(String message, String providerName, Throwable cause) {
        super(message, cause);
        this.providerName = providerName;
    }

    /**
     * Construye una excepción básica indicando únicamente el proveedor.
     *
     * @param providerName Nombre del proveedor.
     */
    public ValidationArgumentException(String providerName) {
        super("Error de validación en los argumentos del proveedor");
        this.providerName = providerName;
    }

    /**
     * Obtiene el nombre del proveedor asociado al error.
     *
     * @return {@code String} con el nombre del proveedor.
     */
    public String getProviderName() {
        return providerName;
    }
}