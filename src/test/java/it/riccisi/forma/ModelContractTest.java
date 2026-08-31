package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private record EmailAttribute(AttributeName<Email> name)
        implements Attribute<TextProperty, Email> {

        @Override
        public ModelAttribute<Email> bind(final TextProperty property) {
            return new BoundModelAttribute<>(this.name, new Email(property.text()));
        }
    }

    private record BoundModelAttribute<T>(
        AttributeName<T> name,
        T value
    ) implements ModelAttribute<T> {
    }
}
