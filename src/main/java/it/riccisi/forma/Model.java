package it.riccisi.forma;

/**
 * A semantic view of represented data through metadata.
 *
 * <p>A model composes represented {@link Data} and semantic {@link Metadata}.
 * Construction establishes that composition without requiring represented
 * values to be interpreted.
 *
 * <p>Iteration exposes semantic attribute observations described by metadata.
 * A value crosses the representation-to-semantics boundary when that
 * observation is requested.
 */
public interface Model extends Iterable<ModelAttribute<?>> {

    Metadata metadata();

    Data data();
}
