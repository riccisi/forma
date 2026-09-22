package it.riccisi.forma;

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
 * by both sources. Each coordinate is represented by a {@link MergedProperty},
 * which resolves the effective value only when that value is observed.
 *
 * <p>Merge is observational: constructing or iterating this object enumerates
 * representation coordinates but does not observe property values and does not
 * establish semantic validity.
 */
@RequiredArgsConstructor
public final class MergedData implements Data {

    @NonNull
    private final Data base;

    @NonNull
    private final Data overlay;

    @Override
    public Iterator<Property> iterator() {
        return new Mapped<PropertyReference, Property>(
            reference -> new MergedProperty(
                reference,
                this.base,
                this.overlay
            ),
            new SetOf<>(
                new Joined<>(
                    new Mapped<Property, PropertyReference>(
                        Property::reference,
                        this.base
                    ),
                    new Mapped<Property, PropertyReference>(
                        Property::reference,
                        this.overlay
                    )
                )
            )
        ).iterator();
    }
}
