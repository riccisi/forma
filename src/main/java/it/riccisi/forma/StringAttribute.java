package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Text;
import org.cactoos.text.UncheckedText;

/**
 * Semantic string interpreted from a textual property value.
 */
@RequiredArgsConstructor
public final class StringAttribute extends TextAttribute<String> {

    @NonNull private final AttributeName<String> name;

    @Override
    public AttributeName<String> name() {
        return this.name;
    }

    @Override
    protected String interpret(final Text value) {
        return new UncheckedText(value).asString();
    }
}
