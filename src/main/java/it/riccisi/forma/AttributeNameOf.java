package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Text;
import org.cactoos.text.TextOf;

/**
 * Attribute identity with a conventional textual name.
 *
 * <p>Identity remains object identity; equal text does not make two instances
 * the same semantic attribute.
 *
 * @param <T> semantic value type
 */
@RequiredArgsConstructor
public final class AttributeNameOf<T> implements AttributeName<T> {

    @NonNull private final Text text;

    public AttributeNameOf(final String text) {
        this(new TextOf(text));
    }

    @Override
    public Text text() {
        return this.text;
    }
}
