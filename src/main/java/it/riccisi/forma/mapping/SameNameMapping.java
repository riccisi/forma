package it.riccisi.forma.mapping;

import it.riccisi.forma.AttributeName;
import it.riccisi.forma.FieldMapping;
import it.riccisi.forma.FieldReference;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Text;

/** Field mapping deriving representation coordinates from attribute names. */
@RequiredArgsConstructor
public final class SameNameMapping implements FieldMapping {

    @NonNull private final Function<Text, ? extends FieldReference> reference;

    @Override
    public FieldReference reference(final AttributeName<?> attribute) {
        return this.reference.apply(attribute);
    }
}
