package it.riccisi.forma;

import java.util.Iterator;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;

/**
 * Model established by binding represented data to metadata.
 *
 * <p>Construction eagerly binds every semantic attribute. Consequently, an
 * instance exists only after all metadata invariants have been satisfied.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
                    attribute -> {
                        final PropertyReference reference =
                            mapping.property(attribute.name());
                        try {
                            return attribute.from(
                                new PropertyAt(reference, data)
                            );
                        } catch (final BindingReason reason) {
                            throw new BindingFailure(
                                attribute.name(),
                                reference,
                                reason
                            );
                        }
                    },
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
