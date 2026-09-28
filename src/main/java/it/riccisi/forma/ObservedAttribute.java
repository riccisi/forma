package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Semantic observation of one represented attribute.
 *
 * <p>Construction retains the semantic attribute, representation coordinate,
 * and data source without interpreting the represented value. Interpretation
 * and validation happen when the semantic value is observed.
 *
 * @param <T> semantic value type
 */
@RequiredArgsConstructor
final class ObservedAttribute<T> implements ModelAttribute<T> {

    @NonNull private final Attribute<T> attribute;
    @NonNull private final PropertyReference reference;
    @NonNull private final Data data;

    @Override
    public AttributeName<T> name() {
        return this.attribute.name();
    }

    @Override
    public T value() {
        try {
            return this.attribute.from(new PropertyAt(this.reference, this.data)).value();
        } catch (final BindingReason reason) {
            throw new BindingFailure(
                this.attribute.name(),
                this.reference,
                reason
            );
        }
    }
}