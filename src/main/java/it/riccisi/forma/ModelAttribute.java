package it.riccisi.forma;

/**
 * Evidence that a concrete property satisfies a semantic attribute.
 *
 * <p>A model attribute is produced by successful binding. Its value has already
 * crossed the representation-to-semantics boundary and can be trusted as the
 * value of the returned {@link AttributeName}.
 *
 * @param <T> semantic value type
 */
public interface ModelAttribute<T> {

    /**
     * Returns the typed semantic identity of this bound value.
     *
     * @return attribute identity
     */
    AttributeName<T> name();

    /**
     * Returns the interpreted and accepted semantic value.
     *
     * @return semantic value
     */
    T value();
}
