package it.riccisi.forma;

/**
 * A typed semantic observation exposed by a model.
 *
 * <p>The attribute identity is available without interpreting represented data.
 * Requesting the value establishes the semantic interpretation and may fail
 * when represented state cannot satisfy it.
 *
 * @param <T> semantic value type
 */
public interface ModelAttribute<T> {

    AttributeName<T> name();

    T value();
}
