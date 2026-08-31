package it.riccisi.forma;

/**
 * Information in a representation that does not yet claim semantic validity.
 *
 * <p>A data object represents {@link Property} objects without requiring the
 * representation to be materialized into an equivalent Java object graph. It may
 * be complete, partial, projected, filtered, merged, or backed dynamically by a
 * source whose contents can change independently from this object's identity.
 *
 * <p>Each property carries its own representation coordinate. Operations such as
 * lookup by reference are therefore derivable from iteration and are deliberately
 * not prescribed by the core Data contract.
 */
public interface Data extends Iterable<Property> {
}
