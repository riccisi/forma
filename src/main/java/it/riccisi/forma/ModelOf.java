package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;

import java.util.Iterator;

/**
 * Model established by binding represented data to metadata.
 *
 * <p>Construction eagerly binds every semantic attribute. Consequently an
 * instance exists only after all metadata invariants have been satisfied.
 */
@RequiredArgsConstructor
public final class ModelOf implements Model {

    @NonNull private final Metadata metadata;
    @NonNull private final Data data;
    @NonNull private final Iterable<ModelAttribute<?>> attributes;

    public ModelOf(
        @NonNull final Metadata metadata,
        @NonNull final Data data,
        @NonNull final PropertyMapping mapping
    ) {
        this(
            metadata,
            data,
            new ListOf<>(
                new Mapped<ModelAttribute<?>>(
                    attribute -> attribute.from(
                        new PropertyAt(mapping.property(attribute.name()), data)
                    ),
                    metadata
                )
            )
        );
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
