package it.riccisi.forma.field;

import it.riccisi.forma.Data;
import it.riccisi.forma.Field;
import it.riccisi.forma.FieldReference;
import it.riccisi.forma.FieldValue;
import it.riccisi.forma.exception.MissingFieldException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.scalar.FirstOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

/**
 * Field addressed by a representation coordinate inside data.
 *
 * <p>The lookup itself is modeled as a field. Resolution is derived from the
 * iterable {@link Data} contract rather than being a responsibility of Data.
 */
@RequiredArgsConstructor
public final class FieldAt implements Field {

    @NonNull private final Scalar<Field> field;

    public FieldAt(final FieldReference reference, final Data data) {
        this(new Sticky<>(new FirstOf<>(
            item -> item.reference().equals(reference),
            data,
            () -> { throw new MissingFieldException(); }
        )));
    }

    @Override
    public FieldReference reference() {
        return this.field().reference();
    }

    @Override
    public FieldValue value() {
        return this.field().value();
    }

    private Field field() {
        return new Unchecked<>(this.field).value();
    }
}
