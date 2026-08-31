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
 * {@link PropertyReference}s and each iterated {@link Property} carries its own
 * coordinate and value.
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
                new ValueProperty(reference, value)
            )
        );
    }

    @Override
    public Iterator<Property> iterator() {
        return this.properties.values().iterator();
    }
}
