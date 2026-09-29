package it.riccisi.forma.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.riccisi.forma.Data;
import it.riccisi.forma.Field;
import it.riccisi.forma.field.FieldAt;
import it.riccisi.forma.field.NamedReference;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import org.cactoos.Input;
import org.cactoos.io.InputOf;
import org.cactoos.text.UncheckedText;
import org.junit.jupiter.api.Test;

final class JsonDataContractTest {

    @Test
    void readsStringMember() {
        assertThat(
            new UncheckedText(
                new FieldAt(
                    new NamedReference("id"),
                    new JsonData("{\"id\":\"S-42\"}")
                ).value().asText()
            ).asString(),
            equalTo("S-42")
        );
    }

    @Test
    void readsNumberMember() {
        assertThat(
            new FieldAt(
                new NamedReference("age"),
                new JsonData("{\"age\":42}")
            ).value().asNumber().intValue(),
            equalTo(42)
        );
    }

    @Test
    void readsNumberMemberAsText() {
        assertThat(
            text(new JsonData("{\"age\":42}"), "age"),
            equalTo("42")
        );
    }

    @Test
    void readsNumericStringAsNumber() {
        assertThat(
            new FieldAt(
                new NamedReference("age"),
                new JsonData("{\"age\":\"42\"}")
            ).value().asNumber().intValue(),
            equalTo(42)
        );
    }

    @Test
    void exposesNamedFields() {
        assertThat(
            new JsonData("{\"status\":\"ACTIVE\"}").iterator().next().reference(),
            equalTo(new NamedReference("status"))
        );
    }

    @Test
    void stringConstructionIsRepeatable() {
        final Data data = new JsonData("{\"name\":\"Ada\"}");

        assertThat(
            String.format(
                "%s:%s",
                text(data, "name"),
                text(data, "name")
            ),
            equalTo("Ada:Ada")
        );
    }

    @Test
    void constructionDoesNotReadInput() {
        final AtomicInteger reads = new AtomicInteger();

        new JsonData((Input) () -> {
            reads.incrementAndGet();
            return new InputOf("{}").stream();
        });

        assertThat(reads.get(), equalTo(0));
    }

    @Test
    void eachIteratorPerformsOneRead() {
        final AtomicInteger reads = new AtomicInteger();
        final Data data = new JsonData((Input) () -> new InputOf(
            String.format("{\"value\":\"%s\"}", reads.incrementAndGet())
        ).stream());

        assertThat(
            String.format("%s:%s", text(data, "value"), text(data, "value")),
            equalTo("1:2")
        );
    }

    @Test
    void closesInputBeforeReturningIterator() {
        final ClosingInput input = new ClosingInput("{\"id\":\"S-42\"}");

        final Iterator<Field> fields = new JsonData(input).iterator();

        assertThat(
            String.format("%s:%s", input.closed(), fields.hasNext()),
            equalTo("true:true")
        );
    }

    @Test
    void failsExplicitlyForInvalidJson() {
        assertThat(
            assertThrows(JsonDataException.class, () -> new JsonData("{").iterator())
                .getCause(),
            instanceOf(InvalidJsonException.class)
        );
    }

    @Test
    void failsExplicitlyForNonObjectRoot() {
        assertThat(
            assertThrows(JsonDataException.class, () -> new JsonData("[1]").iterator())
                .getCause(),
            instanceOf(NonObjectJsonException.class)
        );
    }

    @Test
    void failsExplicitlyForUnsupportedMember() {
        assertThrows(
            UnsupportedJsonValueException.class,
            () -> text(new JsonData("{\"active\":true}"), "active")
        );
    }

    private static String text(final Data data, final String name) {
        return new UncheckedText(
            new FieldAt(new NamedReference(name), data).value().asText()
        ).asString();
    }

    private static final class ClosingInput implements Input {

        private final String json;
        private boolean closed;

        private ClosingInput(final String json) {
            this.json = json;
        }

        boolean closed() {
            return this.closed;
        }

        @Override
        public InputStream stream() {
            return new ByteArrayInputStream(
                this.json.getBytes(StandardCharsets.UTF_8)
            ) {
                @Override
                public void close() throws IOException {
                    ClosingInput.this.closed = true;
                    super.close();
                }
            };
        }
    }
}
