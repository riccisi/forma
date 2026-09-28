package it.riccisi.forma.model;

import it.riccisi.forma.Attribute;
import it.riccisi.forma.AttributeName;
import it.riccisi.forma.observation.ObservationFailure;
import it.riccisi.forma.observation.ObservationReason;
import it.riccisi.forma.Data;
import it.riccisi.forma.ModelAttribute;
import it.riccisi.forma.PropertyMapping;
import it.riccisi.forma.PropertyReference;
import it.riccisi.forma.property.PropertyAt;
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
        } catch (final ObservationReason reason) {
            throw new ObservationFailure(
                this.attribute.name(),
                reference,
                reason
            );
        }
    }
}