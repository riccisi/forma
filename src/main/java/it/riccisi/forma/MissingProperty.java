package it.riccisi.forma;

/**
 * Binding reason stating that the selected representation coordinate has no
 * property in the supplied data.
 */
public final class MissingProperty extends BindingReason {

    private static final long serialVersionUID = 1L;

    public MissingProperty() {
        super("The represented property does not exist");
    }

    @Override
    public <T> T describe(final BindingReasonSelection<T> selection) {
        return selection.missingProperty();
    }
}
