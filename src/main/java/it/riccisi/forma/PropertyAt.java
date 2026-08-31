package it.riccisi.forma;

import java.util.NoSuchElementException;

/**
 * Property addressed by a representation coordinate inside data.
 *
 * <p>The lookup itself is modeled as a property. Resolution is derived from the
 * iterable {@link Data} contract rather than being a responsibility of Data.
 */
public final class PropertyAt implements Property {

    private final PropertyReference reference;
    private final Data data;

    public PropertyAt(final PropertyReference reference, final Data data) {
        this.reference = reference;
        this.data = data;
    }

    @Override
    public PropertyReference reference() {
        return this.reference;
    }

    @Override
    public PropertyValue value() {
        for (final Property property : this.data) {
            if (property.reference().equals(this.reference)) {
                return property.value();
            }
        }
        throw new NoSuchElementException(
            "No property exists at the supplied reference"
        );
    }
}
