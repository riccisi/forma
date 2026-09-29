package it.riccisi.forma.attribute;

import it.riccisi.forma.Attribute;
import it.riccisi.forma.Field;

/**
 * Base attribute for semantic values interpreted from numeric field values.
 *
 * <p>The field value owns representation-level numeric interpretation, while
 * the attribute establishes the semantic meaning and validity of that number.
 *
 * @param <T> semantic value type
 */
public abstract class NumberAttribute<T> implements Attribute<T> {

    @Override
    public final T valueFrom(final Field field) {
        return this.interpret(field.value().asNumber());
    }

    /**
     * Interprets a represented number as a semantic value.
     *
     * @param value numeric representation
     * @return semantic value
     */
    protected abstract T interpret(Number value);
}
