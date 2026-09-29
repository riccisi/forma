package it.riccisi.forma.model;

import it.riccisi.forma.AttributeName;
import it.riccisi.forma.AttributeValue;
import it.riccisi.forma.Model;
import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.scalar.FirstOf;
import org.cactoos.scalar.Mapped;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

/** Attribute value identified by its semantic name inside a model. */
@RequiredArgsConstructor
public final class AttributeValueAt<T> implements AttributeValue<T> {

    @NonNull private final Scalar<AttributeValue<T>> attribute;

    @SuppressWarnings("unchecked")
    public AttributeValueAt(final AttributeName<T> name, final Model model) {
        this(new Sticky<>(new Mapped<>(
            item -> (AttributeValue<T>) item,
            new FirstOf<>(
                item -> item.name().equals(name),
                model,
                () -> {
                    throw new NoSuchElementException(
                        String.format(
                            "No attribute value exists for the supplied name: %s",
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
    public T value() {
        return this.attribute().value();
    }

    private AttributeValue<T> attribute() {
        return new Unchecked<>(this.attribute).value();
    }
}
