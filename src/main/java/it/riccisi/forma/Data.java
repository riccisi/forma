package it.riccisi.forma;

/**
 * Information in a representation that does not yet claim semantic validity.
 *
 * <p>A data object owns access to represented information without requiring the
 * representation to be materialized into an equivalent Java object graph. It may
 * be complete, partial, projected, filtered, merged, or backed dynamically by a
 * source whose contents can change independently from this object's identity.
 *
 * <p>Iteration exposes the properties that the implementation chooses to make
 * discoverable. The contract does not require eager extraction of every possible
 * property from the underlying representation.
 */
public interface Data extends Iterable<Property> {
}
