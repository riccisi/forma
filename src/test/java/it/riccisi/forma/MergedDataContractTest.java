package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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

        final Iterator<Property> properties = merged.iterator();
        final Property id = properties.next();
        final Property email = properties.next();
        final Property description = properties.next();
        final Property status = properties.next();

        assertInstanceOf(MergedProperty.class, id);
        assertEquals("42", id.value().asText().asString());
        assertInstanceOf(MergedProperty.class, email);
        assertEquals("new@example.com", email.value().asText().asString());
        assertInstanceOf(MergedProperty.class, description);
        assertEquals("preserved", description.value().asText().asString());
        assertInstanceOf(MergedProperty.class, status);
        assertEquals("ACTIVE", status.value().asText().asString());
        assertFalse(properties.hasNext());
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
        final Data merged = new MergedData(
            new DataOf(observed("description", "preserved", base)),
            new DataOf(property("status", "ACTIVE"))
        );

        final Property description = merged.iterator().next();

        assertFalse(base.get());
        assertEquals(
            "preserved",
            description.value().asText().asString()
        );
        assertEquals(true, base.get());
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
