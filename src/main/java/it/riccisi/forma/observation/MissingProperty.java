package it.riccisi.forma.observation;

/**
 * Observation reason stating that the selected representation coordinate has no
 * property in the supplied data.
 */
public final class MissingProperty extends ObservationReason {

    private static final long serialVersionUID = 1L;

    public MissingProperty() {
        super("The represented property does not exist");
    }

    @Override
    public <T> T describe(final ObservationReasonSelection<T> selection) {
        return selection.missingProperty();
    }
}
