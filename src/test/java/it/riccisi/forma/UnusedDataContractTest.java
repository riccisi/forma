package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;
import org.cactoos.Text;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

/**
 * Contract proving that model construction interprets only information required
 * by metadata while preserving all represented data.
 */
final class UnusedDataContractTest {

    @Test
    void preservesUnusedPropertyWithoutInterpretingItsValue() {
        final AttributeName<String> id = new AttributeNameOf<>("id");
        final AttributeName<String> status = new AttributeNameOf<>("status");
        final PropertyReference idref = new NamedReference("id");
        final PropertyReference statusref = new NamedReference("status");
        final PropertyReference description = new NamedReference("description");
        final AtomicBoolean interpreted = new AtomicBoolean();
        final Data data = new DataOf(
            new ValueProperty(idref, new TextValue(new TextOf("42"))),
            new ValueProperty(statusref, new TextValue(new TextOf("ACTIVE"))),
            new ValueProperty(
                description,
                new ObservedValue(
                    new TextValue(new TextOf("Preserved source data")),
                    interpreted
                )
            )
        );
        final Metadata metadata = new MetadataOf(
            new StringAttribute(id),
            new StringAttribute(status)
        );

        final Model model = new ModelOf(
            metadata,
            data,
            new SameNameMapping(NamedReference::new)
        );

        assertFalse(interpreted.get());
        assertSame(data, model.data());
        assertEquals(
            2L,
            StreamSupport.stream(model.spliterator(), false).count()
        );
        assertEquals(
            "Preserved source data",
            new PropertyAt(description, model.data()).value().asText().asString()
        );
        assertEquals(true, interpreted.get());
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
