package it.riccisi.forma;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.cactoos.Text;

/**
 * Property value represented fundamentally as text.
 */
@RequiredArgsConstructor
public final class TextValue implements PropertyValue {

    private final Text value;

    @Override
    public Text asText() {
        return this.value;
    }

    @Override
    public Number asNumber() {
        try {
            return new BigDecimal(this.value.asString());
        } catch (final Exception err) {
            throw new UninterpretableValue(err);
        }
    }
}
