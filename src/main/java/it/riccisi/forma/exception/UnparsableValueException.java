package it.riccisi.forma.exception;

/**
 * Raised when represented information cannot provide the primitive value form
 * required by an attribute.
 */
public final class UnparsableValueException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public UnparsableValueException(final Throwable cause) {
        super("The represented value cannot be parsed as required", cause);
    }
}
