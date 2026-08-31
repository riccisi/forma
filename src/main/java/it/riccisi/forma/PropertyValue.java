package it.riccisi.forma;

/**
 * A representation-level interpretation requested from a property.
 *
 * <p>A property value object defines part of the small algebra used to describe
 * represented values, such as text, numbers, booleans, arrays, or nested data.
 * It must not encode business concepts such as email addresses, money, or
 * student identifiers; those concepts belong to {@link Attribute attributes}.
 *
 * @param <T> interpreted result type
 */
public interface PropertyValue<T> {
}
