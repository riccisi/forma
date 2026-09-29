package it.riccisi.forma.field;

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
 * Field addressed by a representation coordinate among fields.
 *
 * <p>The lookup itself is modeled as a field. Resolution depends only on an
 * iterable source of fields, allowing addressing over Data as well as composed
 * field sources.
 */
@RequiredArgsConstructor
public final class FieldAt implements Field {

    @NonNull private final Scalar<Field> field;

    public FieldAt(
        final FieldReference reference,
        final Iterable<Field> fields
    ) {
        this(new Sticky<>(new FirstOf<>(
            item -> item.reference().equals(reference),
            fields,
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
