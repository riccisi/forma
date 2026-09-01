package it.riccisi.forma;

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
    public ModelAttribute<String> from(final Property property) {
        final ModelAttribute<String> bound = this.origin.from(property);
        if (bound.value().isBlank()) {
            throw new IllegalArgumentException(
                "The semantic string cannot be blank"
            );
        }
        return bound;
    }
}
