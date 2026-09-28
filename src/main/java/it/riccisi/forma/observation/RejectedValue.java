package it.riccisi.forma.observation;

/**
 * Observation reason stating that represented information was interpretable but
 * did not satisfy a semantic constraint.
 */
public final class RejectedValue extends ObservationReason {

    private static final long serialVersionUID = 1L;

    public RejectedValue(final String message) {
        super(message);
    }

    @Override
    public <T> T describe(final ObservationReasonSelection<T> selection) {
        return selection.rejectedValue();
    }
}
