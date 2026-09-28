package it.riccisi.forma.attribute;

import it.riccisi.forma.Attribute;
import it.riccisi.forma.AttributeName;
import it.riccisi.forma.Property;
import it.riccisi.forma.RejectedValue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Semantic attribute decorator rejecting blank strings.
 *
 * <p>The wrapped attribute remains responsible for interpreting represented
 * data. This decorator only constrains the already-interpreted semantic value.
 */
@RequiredArgsConstructor
public final class NonBlankAttribute implements Attribute<String> {

    @NonNull private final Attribute<String> origin;

    @Override
    public AttributeName<String> name() {
        return this.origin.name();
    }

    @Override
    public String valueFrom(final Property property) {
        final String value = this.origin.valueFrom(property);
        if (value.isBlank()) {
            throw new RejectedValue("The semantic string cannot be blank");
        }
        return value;
    }
}
