package it.riccisi.forma.exception;

/** Raised when a referenced field does not exist in represented data. */
public final class MissingFieldException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public MissingFieldException() {
        super("The represented field does not exist");
    }
}
