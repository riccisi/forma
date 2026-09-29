package it.riccisi.forma.model;

import it.riccisi.forma.Attribute;
import it.riccisi.forma.AttributeName;
import it.riccisi.forma.AttributeValue;
import it.riccisi.forma.Data;
import it.riccisi.forma.FieldMapping;
import it.riccisi.forma.FieldReference;
import it.riccisi.forma.exception.AttributeValueException;
import it.riccisi.forma.exception.MissingFieldException;
import it.riccisi.forma.exception.RejectedValueException;
import it.riccisi.forma.exception.UnparsableValueException;
import it.riccisi.forma.field.FieldAt;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Semantic value of one attribute over represented data. */
@RequiredArgsConstructor
final class AttributeValueOf<T> implements AttributeValue<T> {

    @NonNull private final Attribute<T> attribute;
    @NonNull private final FieldMapping mapping;
    @NonNull private final Data data;

    @Override
    public AttributeName<T> name() {
        return this.attribute.name();
    }

    @Override
    public T value() {
        final FieldReference reference =
            this.mapping.reference(this.attribute.name());
        try {
            return this.attribute.valueFrom(new FieldAt(reference, this.data));
        } catch (
            final MissingFieldException
                | UnparsableValueException
                | RejectedValueException err
        ) {
            throw new AttributeValueException(
                this.attribute.name(),
                reference,
                err
            );
        }
    }
}
