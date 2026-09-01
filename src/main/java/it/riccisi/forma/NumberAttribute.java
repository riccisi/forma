package it.riccisi.forma;

/**
 * Base attribute for semantic values interpreted from numeric property values.
 *
 * <p>The property value owns representation-level numeric interpretation, while
 * the attribute establishes the semantic meaning and validity of that number.
 *
 * @param <T> semantic value type
 */
public abstract class NumberAttribute<T> implements Attribute<T> {

    @Override
    public final ModelAttribute<T> bind(final Property property) {
        return this.bind(property.value().asNumber());
    }

    /**
     * Interprets a represented number as a semantic value.
     *
     * @param value numeric representation
     * @return successfully bound model attribute
     */
    protected abstract ModelAttribute<T> bind(Number value);
}
