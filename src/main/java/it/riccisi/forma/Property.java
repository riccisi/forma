package it.riccisi.forma;

/**
 * An individually interpretable portion of represented data.
 *
 * <p>A property keeps representation knowledge behind an object boundary and
 * collaborates with a representation-level interpreter instead of exposing raw
 * universal values or requiring semantic attributes to inspect concrete property
 * implementations.
 */
public interface Property {

    /**
     * Describes this represented property through the supplied interpreter.
     *
     * @param value representation-level interpretation
     * @param <T> interpreted result type
     * @return interpreted result
     */
    <T> T describe(PropertyValue<T> value);
}
