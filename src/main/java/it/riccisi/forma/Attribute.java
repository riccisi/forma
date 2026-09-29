package it.riccisi.forma;

/**
 * A semantic coordinate that interprets represented fields as values.
 *
 * @param <T> semantic value type
 */
public interface Attribute<T> {

    AttributeName<T> name();

    T valueFrom(Field field);
}
