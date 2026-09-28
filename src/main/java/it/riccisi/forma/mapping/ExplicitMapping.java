package it.riccisi.forma.mapping;

import it.riccisi.forma.AttributeName;
import it.riccisi.forma.PropertyMapping;
import it.riccisi.forma.PropertyReference;
import java.util.Map;
import lombok.NonNull;

/**
 * Property mapping defined by explicit semantic-to-representation associations.
 */
public final class ExplicitMapping implements PropertyMapping {

    private final Map<AttributeName<?>, PropertyReference> references;

    public ExplicitMapping(
        @NonNull final Map<? extends AttributeName<?>, ? extends PropertyReference> references
    ) {
        this.references = Map.copyOf(references);
    }

    @Override
    public PropertyReference property(final AttributeName<?> attribute) {
        final PropertyReference reference = this.references.get(attribute);
        if (reference == null) {
            throw new IllegalArgumentException(
                String.format("No property mapping for attribute '%s'", attribute)
            );
        }
        return reference;
    }
}
