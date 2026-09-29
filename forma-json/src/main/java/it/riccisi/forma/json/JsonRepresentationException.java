package it.riccisi.forma.json;

import java.io.Serial;

/** Explicit reason why a JSON document cannot be represented as Forma data. */
abstract class JsonRepresentationException extends IllegalArgumentException {

    @Serial private static final long serialVersionUID = 1L;

    JsonRepresentationException(final String message) {
        super(message);
    }

    JsonRepresentationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
