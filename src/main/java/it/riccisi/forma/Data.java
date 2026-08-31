package it.riccisi.forma;

/**
 * Information in a representation that does not yet claim semantic validity.
 *
 * <p>A data object owns access to represented information without requiring the
 * representation to be materialized into an equivalent Java object graph. It may
 * be complete, partial, projected, filtered, merged, or backed dynamically by a
 * source whose contents can change independently from this object's identity.
 *
 * <p>Property resolution belongs to the data representation. Concrete data
 * objects may use a {@link PropertyMapping} supplied at construction time to
 * translate semantic attribute identities into representation coordinates.
 */
public interface Data extends Iterable<Property> {

    /**
     * Resolves the represented property associated with a semantic attribute.
     *
     * @param name semantic attribute identity
     * @return represented property
     */
    Property property(AttributeName<?> name);
}
