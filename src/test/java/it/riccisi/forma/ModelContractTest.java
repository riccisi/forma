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

    @Test
    void mappingBelongsToBindingRelationship() {
        final AttributeName<Email> name = new EmailName();
        final PropertyName field = new NamedProperty("e_mail");
        final Attribute<Email> email = new EmailAttribute(name);
        final Metadata metadata = new SingleAttributeMetadata(email);
        final Data data = new NamedData(
            Map.of(field, new JsonStringProperty("alice@example.com"))
        );
        final PropertyMapping mapping = new ExplicitMapping(Map.of(name, field));

        final Model model = metadata.bind(data, mapping);

        assertSame(metadata, model.metadata());
        assertSame(data, model.data());
        assertEquals("alice@example.com", model.value(name).toString());
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

    private record JsonStringProperty(Text text) implements Property {
        private JsonStringProperty(final String text) {
            this(new TextOf(text));
        }

        @Override
        public <T> T describe(final PropertyValue<T> value) {
            return value.text(this.text);
        }
    }

    private record MapStringProperty(Text text) implements Property {
        private MapStringProperty(final String text) {
            this(new TextOf(text));
        }

        @Override
        public <T> T describe(final PropertyValue<T> value) {
            return value.text(this.text);
        }
    }

    private record PojoStringProperty(Text text) implements Property {
        private PojoStringProperty(final String text) {
            this(new TextOf(text));
        }

        @Override
        public <T> T describe(final PropertyValue<T> value) {
            return value.text(this.text);
        }
    }

    private record EmailAttribute(AttributeName<Email> name)
        extends TextAttribute<Email> {

        @Override
        public ModelAttribute<Email> text(final Text value) {
            return new BoundModelAttribute<>(this.name, new Email(value));
        }
    }

    private record NamedData(Map<PropertyName, Property> properties) implements Data {

        @Override
        public Property property(final PropertyReference reference) {
            return this.properties.get(reference);
        }

        @Override
        public Iterator<Property> iterator() {
            return this.properties.values().iterator();
        }
    }

    private record ExplicitMapping(
        Map<AttributeName<?>, PropertyReference> references
    ) implements PropertyMapping {

        @Override
        public PropertyReference property(final AttributeName<?> attribute) {
            return this.references.get(attribute);
        }
    }

    private record SingleAttributeMetadata(Attribute<?> attribute) implements Metadata {

        @Override
        public Model bind(final Data data, final PropertyMapping mapping) {
            final ModelAttribute<?> bound = this.attribute.bind(
                data.property(mapping.property(this.attribute.name()))
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
