package it.riccisi.forma;

/**
 * A valid semantic interpretation of represented data.
 *
 * <p>A model differs from a data transfer object: it does not need to reproduce
 * the represented shape as Java state. It establishes that selected information
 * from the source data satisfies the semantic structure defined by metadata,
 * while other represented information may remain preserved as data.
 *
 * <p>A model exposes the semantic attributes established by binding. Lookup by
 * attribute identity is a derived operation over this iterable observation.
 */
public interface Model extends Iterable<ModelAttribute<?>> {

    /**
     * Returns the metadata that established this model's semantic validity.
     *
     * @return metadata used for binding
     */
    Metadata metadata();

    /**
     * Returns the represented data from which this model was constructed.
     *
     * <p>Access to source data does not weaken the model invariant. It means the
     * model keeps the representation that supported the successful semantic
     * interpretation, including information that may not have become a model
     * attribute.
     *
     * @return represented source data
     */
    Data data();
}
