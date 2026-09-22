package it.riccisi.forma;

/**
 * A valid semantic interpretation of represented data.
 *
 * <p>A model differs from a data transfer object: it does not need to reproduce
 * the represented shape as Java state. Metadata determines which represented
 * information must be understood for the model to exist. Every attribute
 * declared by that metadata is therefore established during model construction,
 * while information not described by metadata remains data and need not be
 * interpreted.
 *
 * <p>A model is complete with respect to its metadata, not with respect to its
 * data. The underlying data may contain more information than the model means,
 * and that information remains preserved without becoming semantic state merely
 * because it is present in the representation.
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
     * interpretation, including information that metadata did not require to be
     * interpreted and that therefore did not become a model attribute.
     *
     * @return represented source data
     */
    Data data();
}
