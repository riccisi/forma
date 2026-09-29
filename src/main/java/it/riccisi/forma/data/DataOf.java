package it.riccisi.forma.data;

import it.riccisi.forma.Data;
import it.riccisi.forma.Field;
import java.util.Iterator;
import lombok.NonNull;
import org.cactoos.list.ListOf;

/** Data composed directly from represented fields. */
public final class DataOf implements Data {

    private final Iterable<Field> fields;

    public DataOf(final Field... fields) {
        this(new ListOf<>(fields));
    }

    public DataOf(@NonNull final Iterable<Field> fields) {
        this.fields = new ListOf<>(fields);
    }

    @Override
    public Iterator<Field> iterator() {
        return this.fields.iterator();
    }
}
