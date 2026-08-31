package it.riccisi.forma;

/**
 * Strategy translating semantic attribute identities into representation
 * coordinates.
 *
 * <p>The mapping belongs to the binding relationship between {@link Metadata}
 * and {@link Data}: metadata owns semantic names, data owns representation
 * references, and the mapping relates the two without coupling either side to
 * the other's coordinate system.
 */
public interface PropertyMapping {

    /**
     * Resolves the representation coordinate for a semantic attribute.
     *
     * @param attribute semantic attribute identity
     * @return representation property reference
     */
    PropertyReference property(AttributeName<?> attribute);
}
