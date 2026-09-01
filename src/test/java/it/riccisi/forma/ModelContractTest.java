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
        final PropertyReference reference = new NamedReference("email");

        assertEquals(
            "alice@example.com",
            email.from(new JsonStringProperty(reference, "alice@example.com"))
                .value().toString()
        );
        assertEquals(
            "bob@example.com",
            email.from(new MapStringProperty(reference, "bob@example.com"))
                .value().toString()
        );
        assertEquals(
            "carol@example.com",
            email.from(new PojoStringProperty(reference, "carol@example.com"))
                .value().toString()
        );
    }

    @Test
    void propertyValuesOwnPrimitiveConversions() throws Exception {
        final PropertyValue textual = new TextValue(new TextOf("42"));
        final PropertyValue numeric = new NumberValue(42);

        assertEquals("42", textual.asNumber().toString());
        assertEquals("42", numeric.asText().asString());
    }

    @Test
    void mappingBelongsToBindingRelationship() {
        final AttributeName<Email> name = new EmailName();
        final PropertyReference field = new NamedReference("e_mail");
        final Attribute<Email> email = new EmailAttribute(name);
        final Metadata metadata = new SingleAttributeMetadata(email);
        final Data data = new NamedData(
            Map.of(field, new JsonStringProperty(field, "alice@example.com"))
        );
        final PropertyMapping mapping = new ExplicitMapping(Map.of(name, field));

        final Model model = new ModelOf(metadata, data, mapping);

        assertSame(metadata, model.metadata());
        assertSame(data, model.data());
        assertEquals(
            "alice@example.com",
            new AttributeOf<>(name, model).value().toString()
        );
        assertSame(name, model.iterator().next().name());
    }

    private record EmailName() implements AttributeName<Email> {
    }

    private record NamedReference(String value) implements PropertyReference {
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

    private record JsonStringProperty(
        PropertyReference reference,
        Text text
    ) implements Property {
        private JsonStringProperty(
            final PropertyReference reference,
            final String text
        ) {
            this(reference, new TextOf(text));
        }

        @Override
        public PropertyValue value() {
            return new TextValue(this.text);
        }
    }

    private record MapStringProperty(
        PropertyReference reference,
        Text text
    ) implements Property {
        private MapStringProperty(
            final PropertyReference reference,
            final String text
        ) {
            this(reference, new TextOf(text));
        }

        @Override
        public PropertyValue value() {
            return new TextValue(this.text);
        }
    }

    private record PojoStringProperty(
        PropertyReference reference,
        Text text
    ) implements Property {
        private PojoStringProperty(
            final PropertyReference reference,
            final String text
        ) {
            this(reference, new TextOf(text));
        }

        @Override
        public PropertyValue value() {
            return new TextValue(this.text);
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
        protected ModelAttribute<Email> bind(final Text value) {
            return new BoundModelAttribute<>(this.name, new Email(value));
        }
    }

    private record NamedData(
        Map<PropertyReference, Property> properties
    ) implements Data {

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
        public Iterator<Attribute<?>> iterator() {
            return List.<Attribute<?>>of(this.attribute).iterator();
        }
    }

    private record BoundModelAttribute<T>(
        AttributeName<T> name,
        T value
    ) implements ModelAttribute<T> {
    }
}
