package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;
import org.cactoos.Text;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

/**
 * Contract for representation-level Data composition.
 */
final class MergedDataContractTest {

    @Test
    void exposesTheUnionOfBothRepresentations() {
        final Data merged = new MergedData(
            new DataOf(
                property("id", "42"),
                property("email", "old@example.com"),
                property("description", "preserved")
            ),
            new DataOf(
                property("email", "new@example.com"),
                property("status", "ACTIVE")
            )
        );

        assertEquals(4L, StreamSupport.stream(merged.spliterator(), false).count());
        this.assertValue(merged, "id", "42");
        this.assertValue(merged, "email", "new@example.com");
        this.assertValue(merged, "description", "preserved");
        this.assertValue(merged, "status", "ACTIVE");
    }

    @Test
    void doesNotObserveValuesWhileMergingOrIterating() {
        final AtomicBoolean base = new AtomicBoolean();
        final AtomicBoolean overlay = new AtomicBoolean();
        final Data merged = new MergedData(
            new DataOf(observed("email", "old@example.com", base)),
            new DataOf(observed("email", "new@example.com", overlay))
        );

        final Property email = merged.iterator().next();

        assertFalse(base.get());
        assertFalse(overlay.get());
        assertEquals("new@example.com", email.value().asText().asString());
        assertFalse(base.get());
        assertEquals(true, overlay.get());
    }

    @Test
    void fallsBackToBaseWhenOverlayDoesNotRepresentCoordinate() {
        final AtomicBoolean base = new AtomicBoolean();
        final PropertyReference description = new NamedReference("description");
        final Data merged = new MergedData(
            new DataOf(observed("description", "preserved", base)),
            new DataOf(property("status", "ACTIVE"))
        );

        final Property property = new PropertyAt(description, merged);

        assertFalse(base.get());
        assertEquals("preserved", property.value().asText().asString());
        assertEquals(true, base.get());
    }

    private void assertValue(
        final Data data,
        final String name,
        final String expected
    ) {
        final Property property = new PropertyAt(new NamedReference(name), data);
        assertInstanceOf(MergedProperty.class, property);
        assertEquals(expected, property.value().asText().asString());
    }

    private static Property property(final String name, final String value) {
        return new ValueProperty(
            new NamedReference(name),
            new TextValue(new TextOf(value))
        );
    }

    private static Property observed(
        final String name,
        final String value,
        final AtomicBoolean interpreted
    ) {
        return new ValueProperty(
            new NamedReference(name),
            new ObservedValue(
                new TextValue(new TextOf(value)),
                interpreted
            )
        );
    }

    private record NamedReference(String value) implements PropertyReference {
    }

    private static final class DataOf implements Data {

        private final Iterable<Property> properties;

        private DataOf(final Property... properties) {
            this.properties = List.of(properties);
        }

        @Override
        public Iterator<Property> iterator() {
            return this.properties.iterator();
        }
    }

    private static final class ObservedValue implements PropertyValue {

        private final PropertyValue origin;
        private final AtomicBoolean interpreted;

        private ObservedValue(
            final PropertyValue origin,
            final AtomicBoolean interpreted
        ) {
            this.origin = origin;
            this.interpreted = interpreted;
        }

        @Override
        public Text asText() {
            this.interpreted.set(true);
            return this.origin.asText();
        }

        @Override
        public Number asNumber() {
            this.interpreted.set(true);
            return this.origin.asNumber();
        }
    }
}
