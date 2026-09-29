package it.riccisi.forma.model;

import it.riccisi.forma.AttributeValue;
import it.riccisi.forma.Data;
import it.riccisi.forma.FieldMapping;
import it.riccisi.forma.Metadata;
import it.riccisi.forma.Model;
import java.util.Iterator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.iterator.Mapped;

/** An instance of metadata over represented data. */
@RequiredArgsConstructor
public final class ModelOf implements Model {

    @NonNull private final Metadata metadata;
    @NonNull private final Data data;
    @NonNull private final FieldMapping mapping;

    @Override
    public Metadata metadata() {
        return this.metadata;
    }

    @Override
    public Data data() {
        return this.data;
    }

    @Override
    public Iterator<AttributeValue<?>> iterator() {
        return new Mapped<>(
            attribute -> new AttributeValueOf<>(
                attribute,
                this.mapping,
                this.data
            ),
            this.metadata.iterator()
        );
    }
}
