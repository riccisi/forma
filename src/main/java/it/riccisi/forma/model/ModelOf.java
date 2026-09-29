package it.riccisi.forma.model;

import it.riccisi.forma.AttributeName;
import it.riccisi.forma.Data;
import it.riccisi.forma.FieldMapping;
import it.riccisi.forma.FieldReference;
import it.riccisi.forma.Metadata;
import it.riccisi.forma.Model;
import it.riccisi.forma.attribute.AttributeAt;
import it.riccisi.forma.exception.AttributeValueException;
import it.riccisi.forma.exception.MissingFieldException;
import it.riccisi.forma.exception.RejectedValueException;
import it.riccisi.forma.exception.UnparsableValueException;
import it.riccisi.forma.field.FieldAt;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Model relating metadata to represented data through a field mapping.
 *
 * <p>The model has no structure beyond the structure already described by its
 * metadata. It establishes one semantic value at a time when requested.
 */
@RequiredArgsConstructor
public final class ModelOf implements Model {

    @NonNull private final Metadata metadata;
    @NonNull private final Data data;
    @NonNull private final FieldMapping mapping;

    @Override
    public Metadata metadata() {
        return this.metadata;
    }

    @Override
    public Data data() {
        return this.data;
    }

    @Override
    public <T> T valueOf(final AttributeName<T> name) {
        final FieldReference reference = this.mapping.reference(name);
        try {
            return new AttributeAt<T>(name, this.metadata).valueFrom(
                new FieldAt(reference, this.data)
            );
        } catch (
            final MissingFieldException
                | UnparsableValueException
                | RejectedValueException err
        ) {
            throw new AttributeValueException(name, reference, err);
        }
    }
}
