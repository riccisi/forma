package it.riccisi.forma;

/**
 * Semantic structure and invariants used to construct a model from data.
 *
 * <p>Metadata is not just a list of fields. It decides whether arbitrary
 * {@link Data} can acquire a specific semantic structure.
 *
 * <p>Binding is construction. A {@link Model} returned by this contract is valid
 * according to this metadata and must not require a separate validation step
 * before use. A {@link PropertyMapping} supplied for the binding relates semantic
 * attribute names to the coordinates understood by the represented data.
 */
public interface Metadata extends Iterable<Attribute<?>> {

    /**
     * Constructs a valid semantic model from represented data.
     *
     * @param data represented information to bind
     * @param mapping association between semantic names and data coordinates
     * @return a valid model
     */
    Model bind(Data data, PropertyMapping mapping);
}
