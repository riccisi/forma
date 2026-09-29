package it.riccisi.forma.exception;

/** Raised when an interpretable value violates a semantic constraint. */
public final class RejectedValueException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public RejectedValueException(final String message) {
        super(message);
    }
}
