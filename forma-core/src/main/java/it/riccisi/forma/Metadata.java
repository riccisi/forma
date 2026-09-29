package it.riccisi.forma;

/**
 * Semantic knowledge through which represented data can be understood.
 *
 * <p>Metadata describes the attributes whose values may be established over
 * represented data. It describes semantics; it does not read or validate data
 * during model construction.
 */
public interface Metadata extends Iterable<Attribute<?>> {
}
