package it.riccisi.forma;

/**
 * A semantic coordinate that can bind a represented property into a valid value.
 *
 * <p>The property type expresses the representation capability required by this
 * attribute. For example, an email attribute may require a {@link TextProperty}
 * without knowing whether that text comes from JSON, a map, or a POJO.
 *
 * @param <P> property capability required by this attribute
 * @param <T> semantic value type
 */
public interface Attribute<P extends Property, T> {

    /**
     * Returns this attribute's typed semantic identity.
     *
     * @return attribute identity
     */
    AttributeName<T> name();

    /**
     * Returns the witness used to recognize the required property capability
     * when binding from generic {@link Data}.
     *
     * @return required property capability
     */
    PropertyCapability<P> capability();

    /**
     * Binds a represented property having the required capability.
     *
     * @param property represented property to interpret
     * @return successfully bound model attribute
     */
    ModelAttribute<T> bind(P property);
}
