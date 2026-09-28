package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Semantic observation of one represented attribute.
 *
 * <p>Construction retains the semantic attribute, property mapping, and data
 * source without resolving a representation coordinate or interpreting its
 * value. Mapping, interpretation, and validation happen when the semantic value
 * is observed.
 *
 * @param <T> semantic value type
 */
@RequiredArgsConstructor
final class ObservedAttribute<T> implements ModelAttribute<T> {

    @NonNull private final Attribute<T> attribute;
    @NonNull private final PropertyMapping mapping;
    @NonNull private final Data data;

    @Override
    public AttributeName<T> name() {
        return this.attribute.name();
    }

    @Override
    public T value() {
        final PropertyReference reference =
            this.mapping.property(this.attribute.name());
        try {
            return this.attribute.valueFrom(new PropertyAt(reference, this.data));
        } catch (final BindingReason reason) {
            throw new BindingFailure(
                this.attribute.name(),
                reference,
                reason
            );
        }
    }
}