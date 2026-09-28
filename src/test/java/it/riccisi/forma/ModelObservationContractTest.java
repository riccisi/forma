package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.StringAttribute;
import it.riccisi.forma.data.DataOf;
import it.riccisi.forma.metadata.MetadataOf;
import it.riccisi.forma.model.AttributeOf;
import it.riccisi.forma.model.ModelOf;
import it.riccisi.forma.property.NamedReference;
import it.riccisi.forma.property.ValueProperty;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.cactoos.Text;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.junit.jupiter.api.Test;

/**
 * Contract for demand-driven semantic observation.
 */
final class ModelObservationContractTest {

    @Test
    void constructionDoesNotResolveMapping() {
        assertThat(new ObservedModel().mappingCalls(), is(0));
    }

    @Test
    void iterationDoesNotResolveMapping() {
        assertThat(new ObservedModel().iterate().mappingCalls(), is(0));
    }

    @Test
    void nameDoesNotResolveMapping() {
        assertThat(new ObservedModel().name().mappingCalls(), is(0));
    }

    @Test
    void valueInterpretsOnlyObservedProperty() {
        assertThat(new ObservedModel().value(), is("ACTIVE:0:1"));
    }

    private static final class ObservedModel {

        private final AtomicInteger mappings;
        private final CountingValue id;
        private final CountingValue status;
        private final Model model;

        private ObservedModel() {
            this.mappings = new AtomicInteger();
            this.id = new CountingValue("42");
            this.status = new CountingValue("ACTIVE");
            this.model = new ModelOf(
                new MetadataOf(
                    new StringAttribute(new AttributeNameOf<>("id")),
                    new StringAttribute(new AttributeNameOf<>("status"))
                ),
                new DataOf(
                    new ValueProperty(new NamedReference("id"), this.id),
                    new ValueProperty(new NamedReference("status"), this.status)
                ),
                new CountingMapping(this.mappings)
            );
        }

        ObservedModel iterate() {
            this.model.iterator().next();
            return this;
        }

        ObservedModel name() {
            this.model.iterator().next().name();
            return this;
        }

        int mappingCalls() {
            return this.mappings.get();
        }

        String value() {
            final String value = new AttributeOf<String>(
                new AttributeNameOf<>("status"),
                this.model
            ).value();
            return String.format(
                "%s:%d:%d",
                value,
                this.id.observations(),
                this.status.observations()
            );
        }
    }

    private static final class CountingMapping implements PropertyMapping {

        private final AtomicInteger calls;

        private CountingMapping(final AtomicInteger calls) {
            this.calls = calls;
        }

        @Override
        public PropertyReference property(final AttributeName<?> attribute) {
            this.calls.incrementAndGet();
            return new NamedReference(new UncheckedText(attribute).asString());
        }
    }

    private static final class CountingValue implements PropertyValue {

        private final Text origin;
        private final AtomicInteger observations;

        private CountingValue(final String origin) {
            this.origin = new TextOf(origin);
            this.observations = new AtomicInteger();
        }

        @Override
        public Text asText() {
            this.observations.incrementAndGet();
            return this.origin;
        }

        @Override
        public Number asNumber() {
            this.observations.incrementAndGet();
            throw new UnsupportedOperationException();
        }

        int observations() {
            return this.observations.get();
        }
    }
}
