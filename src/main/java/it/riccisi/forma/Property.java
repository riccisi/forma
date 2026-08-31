package it.riccisi.forma;

/**
 * An individually interpretable portion of represented data.
 *
 * <p>A property keeps representation knowledge behind an object boundary. It
 * does not expose a raw {@link Object}, string, byte array, or other universal
 * carrier because doing so would force clients to recover representation
 * semantics procedurally.
 */
public interface Property {

    /**
     * Describes this property using the requested representation-level
     * interpretation.
     *
     * <p>The supplied {@link PropertyValue} belongs to the representation layer,
     * not to the business-semantic layer. Implementations may use information
     * already available in their source representation instead of serializing and
     * parsing the property again.
     *
     * @param value requested representation-level interpretation
     * @param <T> result type produced by the interpretation
     * @return the interpreted value
     */
    <T> T describe(PropertyValue<T> value);
}
