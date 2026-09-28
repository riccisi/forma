package it.riccisi.forma;

import java.util.Iterator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.iterator.Mapped;

/**
 * Semantic view of represented data through metadata.
 *
 * <p>Construction composes the objects required for semantic observation. It
 * does not interpret represented values. Iteration produces semantic attribute
 * observations; their values are established only when requested.
 */
@RequiredArgsConstructor
public final class ModelOf implements Model {

    @NonNull private final Metadata metadata;
    @NonNull private final Data data;
    @NonNull private final PropertyMapping mapping;

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
        return new Mapped<>(
            attribute -> new ObservedAttribute<>(
                attribute,
                this.mapping,
                this.data
            ),
            this.metadata.iterator()
        );
    }
}
