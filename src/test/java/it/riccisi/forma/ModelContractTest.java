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
    void dataOwnsSemanticToRepresentationMapping() {
        final AttributeName<Email> name = new EmailName();
        final PropertyName field = new NamedProperty("e_mail");
        final Data data = new MappedData(
            Map.of(field, new JsonStringProperty("alice@example.com")),
            new ExplicitMapping(Map.of(name, field))
        );
        final Metadata metadata = new SingleAttributeMetadata(
            new EmailAttribute(name)
        );

        final Model model = metadata.bind(data);

        assertSame(metadata, model.metadata());
        assertSame(data, model.data());
        assertEquals("alice@example.com", model.value(name).toString());
    }

    @Test
    void textualAttributeIsIndependentFromConcreteRepresentation() {
        final AttributeName<Email> name = new EmailName();
        final Attribute<Email> email = new EmailAttribute(name);

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

    private record EmailName() implements AttributeName<Email> {
    }

    private record NamedProperty(String value) implements PropertyName {
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

    private static final class EmailAttribute extends TextAttribute<Email> {

        private final AttributeName<Email> name;

        private EmailAttribute(final AttributeName<Email> name) {
            this.name = name;
        }

        @Override
        public AttributeName<Email> name() {
            return this.name;
        }

        @Override
        protected ModelAttribute<Email> bind(final TextProperty property) {
            return new BoundModelAttribute<>(this.name, new Email(property.text()));
        }
    }

    private record ExplicitMapping(
        Map<AttributeName<?>, PropertyName> properties
    ) implements PropertyMapping {

        @Override
        public PropertyName property(final AttributeName<?> attribute) {
            final PropertyName property = this.properties.get(attribute);
            if (property == null) {
                throw new IllegalArgumentException("No property mapping for attribute");
            }
            return property;
        }
    }

    private record MappedData(
        Map<PropertyName, Property> properties,
        PropertyMapping mapping
    ) implements Data {

        @Override
        public Property property(final AttributeName<?> name) {
            final Property property = this.properties.get(this.mapping.property(name));
            if (property == null) {
                throw new IllegalArgumentException("Mapped property does not exist");
            }
            return property;
        }

        @Override
        public Iterator<Property> iterator() {
            return this.properties.values().iterator();
        }
    }

    private record SingleAttributeMetadata(Attribute<?> attribute) implements Metadata {

        @Override
        public Model bind(final Data data) {
            final ModelAttribute<?> bound = this.attribute.bind(
                data.property(this.attribute.name())
            );
            return new BoundModel(
                this,
                data,
                Map.of(bound.name(), bound.value())
            );
        }

        @Override
        public Iterator<Attribute<?>> iterator() {
            return List.<Attribute<?>>of(this.attribute).iterator();
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
