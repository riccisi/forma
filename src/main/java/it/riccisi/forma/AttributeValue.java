package it.riccisi.forma;

/**
 * The semantic value of an attribute within a model.
 *
 * <p>The attribute identity is available without resolving represented data.
 * Its value is established when {@link #value()} is requested.
 *
 * @param <T> semantic value type
 */
public interface AttributeValue<T> {

    AttributeName<T> name();

    T value();
}
