package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.cactoos.Text;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

final class ModelContractTest {

    @Test
    void sameSemanticAttributeReadsHeterogeneousRepresentations() {
        final AttributeName<Email> name = new EmailName();
        final Attribute<TextProperty, Email> email = new EmailAttribute(name);

        assertEquals(
            "alice@example.com",
            email.bind(new JsonStringProperty("alice@example.com")).value().toString()
        );
        assertEquals(
            "bob@example.com",
            email.bind(new MapStringProperty("bob@example.com")).value().toString()
        );
        assertEquals(
            "carol@example.com",
            email.bind(new PojoStringProperty("carol@example.com")).value().toString()
        );
    }

    @Test
    void metadataCapturesTypedCapabilityWithoutCasting() {
        final AttributeName<Email> name = new EmailName();
        final Attribute<TextProperty, Email> email = new EmailAttribute(name);
        final Metadata metadata = new SingleAttributeMetadata(email);
        final Data data = new SinglePropertyData(
            new JsonStringProperty("alice@example.com")
        );

        final Model model = metadata.bind(data);

        assertSame(metadata, model.metadata());
        assertSame(data, model.data());
        assertEquals("alice@example.com", model.value(name).toString());
    }

    private record EmailName() implements AttributeName<Email> {
    }

    private record Email(Text text) {

        @Override
        public String toString() {
            try {
                return this.text.asString();
            } catch (final Exception err) {
                throw new IllegalStateException(err);
            }
        }
    }

    private record JsonStringProperty(Text text) implements TextProperty {
        private JsonStringProperty(final String text) {
            this(new TextOf(text));
        }
    }

    private record MapStringProperty(Text text) implements TextProperty {
        private MapStringProperty(final String text) {
            this(new TextOf(text));
        }
    }

    private record PojoStringProperty(Text text) implements TextProperty {
        private PojoStringProperty(final String text) {
            this(new TextOf(text));
        }
    }

    private record TextCapability() implements PropertyCapability<TextProperty> {

        @Override
        public TextProperty require(final Property property) {
            if (!(property instanceof TextProperty text)) {
                throw new IllegalArgumentException("A textual property is required");
            }
            return text;
        }
    }

    private record EmailAttribute(AttributeName<Email> name)
        implements Attribute<TextProperty, Email> {

        @Override
        public PropertyCapability<TextProperty> capability() {
            return new TextCapability();
        }

        @Override
        public ModelAttribute<Email> bind(final TextProperty property) {
            return new BoundModelAttribute<>(this.name, new Email(property.text()));
        }
    }

    private record SinglePropertyData(Property property) implements Data {

        @Override
        public Iterator<Property> iterator() {
            return List.of(this.property).iterator();
        }
    }

    private record SingleAttributeMetadata(Attribute<?, ?> attribute) implements Metadata {

        @Override
        public Model bind(final Data data) {
            final ModelAttribute<?> bound = SingleAttributeMetadata.bound(
                this.attribute,
                data.iterator().next()
            );
            return new BoundModel(
                this,
                data,
                Map.of(bound.name(), bound.value())
            );
        }

        @Override
        public Iterator<Attribute<?, ?>> iterator() {
            return List.<Attribute<?, ?>>of(this.attribute).iterator();
        }

        private static <P extends Property, T> ModelAttribute<T> bound(
            final Attribute<P, T> attribute,
            final Property property
        ) {
            return attribute.bind(attribute.capability().require(property));
        }
    }

    private record BoundModelAttribute<T>(
        AttributeName<T> name,
        T value
    ) implements ModelAttribute<T> {
    }

    private record BoundModel(
        Metadata metadata,
        Data data,
        Map<AttributeName<?>, Object> values
    ) implements Model {

        @Override
        @SuppressWarnings("unchecked")
        public <T> T value(final AttributeName<T> name) {
            return (T) this.values.get(name);
        }
    }
}
