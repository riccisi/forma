package it.riccisi.forma;

/**
 * A semantic coordinate that can bind a represented property into a valid value.
 *
 * <p>An attribute defines what semantic value is expected from a property and
 * what rules must hold for that value. Attribute implementations may be composed
 * to add stronger meaning, such as required values, positive numbers, emails, or
 * money, without moving interpretation into external validators or mappers.
 *
 * <p>Two attributes are not identical merely because they share the same Java
 * result type or textual label. Their identity is expressed by their
 * {@link AttributeName}.
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
     * <p>A successful result is evidence that the property has been interpreted
     * and accepted as this attribute's semantic value. The public failure model
     * is intentionally not defined by this base contract yet.
     *
     * @param property represented property to interpret
     * @return successfully bound model attribute
     */
    ModelAttribute<T> bind(Property property);
}
