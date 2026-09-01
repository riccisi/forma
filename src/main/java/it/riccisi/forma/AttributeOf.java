package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.scalar.FirstOf;
import org.cactoos.scalar.Mapped;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

import java.util.NoSuchElementException;

/**
 * Semantic attribute identified inside a model.
 *
 * @param <T> semantic value type
 */
@RequiredArgsConstructor
public final class AttributeOf<T> implements ModelAttribute<T> {

    @NonNull private final Scalar<ModelAttribute<T>> attribute;

    @SuppressWarnings("unchecked")
    public AttributeOf(final AttributeName<T> name, final Model model) {
        this(
            new Sticky<>(
                new Mapped<>(
                    attr -> (ModelAttribute<T>) attr,
                    new FirstOf<>(
                        attribute -> attribute.name().equals(name),
                        model,
                        () -> {
                            throw new NoSuchElementException(
                                "No model attribute exists for the supplied name"
                            );
                        }
                    )
                )
            )
        );
    }

    @Override
    public AttributeName<T> name() {
        return new Unchecked<>(this.attribute).value().name();
    }

    @Override
    public T value() {
        return new Unchecked<>(this.attribute).value().value();
    }
}