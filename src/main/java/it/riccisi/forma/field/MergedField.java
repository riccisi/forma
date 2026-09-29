package it.riccisi.forma.field;

import it.riccisi.forma.Data;
import it.riccisi.forma.Field;
import it.riccisi.forma.FieldReference;
import it.riccisi.forma.FieldValue;
import it.riccisi.forma.exception.MissingFieldException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Field at one coordinate of two composed data representations. */
@RequiredArgsConstructor
public final class MergedField implements Field {

    @NonNull private final FieldReference reference;
    @NonNull private final Data base;
    @NonNull private final Data overlay;

    @Override
    public FieldReference reference() {
        return this.reference;
    }

    @Override
    public FieldValue value() {
        Field field = new FieldAt(this.reference, this.overlay);
        try {
            field.reference();
        } catch (final MissingFieldException missing) {
            field = new FieldAt(this.reference, this.base);
        }
        return field.value();
    }
}
