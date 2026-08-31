package it.riccisi.forma;

/**
 * An individually interpretable portion of represented data.
 *
 * <p>A property keeps representation knowledge behind an object boundary while
 * exposing its represented value through a small, representation-neutral
 * {@link PropertyValue} abstraction.
 */
public interface Property {

    /**
     * Returns the represented value of this property.
     *
     * @return represented value
     */
    PropertyValue value();
}
