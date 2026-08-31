package it.riccisi.forma;

import org.cactoos.Text;

/**
 * Base attribute for semantic values interpreted from textual properties.
 *
 * <p>The attribute itself is the representation-level interpreter requested from
 * a property. This keeps concrete property types out of semantic attributes and
 * avoids runtime type inspection.
 *
 * @param <T> semantic value type
 */
public abstract class TextAttribute<T>
    implements Attribute<T>, PropertyValue<ModelAttribute<T>> {

    @Override
    public final ModelAttribute<T> bind(final Property property) {
        return property.describe(this);
    }

    @Override
    public abstract ModelAttribute<T> text(Text value);
}
