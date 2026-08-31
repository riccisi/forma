package it.riccisi.forma;

import org.cactoos.Text;

/**
 * Representation-neutral value exposed by a {@link Property}.
 *
 * <p>The contract intentionally contains only a small set of fundamental value
 * interpretations. Concrete value objects may support conversions between those
 * forms when the represented information permits it.
 */
public interface PropertyValue {

    /**
     * Interprets this represented value as text.
     *
     * @return textual interpretation
     */
    Text asText();

    /**
     * Interprets this represented value as a number.
     *
     * @return numeric interpretation
     */
    Number asNumber();
}
