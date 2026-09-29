package it.riccisi.forma.json;

import java.io.Serial;

/** Failure to represent a JSON source as Forma data. */
public final class JsonDataException extends IllegalArgumentException {

    @Serial private static final long serialVersionUID = 1L;

    public JsonDataException(final Throwable cause) {
        super("Unable to represent JSON as Forma data", cause);
    }
}
