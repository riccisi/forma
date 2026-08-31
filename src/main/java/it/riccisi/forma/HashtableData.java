package it.riccisi.forma;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Minimal representation-neutral data source backed by a table of coordinates
 * and representation-level values.
 *
 * <p>This class is intentionally small. It is a proving ground for Forma's core
 * object boundaries rather than a technology adapter. Coordinates remain opaque
 * {@link PropertyReference}s and values remain {@link PropertyValue}s.
 */
public final class HashtableData implements Data {

    private final Map<PropertyReference, Property> properties;

    /**
     * New data backed by the supplied coordinate-to-value associations.
     *
     * @param values represented values indexed by representation coordinates
     */
    public HashtableData(
        final Map<? extends PropertyReference, ? extends PropertyValue> values
    ) {
        this.properties = new LinkedHashMap<>(values.size());
        values.forEach(
            (reference, value) -> this.properties.put(
                reference,
                new ValueProperty(value)
            )
        );
    }

    @Override
    public Property property(final PropertyReference reference) {
        final Property property = this.properties.get(reference);
        if (property == null) {
            throw new IllegalArgumentException(
                "No property exists at the supplied reference"
            );
        }
        return property;
    }

    @Override
    public Iterator<Property> iterator() {
        return this.properties.values().iterator();
    }
}
