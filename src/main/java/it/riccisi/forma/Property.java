package it.riccisi.forma;

/**
 * An addressable, individually interpretable portion of represented data.
 *
 * <p>A property belongs to the representation side of Forma. It carries the
 * coordinate by which it is identified inside its data representation and exposes
 * its represented value through the representation-neutral {@link PropertyValue}
 * abstraction.
 */
public interface Property {

    /**
     * Returns this property's coordinate in the represented data.
     *
     * @return representation coordinate
     */
    PropertyReference reference();

    /**
     * Returns the represented value of this property.
     *
     * @return represented value
     */
    PropertyValue value();
}
