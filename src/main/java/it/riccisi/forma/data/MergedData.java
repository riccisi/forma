package it.riccisi.forma.data;

import it.riccisi.forma.Data;
import it.riccisi.forma.Field;
import it.riccisi.forma.FieldReference;
import it.riccisi.forma.field.MergedField;
import java.util.Iterator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.set.SetOf;

/**
 * Data obtained by overlaying one representation over another.
 *
 * <p>The composed representation contains the union of the coordinates exposed
 * by both sources. Each coordinate is represented by a {@link MergedField}.
 * Construction and iteration inspect representation coordinates only; field
 * values remain unread until requested.
 */
@RequiredArgsConstructor
public final class MergedData implements Data {

    @NonNull private final Data base;
    @NonNull private final Data overlay;

    @Override
    public Iterator<Field> iterator() {
        return new Mapped<Field>(
            reference -> new MergedField(reference, this.base, this.overlay),
            new SetOf<>(
                new Joined<>(
                    new Mapped<Field, FieldReference>(
                        Field::reference,
                        this.base
                    ),
                    new Mapped<Field, FieldReference>(
                        Field::reference,
                        this.overlay
                    )
                )
            )
        ).iterator();
    }
}
