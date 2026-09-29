package it.riccisi.forma.mapping;

import it.riccisi.forma.AttributeName;
import it.riccisi.forma.FieldMapping;
import it.riccisi.forma.FieldReference;
import java.util.Map;
import lombok.NonNull;

/** Field mapping defined by explicit semantic-to-representation associations. */
public final class ExplicitMapping implements FieldMapping {

    private final Map<AttributeName<?>, FieldReference> references;

    public ExplicitMapping(
        @NonNull final Map<? extends AttributeName<?>, ? extends FieldReference> references
    ) {
        this.references = Map.copyOf(references);
    }

    @Override
    public FieldReference reference(final AttributeName<?> attribute) {
        final FieldReference reference = this.references.get(attribute);
        if (reference == null) {
            throw new IllegalArgumentException(
                String.format("No field mapping for attribute '%s'", attribute)
            );
        }
        return reference;
    }
}
