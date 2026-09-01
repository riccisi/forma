package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Attribute accepting only positive numeric semantic values.
 *
 * @param <T> numeric semantic value type
 */
@RequiredArgsConstructor
public final class PositiveAttribute<T extends Number> implements Attribute<T> {

    @NonNull private final Attribute<T> origin;

    @Override
    public AttributeName<T> name() {
        return this.origin.name();
    }

    @Override
    public ModelAttribute<T> bind(final Property property) {
        final ModelAttribute<T> bound = this.origin.bind(property);
        if (bound.value().doubleValue() <= 0.0d) {
            throw new IllegalArgumentException(
                "The semantic number must be positive"
            );
        }
        return bound;
    }
}
