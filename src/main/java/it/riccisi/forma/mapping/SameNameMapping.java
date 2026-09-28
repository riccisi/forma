package it.riccisi.forma.mapping;

import it.riccisi.forma.AttributeName;
import it.riccisi.forma.PropertyMapping;
import it.riccisi.forma.PropertyReference;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Text;

/**
 * Property mapping deriving representation coordinates from attribute names.
 *
 * <p>The supplied reference function remains representation-specific. This
 * mapping only establishes the convention that semantic and representation
 * names share the same text.
 */
@RequiredArgsConstructor
public final class SameNameMapping implements PropertyMapping {

    @NonNull private final Function<Text, ? extends PropertyReference> reference;

    @Override
    public PropertyReference property(final AttributeName<?> attribute) {
        return this.reference.apply(attribute);
    }
}
