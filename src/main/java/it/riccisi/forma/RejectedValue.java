package it.riccisi.forma;

/**
 * Binding reason stating that represented information was interpretable but
 * did not satisfy a semantic constraint.
 */
public final class RejectedValue extends BindingReason {

    private static final long serialVersionUID = 1L;

    public RejectedValue(final String message) {
        super(message);
    }

    @Override
    public <T> T describe(final BindingReasonSelection<T> selection) {
        return selection.rejectedValue();
    }
}
