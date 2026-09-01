package it.riccisi.forma;

import lombok.NonNull;
import org.cactoos.Text;
import org.cactoos.text.TextOf;

/**
 * Valid textual attribute name with value semantics.
 *
 * @param <T> semantic value type
 */
public final class AttributeNameOf<T> implements AttributeName<T> {

    private final String value;

    public AttributeNameOf(final String value) {
        this.value = AttributeNameOf.valid(value);
    }

    public AttributeNameOf(@NonNull final Text value) {
        this(AttributeNameOf.string(value));
    }

    @Override
    public String asString() {
        return this.value;
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof AttributeName<?>
            && this.value.equals(AttributeNameOf.string((AttributeName<?>) other));
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.value;
    }

    private static String valid(@NonNull final String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("Attribute name cannot be blank");
        }
        if (!value.equals(value.strip())) {
            throw new IllegalArgumentException(
                "Attribute name cannot have surrounding whitespace"
            );
        }
        return value;
    }

    private static String string(final Text text) {
        try {
            return text.asString();
        } catch (final Exception err) {
            throw new IllegalArgumentException(
                "Attribute name text cannot be read",
                err
            );
        }
    }
}
