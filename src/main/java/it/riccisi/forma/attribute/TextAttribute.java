package it.riccisi.forma.attribute;

import it.riccisi.forma.Attribute;
import it.riccisi.forma.Property;
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
    public final T valueFrom(final Property property) {
        return this.interpret(property.value().asText());
    }

    /**
     * Interprets represented text as a semantic value.
     *
     * @param value textual representation
     * @return semantic value
     */
    protected abstract T interpret(Text value);
}
