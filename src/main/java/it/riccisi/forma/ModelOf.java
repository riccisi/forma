package it.riccisi.forma;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.NonNull;

/**
 * Model established by binding represented data to metadata.
 *
 * <p>Construction eagerly binds every semantic attribute. Consequently an
 * instance exists only after all metadata invariants have been satisfied.
 */
public final class ModelOf implements Model {

    private final Metadata metadata;
    private final Data data;
    private final List<ModelAttribute<?>> attributes;

    public ModelOf(
        @NonNull final Metadata metadata,
        @NonNull final Data data,
        @NonNull final PropertyMapping mapping
    ) {
        this.metadata = metadata;
        this.data = data;
        this.attributes = new ArrayList<>();
        for (final Attribute<?> attribute : metadata) {
            this.attributes.add(
                attribute.bind(
                    new PropertyAt(mapping.property(attribute.name()), data)
                )
            );
        }
    }

    @Override
    public Metadata metadata() {
        return this.metadata;
    }

    @Override
    public Data data() {
        return this.data;
    }

    @Override
    public Iterator<ModelAttribute<?>> iterator() {
        return this.attributes.iterator();
    }
}
