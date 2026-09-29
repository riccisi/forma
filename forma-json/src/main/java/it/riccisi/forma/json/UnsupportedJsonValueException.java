package it.riccisi.forma.json;

import java.io.Serial;

/** Failure caused by a JSON member value outside JsonData's supported slice. */
public final class UnsupportedJsonValueException extends JsonRepresentationException {

    @Serial private static final long serialVersionUID = 1L;

    public UnsupportedJsonValueException(final String name) {
        super(String.format("JSON member '%s' has an unsupported value kind", name));
    }
}
