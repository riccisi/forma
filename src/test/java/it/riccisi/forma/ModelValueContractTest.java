package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.StringAttribute;
import it.riccisi.forma.data.DataOf;
import it.riccisi.forma.metadata.MetadataOf;
import it.riccisi.forma.model.AttributeValueAt;
import it.riccisi.forma.model.ModelOf;
import it.riccisi.forma.field.NamedReference;
import it.riccisi.forma.field.FieldOf;

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
 * Contract for demand-driven semantic values.
 */
final class ModelValueContractTest {

    @Test
    void constructionDoesNotResolveMapping() {
        assertThat(new ValueModel().mappingCalls(), is(0));
    }

    @Test
    void iterationDoesNotResolveMapping() {
        assertThat(new ValueModel().iterate().mappingCalls(), is(0));
    }

    @Test
    void nameDoesNotResolveMapping() {
        assertThat(new ValueModel().name().mappingCalls(), is(0));
    }

    @Test
    void valueReadsOnlyRequestedField() {
        assertThat(new ValueModel().value(), is("ACTIVE:0:1"));
    }

    private static final class ValueModel {

        private final AtomicInteger mappings;
        private final CountingValue id;
        private final CountingValue status;
        private final Model model;

        private ValueModel() {
            this.mappings = new AtomicInteger();
            this.id = new CountingValue("42");
            this.status = new CountingValue("ACTIVE");
            this.model = new ModelOf(
                new MetadataOf(
                    new StringAttribute(new AttributeNameOf<>("id")),
                    new StringAttribute(new AttributeNameOf<>("status"))
                ),
                new DataOf(
                    new FieldOf(new NamedReference("id"), this.id),
                    new FieldOf(new NamedReference("status"), this.status)
                ),
                new CountingMapping(this.mappings)
            );
        }

        ValueModel iterate() {
            this.model.iterator().next();
            return this;
        }

        ValueModel name() {
            this.model.iterator().next().name();
            return this;
        }

        int mappingCalls() {
            return this.mappings.get();
        }

        String value() {
            final String value = new AttributeValueAt<String>(
                new AttributeNameOf<>("status"),
                this.model
            ).value();
            return String.format(
                "%s:%d:%d",
                value,
                this.id.reads(),
                this.status.reads()
            );
        }
    }

    private static final class CountingMapping implements FieldMapping {

        private final AtomicInteger calls;

        private CountingMapping(final AtomicInteger calls) {
            this.calls = calls;
        }

        @Override
        public FieldReference reference(final AttributeName<?> attribute) {
            this.calls.incrementAndGet();
            return new NamedReference(new UncheckedText(attribute).asString());
        }
    }

    private static final class CountingValue implements FieldValue {

        private final Text origin;
        private final AtomicInteger reads;

        private CountingValue(final String origin) {
            this.origin = new TextOf(origin);
            this.reads = new AtomicInteger();
        }

        @Override
        public Text asText() {
            this.reads.incrementAndGet();
            return this.origin;
        }

        @Override
        public Number asNumber() {
            this.reads.incrementAndGet();
            throw new UnsupportedOperationException();
        }

        int reads() {
            return this.reads.get();
        }
    }
}
