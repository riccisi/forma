package it.riccisi.forma;

/**
 * A semantic view of represented data through metadata.
 *
 * <p>A model composes represented {@link Data}, semantic {@link Metadata}, and
 * the mapping between their coordinates. Construction establishes that
 * composition without requiring represented values to be interpreted.
 *
 * <p>Iteration exposes semantic attribute observations described by metadata.
 * A value crosses the representation-to-semantics boundary when that
 * observation is requested. Missing, uninterpretable, or rejected represented
 * values therefore fail at observation rather than model construction.
 *
 * <p>The underlying data may contain more information than the model describes,
 * and metadata may describe values that a consumer never observes. Neither case
 * requires those values to be interpreted merely for the model object to exist.
 */
public interface Model extends Iterable<ModelAttribute<?>> {

    /**
     * Returns the metadata defining the semantic observations of this model.
     *
     * @return semantic metadata
     */
    Metadata metadata();

    /**
     * Returns the represented data observed through this model.
     *
     * @return represented data
     */
    Data data();
}
