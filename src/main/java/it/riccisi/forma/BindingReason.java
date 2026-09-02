package it.riccisi.forma;

/**
 * Semantic reason why represented information could not establish a model
 * attribute.
 *
 * <p>A reason originates at the boundary that understands the failed semantic
 * operation. Model construction later enriches it with the attribute and
 * representation coordinate involved in that attempt.
 */
public abstract class BindingReason extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    protected BindingReason(final String message) {
        super(message);
    }

    protected BindingReason(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Describe this reason through a consumer-defined interpretation.
     *
     * @param selection interpretation of the binding-reason vocabulary
     * @param <T> interpretation result type
     * @return interpreted result
     */
    public abstract <T> T describe(BindingReasonSelection<T> selection);
}
