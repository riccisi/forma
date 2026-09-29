package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.IntegerAttribute;
import it.riccisi.forma.attribute.NonBlankAttribute;
import it.riccisi.forma.attribute.StringAttribute;
import it.riccisi.forma.data.HashtableData;
import it.riccisi.forma.exception.AttributeValueException;
import it.riccisi.forma.exception.MissingFieldException;
import it.riccisi.forma.exception.RejectedValueException;
import it.riccisi.forma.exception.UnparsableValueException;
import it.riccisi.forma.field.NamedReference;
import it.riccisi.forma.field.TextValue;
import it.riccisi.forma.metadata.MetadataOf;
import it.riccisi.forma.model.ModelOf;
import java.util.Map;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

final class AttributeValueExceptionContractTest {

    @Test
    void preservesMissingFieldCause() {
        assertThat(
            new FailedValue(
                new StringAttribute(new AttributeNameOf<>("name")),
                new HashtableData(Map.of()),
                new NamedReference("student_name")
            ).failure().getCause(),
            instanceOf(MissingFieldException.class)
        );
    }

    @Test
    void preservesUnparsableValueCause() {
        assertThat(
            new FailedValue(
                new IntegerAttribute(new AttributeNameOf<>("age")),
                new HashtableData(Map.of(
                    new NamedReference("student_age"),
                    new TextValue(new TextOf("not-a-number"))
                )),
                new NamedReference("student_age")
            ).failure().getCause(),
            instanceOf(UnparsableValueException.class)
        );
    }

    @Test
    void preservesRejectedValueCause() {
        assertThat(
            new FailedValue(
                new NonBlankAttribute(
                    new StringAttribute(new AttributeNameOf<>("name"))
                ),
                new HashtableData(Map.of(
                    new NamedReference("student_name"),
                    new TextValue(new TextOf("   "))
                )),
                new NamedReference("student_name")
            ).failure().getCause(),
            instanceOf(RejectedValueException.class)
        );
    }

    @Test
    void preservesAttributeContext() {
        final FailedValue value = new FailedValue(
            new StringAttribute(new AttributeNameOf<>("name")),
            new HashtableData(Map.of()),
            new NamedReference("student_name")
        );
        assertThat(value.failure().attribute(), sameInstance(value.attribute.name()));
    }

    @Test
    void preservesFieldContext() {
        final FieldReference reference = new NamedReference("student_name");
        assertThat(
            new FailedValue(
                new StringAttribute(new AttributeNameOf<>("name")),
                new HashtableData(Map.of()),
                reference
            ).failure().field(),
            is(reference)
        );
    }

    private static final class FailedValue {

        private final Attribute<?> attribute;
        private final Data data;
        private final FieldReference reference;

        private FailedValue(
            final Attribute<?> attribute,
            final Data data,
            final FieldReference reference
        ) {
            this.attribute = attribute;
            this.data = data;
            this.reference = reference;
        }

        AttributeValueException failure() {
            try {
                this.value();
                throw new AssertionError("Expected AttributeValueException");
            } catch (final AttributeValueException failure) {
                return failure;
            }
        }

        private Object value() {
            return new ModelOf(
                    new MetadataOf(this.attribute),
                    this.data,
                    ignored -> this.reference
                )
            ).valueOf(this.attribute.name());
        }
    }
}
