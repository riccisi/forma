package it.riccisi.forma;

import lombok.RequiredArgsConstructor;
import org.cactoos.Text;
import org.cactoos.text.TextOf;

/**
 * Property value represented fundamentally as a number.
 */
@RequiredArgsConstructor
public final class NumberValue implements PropertyValue {

    private final Number value;

    @Override
    public Text asText() {
        return new TextOf(this.value.toString());
    }

    @Override
    public Number asNumber() {
        return this.value;
    }
}
