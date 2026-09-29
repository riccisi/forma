package it.riccisi.forma.field;

import it.riccisi.forma.Data;
import it.riccisi.forma.Field;
import it.riccisi.forma.FieldReference;
import it.riccisi.forma.FieldValue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.iterable.Joined;

/**
 * Field at one coordinate of two composed data representations.
 *
 * <p>The overlay precedes the base in the ordered source of candidate fields.
 * Addressing stops at the first field representing this coordinate, so neither
 * source value is read until {@link #value()} is requested.
 */
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
        return new FieldAt(
            this.reference,
            new Joined<>(this.overlay, this.base)
        ).value();
    }
}
