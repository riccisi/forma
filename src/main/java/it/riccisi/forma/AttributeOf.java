package it.riccisi.forma;

import java.util.NoSuchElementException;

/**
 * Semantic attribute identified inside a model.
 *
 * @param <T> semantic value type
 */
public final class AttributeOf<T> implements ModelAttribute<T> {

    private final AttributeName<T> name;
    private final Model model;

    public AttributeOf(final AttributeName<T> name, final Model model) {
        this.name = name;
        this.model = model;
    }

    @Override
    public AttributeName<T> name() {
        return this.name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T value() {
        for (final ModelAttribute<?> attribute : this.model) {
            if (attribute.name().equals(this.name)) {
                return ((ModelAttribute<T>) attribute).value();
            }
        }
        throw new NoSuchElementException(
            "No model attribute exists for the supplied name"
        );
    }
}
