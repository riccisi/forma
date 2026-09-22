package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void overlaysMatchingPropertiesAndPreservesTheOthers() {
        final Property id = property("id", "42");
        final Property oldmail = property("email", "old@example.com");
        final Property description = property("description", "preserved");
        final Property newmail = property("email", "new@example.com");
        final Property status = property("status", "ACTIVE");

        final Data merged = new MergedData(
            new DataOf(id, oldmail, description),
            new DataOf(newmail, status)
        );

        final Iterator<Property> properties = merged.iterator();
        assertSame(id, properties.next());
        final Property email = properties.next();
        assertInstanceOf(MergedProperty.class, email);
        assertEquals("new@example.com", email.value().asText().asString());
        assertSame(description, properties.next());
        assertSame(status, properties.next());
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
    void rejectsPropertiesAtDifferentCoordinates() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MergedProperty(
                property("email", "old@example.com"),
                property("status", "ACTIVE")
            )
        );
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
