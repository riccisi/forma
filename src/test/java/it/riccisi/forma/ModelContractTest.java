package it.riccisi.forma;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

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
    void interpretsJsonProperty() {
        assertThat(
            new EmailAttribute(new AttributeNameOf<>("email")).valueFrom(
                new JsonStringProperty(new NamedReference("email"), "alice@example.com")
            ).toString(),
            equalTo("alice@example.com")
        );
    }

    @Test
    void interpretsMapProperty() {
        assertThat(
            new EmailAttribute(new AttributeNameOf<>("email")).valueFrom(
                new MapStringProperty(new NamedReference("email"), "bob@example.com")
            ).toString(),
            equalTo("bob@example.com")
        );
    }

    @Test
    void interpretsPojoProperty() {
        assertThat(
            new EmailAttribute(new AttributeNameOf<>("email")).valueFrom(
                new PojoStringProperty(new NamedReference("email"), "carol@example.com")
            ).toString(),
            equalTo("carol@example.com")
        );
    }

    @Test
    void convertsTextToNumber() {
        assertThat(
            new TextValue(new TextOf("42")).asNumber().toString(),
            equalTo("42")
        );
    }

    @Test
    void convertsNumberToText() {
        assertThat(
            new org.cactoos.text.UncheckedText(new NumberValue(42).asText()).asString(),
            equalTo("42")
        );
    }

    @Test
    void retainsMetadataInMappedModel() {
        final EmailModel scenario = new EmailModel();
        assertThat(scenario.model.metadata(), sameInstance(scenario.metadata));
    }

    @Test
    void retainsDataInMappedModel() {
        final EmailModel scenario = new EmailModel();
        assertThat(scenario.model.data(), sameInstance(scenario.data));
    }

    @Test
    void resolvesExplicitlyMappedEmail() {
        assertThat(new EmailModel().email(), equalTo("alice@example.com"));
    }

    @Test
    void preservesAttributeIdentity() {
        final EmailModel scenario = new EmailModel();
        assertThat(scenario.model.iterator().next().name(), sameInstance(scenario.name));
    }

    private static final class EmailModel {

        private final AttributeName<Email> name;
        private final Metadata metadata;
        private final Data data;
        private final Model model;

        private EmailModel() {
            this.name = new AttributeNameOf<>("email");
            final PropertyReference field = new NamedReference("e_mail");
            this.metadata = new MetadataOf(new EmailAttribute(this.name));
            this.data = new DataOf(
                new JsonStringProperty(field, "alice@example.com")
            );
            this.model = new ModelOf(
                this.metadata,
                this.data,
                new ExplicitMapping(Map.of(new AttributeNameOf<Email>("email"), field))
            );
        }

        String email() {
            return new AttributeOf<Email>(
                new AttributeNameOf<>("email"), this.model
            ).value().toString();
        }
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
