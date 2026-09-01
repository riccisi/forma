package it.riccisi.forma;

/**
 * A semantic coordinate that can bind a represented property into a valid value.
 *
 * <p>An attribute defines semantic meaning and interpretation only. It does not
 * know how its semantic identity is mapped to the coordinate of a concrete data
 * representation; that association is supplied at metadata binding time.
 *
 * @param <T> semantic value type
 */
public interface Attribute<T> {

    /**
     * Returns this attribute's typed semantic identity.
     *
     * @return attribute identity
     */
    AttributeName<T> name();

    /**
     * Binds a represented property to this attribute.
     *
     * @param property represented property to interpret
     * @return successfully bound model attribute
     */
    ModelAttribute<T> from(Property property);
}
