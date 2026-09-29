package it.riccisi.forma.attribute;

import it.riccisi.forma.AttributeName;
import it.riccisi.forma.exception.UnparsableValueException;
import java.math.BigDecimal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Semantic integer interpreted from a numeric field value.
 */
@RequiredArgsConstructor
public final class IntegerAttribute extends NumberAttribute<Integer> {

    @NonNull private final AttributeName<Integer> name;

    @Override
    public AttributeName<Integer> name() {
        return this.name;
    }

    @Override
    protected Integer interpret(final Number value) {
        try {
            return new BigDecimal(value.toString()).intValueExact();
        } catch (final ArithmeticException | NumberFormatException err) {
            throw new UnparsableValueException(err);
        }
    }
}
