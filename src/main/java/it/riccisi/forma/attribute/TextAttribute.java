package it.riccisi.forma.attribute;

import it.riccisi.forma.Attribute;
import it.riccisi.forma.Field;
import org.cactoos.Text;

/**
 * Base attribute for semantic values interpreted from textual field values.
 *
 * <p>The field value owns representation-level conversions, while the
 * attribute establishes semantic meaning and validity.
 *
 * @param <T> semantic value type
 */
public abstract class TextAttribute<T> implements Attribute<T> {

    @Override
    public final T valueFrom(final Field field) {
        return this.interpret(field.value().asText());
    }

    /**
     * Interprets represented text as a semantic value.
     *
     * @param value textual representation
     * @return semantic value
     */
    protected abstract T interpret(Text value);
}
