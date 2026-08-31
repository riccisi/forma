package it.riccisi.forma;

/**
 * Semantic structure and invariants used to construct a model from data.
 *
 * <p>Metadata is not just a list of fields. It decides whether arbitrary
 * {@link Data} can acquire a specific semantic structure and how representation
 * coordinates are associated with semantic attributes.
 *
 * <p>Binding is construction. A {@link Model} returned by this contract is valid
 * according to this metadata and must not require a separate validation step
 * before use.
 */
public interface Metadata extends Iterable<Attribute<?, ?>> {

    /**
     * Constructs a valid semantic model from represented data.
     *
     * <p>The base contract intentionally does not define whether failures are
     * fail-fast or accumulated, nor the concrete exception or result type used by
     * implementations.
     *
     * @param data represented information to bind
     * @return a valid model
     */
    Model bind(Data data);
}
