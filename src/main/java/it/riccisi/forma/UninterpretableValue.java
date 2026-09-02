package it.riccisi.forma;

/**
 * Binding reason stating that represented information cannot provide the
 * primitive interpretation required by a semantic attribute.
 */
public final class UninterpretableValue extends BindingReason {

    private static final long serialVersionUID = 1L;

    public UninterpretableValue(final Throwable cause) {
        super("The represented value cannot be interpreted as required", cause);
    }

    @Override
    public <T> T describe(final BindingReasonSelection<T> selection) {
        return selection.uninterpretableValue();
    }
}
