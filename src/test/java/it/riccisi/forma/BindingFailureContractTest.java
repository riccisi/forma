package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

/**
 * Contract for failures occurring while establishing a model.
 */
final class BindingFailureContractTest {

    @Test
    void identifiesMissingRepresentedProperty() {
        final AttributeName<String> name = new AttributeNameOf<>("name");
        final PropertyReference reference = new NamedReference("student_name");

        final BindingFailure failure = assertThrows(
            BindingFailure.class,
            () -> new ModelOf(
                new MetadataOf(new StringAttribute(name)),
                new HashtableData(Map.of()),
                new ExplicitMapping(Map.of(name, reference))
            )
        );

        assertSame(name, failure.attribute());
        assertSame(reference, failure.property());
        assertEquals("missing", this.kind(failure.reason()));
    }

    @Test
    void identifiesUninterpretableRepresentedValue() {
        final AttributeName<Integer> age = new AttributeNameOf<>("age");
        final PropertyReference reference = new NamedReference("student_age");

        final BindingFailure failure = assertThrows(
            BindingFailure.class,
            () -> new ModelOf(
                new MetadataOf(new IntegerAttribute(age)),
                new HashtableData(
                    Map.of(
                        reference,
                        new TextValue(new TextOf("not-a-number"))
                    )
                ),
                new ExplicitMapping(Map.of(age, reference))
            )
        );

        assertSame(age, failure.attribute());
        assertSame(reference, failure.property());
        assertEquals("uninterpretable", this.kind(failure.reason()));
    }

    @Test
    void identifiesSemanticallyRejectedValue() {
        final AttributeName<String> name = new AttributeNameOf<>("name");
        final PropertyReference reference = new NamedReference("student_name");

        final BindingFailure failure = assertThrows(
            BindingFailure.class,
            () -> new ModelOf(
                new MetadataOf(
                    new NonBlankAttribute(new StringAttribute(name))
                ),
                new HashtableData(
                    Map.of(
                        reference,
                        new TextValue(new TextOf("   "))
                    )
                ),
                new ExplicitMapping(Map.of(name, reference))
            )
        );

        assertSame(name, failure.attribute());
        assertSame(reference, failure.property());
        assertEquals("rejected", this.kind(failure.reason()));
    }

    private String kind(final BindingReason reason) {
        return reason.describe(
            new BindingReasonSelection<>() {
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
        );
    }

    private record NamedReference(String value) implements PropertyReference {
    }

    private record ExplicitMapping(
        Map<AttributeName<?>, PropertyReference> references
    ) implements PropertyMapping {

        @Override
        public PropertyReference property(final AttributeName<?> attribute) {
            return this.references.get(attribute);
        }
    }
}
