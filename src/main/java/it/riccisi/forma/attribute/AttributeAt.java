package it.riccisi.forma.attribute;

import it.riccisi.forma.Attribute;
import it.riccisi.forma.AttributeName;
import it.riccisi.forma.Field;
import it.riccisi.forma.Metadata;
import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.scalar.FirstOf;
import org.cactoos.scalar.Mapped;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

/**
 * Attribute identified by its semantic name inside metadata.
 *
 * <p>The lookup itself is modeled as an Attribute, just as {@code FieldAt}
 * models a field lookup inside Data. The metadata is searched only when the
 * represented attribute is needed.
 *
 * @param <T> semantic value type
 */
@RequiredArgsConstructor
public final class AttributeAt<T> implements Attribute<T> {

    @NonNull private final Scalar<Attribute<T>> attribute;

    @SuppressWarnings("unchecked")
    public AttributeAt(final AttributeName<T> name, final Metadata metadata) {
        this(new Sticky<>(new Mapped<>(
            item -> (Attribute<T>) item,
            new FirstOf<>(
                item -> item.name().equals(name),
                metadata,
                () -> {
                    throw new NoSuchElementException(
                        String.format(
                            "No attribute exists for the supplied name: %s",
                            name.asString()
                        )
                    );
                }
            )
        )));
    }

    @Override
    public AttributeName<T> name() {
        return this.attribute().name();
    }

    @Override
    public T valueFrom(final Field field) {
        return this.attribute().valueFrom(field);
    }

    private Attribute<T> attribute() {
        return new Unchecked<>(this.attribute).value();
    }
}
