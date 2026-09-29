package it.riccisi.forma.field;

import it.riccisi.forma.Field;
import it.riccisi.forma.FieldReference;
import it.riccisi.forma.FieldValue;
import lombok.RequiredArgsConstructor;

/** Field backed directly by a representation reference and value. */
@RequiredArgsConstructor
public final class FieldOf implements Field {

    private final FieldReference reference;
    private final FieldValue value;

    @Override
    public FieldReference reference() {
        return this.reference;
    }

    @Override
    public FieldValue value() {
        return this.value;
    }
}
