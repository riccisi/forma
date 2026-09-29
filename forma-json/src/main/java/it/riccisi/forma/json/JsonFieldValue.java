package it.riccisi.forma.json;

import it.riccisi.forma.FieldValue;
import it.riccisi.forma.field.NumberValue;
import it.riccisi.forma.field.TextValue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import tools.jackson.databind.JsonNode;

/** Field value backed by a JSON member. */
@RequiredArgsConstructor
final class JsonFieldValue implements FieldValue {

    @NonNull private final Scalar<FieldValue> value;

    public JsonFieldValue(@NonNull String key, @NonNull JsonNode node) {
        this(new Sticky<>(() -> {
            if (node.isString()) {
                return new TextValue(node::stringValue);
            }
            if (node.isNumber()) {
                return new NumberValue(node.numberValue());
            }
            throw new UnsupportedJsonValueException(key);
        }));
    }

    @Override
    public Text asText() {
        return this.fieldValue().asText();
    }

    @Override
    public Number asNumber() {
        return this.fieldValue().asNumber();
    }

    private FieldValue fieldValue() {
        return new Unchecked<>(this.value).value();
    }
}
