package it.riccisi.forma.observation;

import java.io.Serial;

/**
 * Reason why a requested semantic observation could not be established.
 */
public abstract class ObservationReason extends IllegalArgumentException {

    @Serial private static final long serialVersionUID = 1L;

    protected ObservationReason(final String message) {
        super(message);
    }

    protected ObservationReason(final String message, final Throwable cause) {
        super(message, cause);
    }

    public abstract <T> T describe(ObservationReasonSelection<T> selection);
}
