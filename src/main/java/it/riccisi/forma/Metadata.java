package it.riccisi.forma;

/**
 * Semantic structure and invariants of a model.
 *
 * <p>Metadata is not just a list of fields. Its attributes define the semantic
 * structure that represented data must satisfy in order to constitute a valid
 * {@link Model}.
 *
 * <p>Metadata describes semantics; it does not perform model construction.
 * Binding belongs to the model that associates metadata, represented data and
 * a {@link PropertyMapping}.
 */
public interface Metadata extends Iterable<Attribute<?>> {
}
