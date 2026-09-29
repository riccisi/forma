package it.riccisi.forma;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import it.riccisi.forma.data.DataOf;
import it.riccisi.forma.data.MergedData;
import it.riccisi.forma.field.FieldAt;
import it.riccisi.forma.field.FieldOf;
import it.riccisi.forma.field.NamedReference;
import it.riccisi.forma.field.MergedField;
import it.riccisi.forma.field.TextValue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;
import org.cactoos.Text;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.junit.jupiter.api.Test;

/** Contract for representation-level data composition. */
final class MergedDataContractTest {

    @Test
    void exposesUnionOfCoordinates() {
        assertThat(
            new ListOf<>(
                new MergedData(
                    data(field("id", "42"), field("email", "old")),
                    data(field("email", "new"), field("status", "ACTIVE"))
                )
            ).stream().map(item -> item.reference().toString()).toList(),
            containsInAnyOrder("id", "email", "status")
        );
    }

    @Test
    void overlayValueTakesPrecedence() {
        assertThat(
            new ValueOf(
                new MergedData(
                    data(field("email", "old")),
                    data(field("email", "new"))
                ),
                "email"
            ).value(),
            equalTo("new")
        );
    }

    @Test
    void baseValueRemainsWhenOverlayDoesNotRepresentCoordinate() {
        assertThat(
            new ValueOf(
                new MergedData(
                    data(field("description", "preserved")),
                    data(field("status", "ACTIVE"))
                ),
                "description"
            ).value(),
            equalTo("preserved")
        );
    }

    @Test
    void iterationDoesNotReadValues() {
        assertThat(new LazyMerge().readAfterIteration(), is(false));
    }

    @Test
    void requestedValueReadsOnlySelectedSource() {
        assertThat(new LazyMerge().reads(), equalTo("false:true"));
    }

    @Test
    void mergedFieldDoesNotInspectBaseWhenOverlayMatches() {
        assertThat(new OrderedField().baseIterated(), is(false));
    }

    private static Data data(final Field... fields) {
        return new DataOf(fields);
    }

    private static Field field(final String name, final String value) {
        return new FieldOf(
            new NamedReference(name),
            new TextValue(new TextOf(value))
        );
    }

    private static final class ValueOf {

        private final Data data;
        private final String name;

        private ValueOf(final Data data, final String name) {
            this.data = data;
            this.name = name;
        }

        String value() {
            return new UncheckedText(
                new FieldAt(new NamedReference(this.name), this.data)
                    .value().asText()
            ).asString();
        }
    }

    private static final class LazyMerge {

        private final AtomicBoolean base = new AtomicBoolean();
        private final AtomicBoolean overlay = new AtomicBoolean();
        private final Data merged = new MergedData(
            data(readField("email", "old", this.base)),
            data(readField("email", "new", this.overlay))
        );

        boolean readAfterIteration() {
            this.merged.iterator().next();
            return this.base.get() || this.overlay.get();
        }

        String reads() {
            new ValueOf(this.merged, "email").value();
            return String.format("%s:%s", this.base.get(), this.overlay.get());
        }
    }

    private static final class OrderedField {

        private final AtomicBoolean base = new AtomicBoolean();

        boolean baseIterated() {
            new MergedField(
                new NamedReference("email"),
                () -> {
                    this.base.set(true);
                    return data(field("email", "old")).iterator();
                },
                data(field("email", "new"))
            ).value();
            return this.base.get();
        }
    }

    private static Field readField(
        final String name,
        final String value,
        final AtomicBoolean read
    ) {
        return new FieldOf(
            new NamedReference(name),
            new ReadValue(new TextValue(new TextOf(value)), read)
        );
    }

    private static final class ReadValue implements FieldValue {

        private final FieldValue origin;
        private final AtomicBoolean read;

        private ReadValue(final FieldValue origin, final AtomicBoolean read) {
            this.origin = origin;
            this.read = read;
        }

        @Override
        public Text asText() {
            this.read.set(true);
            return this.origin.asText();
        }

        @Override
        public Number asNumber() {
            this.read.set(true);
            return this.origin.asNumber();
        }
    }
}
