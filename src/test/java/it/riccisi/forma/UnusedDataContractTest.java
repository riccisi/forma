package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.StringAttribute;
import it.riccisi.forma.data.DataOf;
import it.riccisi.forma.mapping.SameNameMapping;
import it.riccisi.forma.metadata.MetadataOf;
import it.riccisi.forma.model.ModelOf;
import it.riccisi.forma.field.NamedReference;
import it.riccisi.forma.field.FieldAt;
import it.riccisi.forma.field.TextValue;
import it.riccisi.forma.field.FieldOf;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;
import org.cactoos.Text;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Unused represented information remains accessible without being interpreted
 * as part of model construction or metadata iteration.
 */
final class UnusedDataContractTest {

    @Test
    void constructionDoesNotInterpretUnusedField() {
        assertThat(new UnusedFieldScenario().interpreted(), is(false));
    }

    @Test
    void modelRetainsOriginalData() {
        final UnusedFieldScenario scenario = new UnusedFieldScenario();
        assertThat(scenario.model.data(), sameInstance(scenario.data));
    }

    @Test
    void modelExposesOnlyMetadataAttributes() {
        assertThat(new UnusedFieldScenario().attributeCount(), equalTo(2L));
    }

    @Test
    void unusedFieldCanStillBeRead() {
        assertThat(
            new UnusedFieldScenario().description(),
            equalTo("Preserved source data")
        );
    }

    @Test
    void readingUnusedFieldInterpretsItsValue() {
        assertThat(new UnusedFieldScenario().interpretedAfterRead(), is(true));
    }

    private static final class UnusedFieldScenario {

        private final AtomicBoolean read;
        private final Data data;
        private final Model model;

        private UnusedFieldScenario() {
            this.read = new AtomicBoolean();
            final FieldReference description = new NamedReference("description");
            this.data = new DataOf(
                new FieldOf(
                    new NamedReference("id"), new TextValue(new TextOf("42"))
                ),
                new FieldOf(
                    new NamedReference("status"), new TextValue(new TextOf("ACTIVE"))
                ),
                new FieldOf(
                    description,
                    new ReadValue(
                        new TextValue(new TextOf("Preserved source data")),
                        this.read
                    )
                )
            );
            this.model = new ModelOf(
                new MetadataOf(
                    new StringAttribute(new AttributeNameOf<>("id")),
                    new StringAttribute(new AttributeNameOf<>("status"))
                ),
                this.data,
                new SameNameMapping(NamedReference::new)
            );
        }

        boolean interpreted() {
            return this.read.get();
        }

        long attributeCount() {
            return StreamSupport.stream(this.model.metadata().spliterator(), false).count();
        }

        String description() {
            return new UncheckedText(
                new FieldAt(new NamedReference("description"), this.model.data())
                    .value().asText()
            ).asString();
        }

        boolean interpretedAfterRead() {
            this.description();
            return this.interpreted();
        }
    }

    private static final class ReadValue implements FieldValue {

        private final FieldValue origin;
        private final AtomicBoolean read;

        private ReadValue(final FieldValue origin, final AtomicBoolean read) {
            this.origin = origin;
            this.read = read;
        }

        @Override
        public Text asText() {
            this.read.set(true);
            return this.origin.asText();
        }

        @Override
        public Number asNumber() {
            this.read.set(true);
            return this.origin.asNumber();
        }
    }
}
