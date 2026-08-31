package it.riccisi.forma;

import org.cactoos.Text;

/**
 * A representation-level interpretation requested from a property.
 *
 * <p>The spike deliberately starts with the smallest useful vocabulary. Further
 * represented value forms should be added only when concrete heterogeneous data
 * representations require them.
 *
 * @param <T> interpreted result type
 */
public interface PropertyValue<T> {

    /**
     * Interprets represented textual information.
     *
     * @param value textual representation
     * @return interpreted result
     */
    T text(Text value);
}
