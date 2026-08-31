package it.riccisi.forma;

/**
 * Strategy translating semantic attribute identities into property coordinates
 * understood by a concrete data representation.
 *
 * <p>The mapping is configuration of a {@link Data} representation. Attributes
 * remain unaware of physical names, columns, positions, members, or other source
 * coordinates.
 */
public interface PropertyMapping {

    /**
     * Resolves the representation property name for a semantic attribute.
     *
     * @param attribute semantic attribute identity
     * @return representation property name
     */
    PropertyName property(AttributeName<?> attribute);
}
