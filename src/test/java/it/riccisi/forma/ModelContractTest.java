package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.TextAttribute;
import it.riccisi.forma.data.DataOf;
import it.riccisi.forma.mapping.ExplicitMapping;
import it.riccisi.forma.metadata.MetadataOf;
import it.riccisi.forma.property.NamedReference;
import it.riccisi.forma.model.AttributeOf;
import it.riccisi.forma.model.ModelOf;
import it.riccisi.forma.property.NumberValue;
import it.riccisi.forma.property.TextValue;
import org.cactoos.Text;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

final class ModelContractTest {

    @Test
    void sameSemanticAttributeReadsHeterogeneousRepresentations() {
        final AttributeName<Email> name = new AttributeNameOf<>("email");
        final Attribute<Email> email = new EmailAttribute(name);
        final PropertyReference reference = new NamedReference("email");

        assertEquals(
            "alice@example.com",
            email.valueFrom(new JsonStringProperty(reference, "alice@example.com"))
                .toString()
        );
        assertEquals(
            "bob@example.com",
            email.valueFrom(new MapStringProperty(reference, "bob@example.com"))
                .toString()
        );
        assertEquals(
            "carol@example.com",
            email.valueFrom(new PojoStringProperty(reference, "carol@example.com"))
                .toString()
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
        final AttributeName<Email> name = new AttributeNameOf<>("email");
        final PropertyReference field = new NamedReference("e_mail");
        final Attribute<Email> email = new EmailAttribute(name);
        final Metadata metadata = new MetadataOf(email);
        final Data data = new DataOf(new JsonStringProperty(field, "alice@example.com"));
        final PropertyMapping mapping = new ExplicitMapping(
            Map.of(new AttributeNameOf<Email>("email"), field)
        );

        final Model model = new ModelOf(metadata, data, mapping);

        assertSame(metadata, model.metadata());
        assertSame(data, model.data());
        assertEquals(
            "alice@example.com",
            new AttributeOf<Email>(
                new AttributeNameOf<>("email"),
                model
            ).value().toString()
        );
        assertSame(name, model.iterator().next().name());
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
        protected Email interpret(Text value) {
            return new Email(value);
        }
    }

}
