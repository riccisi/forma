package it.riccisi.forma;

/**
 * An instance of metadata over represented data.
 *
 * <p>A model relates the semantic structure described by {@link Metadata} to
 * the representation exposed by {@link Data}. It does not introduce a third
 * iterable structure: its structure is already described by its metadata.
 *
 * <p>Construction composes metadata and data without resolving represented
 * values. A semantic value is established only when {@link #valueOf} is
 * requested.
 */
public interface Model {

    Metadata metadata();

    Data data();

    /**
     * Semantic value of the named attribute in this model.
     *
     * @param name semantic attribute name
     * @param <T> semantic value type
     * @return value established from represented data
     */
    <T> T valueOf(AttributeName<T> name);
}
