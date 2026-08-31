package it.riccisi.forma;

/**
 * Base attribute for semantic values interpreted from textual properties.
 *
 * <p>The representation capability check is centralized in the textual semantic
 * family instead of being repeated by every concrete textual attribute or
 * represented by a separate witness object.
 *
 * @param <T> semantic value type
 */
public abstract class TextAttribute<T> implements Attribute<T> {

    @Override
    public final ModelAttribute<T> bind(final Property property) {
        if (!(property instanceof TextProperty text)) {
            throw new IllegalArgumentException("A textual property is required");
        }
        return this.bind(text);
    }

    /**
     * Interprets a textual represented property as a semantic value.
     *
     * @param property textual property
     * @return successfully bound model attribute
     */
    protected abstract ModelAttribute<T> bind(TextProperty property);
}
