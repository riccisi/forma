package it.riccisi.forma;

/**
 * A semantic coordinate that interprets represented properties as values.
 *
 * <p>An attribute defines semantic meaning and interpretation only. It does not
 * know how its semantic identity is mapped to the coordinate of a concrete data
 * representation.
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
     * Interprets a represented property as this attribute's semantic value.
     *
     * @param property represented property to interpret
     * @return semantic value
     */
    T valueFrom(Property property);
}
