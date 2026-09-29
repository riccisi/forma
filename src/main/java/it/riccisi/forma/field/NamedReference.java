package it.riccisi.forma.field;

import it.riccisi.forma.FieldReference;
import lombok.NonNull;
import org.cactoos.Text;
import org.cactoos.text.UncheckedText;

/** Field reference identified by an exact textual name. */
public final class NamedReference implements FieldReference {

    private final String name;

    public NamedReference(@NonNull final String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Field reference name cannot be blank");
        }
        if (!name.equals(name.strip())) {
            throw new IllegalArgumentException(
                "Field reference name cannot have surrounding whitespace"
            );
        }
        this.name = name;
    }

    public NamedReference(@NonNull final Text name) {
        this(new UncheckedText(name).asString());
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof NamedReference
            && this.name.equals(((NamedReference) other).name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
