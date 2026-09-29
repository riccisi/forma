package it.riccisi.forma;

/**
 * Information in a representation that does not yet claim semantic validity.
 *
 * <p>Data is composed of addressable {@link Field}s without requiring the
 * representation to be materialized into an equivalent Java object graph.
 */
public interface Data extends Iterable<Field> {
}
