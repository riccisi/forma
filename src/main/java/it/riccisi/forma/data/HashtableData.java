package it.riccisi.forma.data;

import it.riccisi.forma.Data;
import it.riccisi.forma.Field;
import it.riccisi.forma.FieldReference;
import it.riccisi.forma.FieldValue;
import it.riccisi.forma.field.FieldOf;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Minimal representation-neutral data backed by field references and values. */
public final class HashtableData implements Data {

    private final Map<FieldReference, Field> fields;

    public HashtableData(
        final Map<? extends FieldReference, ? extends FieldValue> values
    ) {
        this.fields = new LinkedHashMap<>(values.size());
        values.forEach(
            (reference, value) -> this.fields.put(
                reference,
                new FieldOf(reference, value)
            )
        );
    }

    @Override
    public Iterator<Field> iterator() {
        return this.fields.values().iterator();
    }
}
