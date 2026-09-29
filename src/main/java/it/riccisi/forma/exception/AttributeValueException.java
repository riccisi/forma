package it.riccisi.forma.exception;

import it.riccisi.forma.AttributeName;
import it.riccisi.forma.FieldReference;
import lombok.NonNull;

/**
 * Raised when the semantic value of an attribute cannot be established from
 * represented data.
 */
public final class AttributeValueException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    @NonNull private final AttributeName<?> attribute;
    @NonNull private final FieldReference field;

    public AttributeValueException(
        @NonNull final AttributeName<?> attribute,
        @NonNull final FieldReference field,
        @NonNull final RuntimeException cause
    ) {
        super("Unable to establish the attribute value", cause);
        this.attribute = attribute;
        this.field = field;
    }

    public AttributeName<?> attribute() {
        return this.attribute;
    }

    public FieldReference field() {
        return this.field;
    }
}
