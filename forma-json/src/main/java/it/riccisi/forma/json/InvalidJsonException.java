package it.riccisi.forma.json;

import java.io.Serial;

/** Failure caused by JSON that cannot be parsed. */
public final class InvalidJsonException extends JsonRepresentationException {

    @Serial private static final long serialVersionUID = 1L;

    public InvalidJsonException(final Throwable cause) {
        super("JSON source is not a valid JSON document", cause);
    }
}
