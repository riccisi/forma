package it.riccisi.forma;

import org.cactoos.Text;

/**
 * Base attribute for semantic values interpreted from textual property values.
 *
 * <p>The property value owns representation-level conversions, while the
 * attribute establishes semantic meaning and validity.
 *
 * @param <T> semantic value type
 */
public abstract class TextAttribute<T> implements Attribute<T> {

    @Override
    public final ModelAttribute<T> from(final Property property) {
        return this.bind(property.value().asText());
    }

    /**
     * Interprets represented text as a semantic value.
     *
     * @param value textual representation
     * @return successfully bound model attribute
     */
    protected abstract ModelAttribute<T> bind(Text value);
}
