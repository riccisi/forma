package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.StringAttribute;
import it.riccisi.forma.data.DataOf;
import it.riccisi.forma.mapping.SameNameMapping;
import it.riccisi.forma.metadata.MetadataOf;
import it.riccisi.forma.model.ModelOf;
import it.riccisi.forma.property.NamedReference;
import it.riccisi.forma.property.PropertyAt;
import it.riccisi.forma.property.TextValue;
import it.riccisi.forma.property.ValueProperty;
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
    void constructionDoesNotInterpretUnusedProperty() {
        assertThat(new UnusedPropertyScenario().interpreted(), is(false));
    }

    @Test
    void modelRetainsOriginalData() {
        final UnusedPropertyScenario scenario = new UnusedPropertyScenario();
        assertThat(scenario.model.data(), sameInstance(scenario.data));
    }

    @Test
    void modelExposesOnlyMetadataAttributes() {
        assertThat(new UnusedPropertyScenario().attributeCount(), equalTo(2L));
    }

    @Test
    void unusedPropertyCanStillBeRead() {
        assertThat(
            new UnusedPropertyScenario().description(),
            equalTo("Preserved source data")
        );
    }

    @Test
    void readingUnusedPropertyInterpretsItsValue() {
        assertThat(new UnusedPropertyScenario().interpretedAfterRead(), is(true));
    }

    private static final class UnusedPropertyScenario {

        private final AtomicBoolean observed;
        private final Data data;
        private final Model model;

        private UnusedPropertyScenario() {
            this.observed = new AtomicBoolean();
            final PropertyReference description = new NamedReference("description");
            this.data = new DataOf(
                new ValueProperty(
                    new NamedReference("id"), new TextValue(new TextOf("42"))
                ),
                new ValueProperty(
                    new NamedReference("status"), new TextValue(new TextOf("ACTIVE"))
                ),
                new ValueProperty(
                    description,
                    new ObservedValue(
                        new TextValue(new TextOf("Preserved source data")),
                        this.observed
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
            return this.observed.get();
        }

        long attributeCount() {
            return StreamSupport.stream(this.model.spliterator(), false).count();
        }

        String description() {
            return new UncheckedText(
                new PropertyAt(new NamedReference("description"), this.model.data())
                    .value().asText()
            ).asString();
        }

        boolean interpretedAfterRead() {
            this.description();
            return this.interpreted();
        }
    }

    private static final class ObservedValue implements PropertyValue {

        private final PropertyValue origin;
        private final AtomicBoolean observed;

        private ObservedValue(final PropertyValue origin, final AtomicBoolean observed) {
            this.origin = origin;
            this.observed = observed;
        }

        @Override
        public Text asText() {
            this.observed.set(true);
            return this.origin.asText();
        }

        @Override
        public Number asNumber() {
            this.observed.set(true);
            return this.origin.asNumber();
        }
    }
}
