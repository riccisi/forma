package it.riccisi.forma.json;

import it.riccisi.forma.Data;
import it.riccisi.forma.Field;
import it.riccisi.forma.field.FieldOf;
import it.riccisi.forma.field.NamedReference;
import java.io.InputStream;
import java.util.Iterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Input;
import org.cactoos.io.InputOf;
import org.cactoos.io.Sticky;
import org.cactoos.iterator.Mapped;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * JSON object represented as Forma data.
 *
 * <p>Construction preserves the supplied {@link Input} semantics. Each
 * iteration reads one complete JSON object, closes the source, and then returns
 * fields materialized from that reading.
 */
@RequiredArgsConstructor
public final class JsonData implements Data {

    @NonNull private final Input input;

    public JsonData(@NonNull final String json) {
        this(new Sticky(new InputOf(json)));
    }

    @Override
    public Iterator<Field> iterator() {
        try (InputStream stream = this.input.stream()) {
            final JsonNode root = new ObjectMapper().readTree(stream);
            if (!root.isObject()) {
                throw new NonObjectJsonException();
            }
            return new Mapped<>(
                prop -> new FieldOf(
                    new NamedReference(prop.getKey()),
                    new JsonFieldValue(prop.getKey(), prop.getValue())
                ),
                root.properties().iterator()
            );
        } catch (final JacksonException err) {
            throw new JsonDataException(new InvalidJsonException(err));
        } catch (final Exception err) {
            throw new JsonDataException(err);
        }
    }
}
