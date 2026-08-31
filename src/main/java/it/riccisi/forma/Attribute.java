package it.riccisi.forma;

/**
 * A semantic coordinate that can bind a represented property into a valid value.
 *
 * <p>An attribute defines the semantic meaning expected from a property. Property
 * location is owned by {@link Data}; attributes only interpret the represented
 * property selected for their {@link AttributeName}.
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
    ModelAttribute<T> bind(Property property);
}
