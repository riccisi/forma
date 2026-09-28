package it.riccisi.forma.data;

import it.riccisi.forma.Data;
import it.riccisi.forma.Property;
import java.util.Iterator;
import lombok.NonNull;
import org.cactoos.list.ListOf;

/**
 * Data composed directly from represented properties.
 */
public final class DataOf implements Data {

    private final Iterable<Property> properties;

    public DataOf(final Property... properties) {
        this(new ListOf<>(properties));
    }

    public DataOf(@NonNull final Iterable<Property> properties) {
        this.properties = new ListOf<>(properties);
    }

    @Override
    public Iterator<Property> iterator() {
        return this.properties.iterator();
    }
}
