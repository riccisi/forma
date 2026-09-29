package it.riccisi.forma.json;

import java.io.Serial;

/** Failure caused by a JSON root that is not an object. */
public final class NonObjectJsonException extends JsonRepresentationException {

    @Serial private static final long serialVersionUID = 1L;

    public NonObjectJsonException() {
        super("JSON root must be an object");
    }
}
