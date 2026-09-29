package it.riccisi.forma.field;

import it.riccisi.forma.FieldValue;
import lombok.RequiredArgsConstructor;
import org.cactoos.Text;
import org.cactoos.text.TextOf;

/** Field value represented fundamentally as a number. */
@RequiredArgsConstructor
public final class NumberValue implements FieldValue {

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
