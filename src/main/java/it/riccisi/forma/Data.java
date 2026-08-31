package it.riccisi.forma;

/**
 * Information in a representation that does not yet claim semantic validity.
 *
 * <p>A data object owns access to represented information without requiring the
 * representation to be materialized into an equivalent Java object graph. It may
 * be complete, partial, projected, filtered, merged, or backed dynamically by a
 * source whose contents can change independently from this object's identity.
 *
 * <p>Data speaks only in representation coordinates. Association with semantic
 * attributes is established externally when metadata is bound to this data.
 */
public interface Data extends Iterable<Property> {

    /**
     * Resolves a property by a coordinate understood by this representation.
     *
     * @param reference representation property reference
     * @return represented property
     */
    Property property(PropertyReference reference);
}
