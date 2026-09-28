package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.IntegerAttribute;
import it.riccisi.forma.attribute.NonBlankAttribute;
import it.riccisi.forma.attribute.StringAttribute;
import it.riccisi.forma.data.HashtableData;
import it.riccisi.forma.metadata.MetadataOf;
import it.riccisi.forma.model.AttributeOf;
import it.riccisi.forma.model.ModelOf;
import it.riccisi.forma.property.NamedReference;
import it.riccisi.forma.property.TextValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Map;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

/**
 * Contract for failures occurring while observing semantic values.
 */
final class BindingFailureContractTest {

    @Test
    void identifiesMissingRepresentedProperty() {
        assertThat(
            new FailedObservation(
                new StringAttribute(new AttributeNameOf<>("name")),
                new HashtableData(Map.of()),
                new NamedReference("student_name")
            ).kind(),
            is("missing")
        );
    }

    @Test
    void identifiesUninterpretableRepresentedValue() {
        assertThat(
            new FailedObservation(
                new IntegerAttribute(new AttributeNameOf<>("age")),
                new HashtableData(
                    Map.of(
                        new NamedReference("student_age"),
                        new TextValue(new TextOf("not-a-number"))
                    )
                ),
                new NamedReference("student_age")
            ).kind(),
            is("uninterpretable")
        );
    }

    @Test
    void identifiesSemanticallyRejectedValue() {
        assertThat(
            new FailedObservation(
                new NonBlankAttribute(
                    new StringAttribute(new AttributeNameOf<>("name"))
                ),
                new HashtableData(
                    Map.of(
                        new NamedReference("student_name"),
                        new TextValue(new TextOf("   "))
                    )
                ),
                new NamedReference("student_name")
            ).kind(),
            is("rejected")
        );
    }

    private static final class FailedObservation {

        private final Attribute<?> attribute;
        private final Data data;
        private final PropertyReference reference;

        private FailedObservation(
            final Attribute<?> attribute,
            final Data data,
            final PropertyReference reference
        ) {
            this.attribute = attribute;
            this.data = data;
            this.reference = reference;
        }

        String kind() {
            try {
                this.value();
                return "none";
            } catch (final BindingFailure failure) {
                return failure.reason().describe(new ReasonKind());
            }
        }

        private Object value() {
            return new AttributeOf<>(
                this.attribute.name(),
                new ModelOf(
                    new MetadataOf(this.attribute),
                    this.data,
                    ignored -> this.reference
                )
            ).value();
        }
    }

    private static final class ReasonKind
        implements BindingReasonSelection<String> {

        @Override
        public String missingProperty() {
            return "missing";
        }

        @Override
        public String uninterpretableValue() {
            return "uninterpretable";
        }

        @Override
        public String rejectedValue() {
            return "rejected";
        }
    }
}
