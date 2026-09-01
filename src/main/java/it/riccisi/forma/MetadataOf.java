package it.riccisi.forma;

import java.util.Iterator;
import lombok.NonNull;
import org.cactoos.list.ListOf;

/**
 * Metadata composed from semantic attributes.
 */
public final class MetadataOf implements Metadata {

    private final Iterable<Attribute<?>> attributes;

    public MetadataOf(final Attribute<?>... attributes) {
        this(new ListOf<>(attributes));
    }

    public MetadataOf(@NonNull final Iterable<Attribute<?>> attributes) {
        this.attributes = new ListOf<>(attributes);
    }

    @Override
    public Iterator<Attribute<?>> iterator() {
        return this.attributes.iterator();
    }
}
