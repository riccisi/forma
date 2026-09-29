package it.riccisi.forma;

import org.cactoos.Text;

/**
 * Representation-neutral value exposed by a {@link Field}.
 *
 * <p>The contract intentionally contains only a small set of fundamental value
 * forms. Concrete values may provide conversions when the represented
 * information permits it.
 */
public interface FieldValue {

    Text asText();

    Number asNumber();
}
