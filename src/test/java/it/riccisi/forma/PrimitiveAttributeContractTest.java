package it.riccisi.forma;

import it.riccisi.forma.exception.RejectedValueException;
import it.riccisi.forma.exception.UnparsableValueException;
import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.IntegerAttribute;
import it.riccisi.forma.attribute.NonBlankAttribute;
import it.riccisi.forma.attribute.StringAttribute;
import it.riccisi.forma.field.NumberValue;
import it.riccisi.forma.field.TextValue;
import it.riccisi.forma.field.FieldOf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

/**
 * Contract spike for primitive semantic attributes.
 */
final class PrimitiveAttributeContractTest {

    @Test
    void bindsTextWithoutKnowingItsRepresentation() {
        assertThat(
            new StringAttribute(
                new AttributeNameOf<>("name")
            ).valueFrom(
                new FieldOf(new Reference(), new TextValue(new TextOf("Ada")))
            ),
            equalTo("Ada")
        );
    }

    @Test
    void composesSemanticConstraintAroundPrimitiveAttribute() {
        assertThat(
            new NonBlankAttribute(
                new StringAttribute(new AttributeNameOf<>("name"))
            ).valueFrom(
                new FieldOf(new Reference(), new TextValue(new TextOf("Ada")))
            ),
            equalTo("Ada")
        );
    }

    @Test
    void rejectsValueThroughSemanticConstraint() {
        assertThrows(
            RejectedValueException.class,
            () -> new NonBlankAttribute(
                new StringAttribute(new AttributeNameOf<>("name"))
            ).valueFrom(
                new FieldOf(
                    new Reference(),
                    new TextValue(new TextOf("   "))
                )
            )
        );
    }

    @Test
    void bindsIntegerFromNumericRepresentation() {
        assertThat(
            new IntegerAttribute(new AttributeNameOf<>("age")).valueFrom(
                new FieldOf(new Reference(), new NumberValue(42))
            ),
            is(42)
        );
    }

    @Test
    void bindsIntegerFromConvertibleTextRepresentation() {
        assertThat(
            new IntegerAttribute(new AttributeNameOf<>("age")).valueFrom(
                new FieldOf(
                    new Reference(),
                    new TextValue(new TextOf("42"))
                )
            ),
            is(42)
        );
    }

    @Test
    void rejectsNonIntegralNumberAsIntegerSemantics() {
        assertThrows(
            UnparsableValueException.class,
            () -> new IntegerAttribute(
                new AttributeNameOf<>("age")
            ).valueFrom(
                new FieldOf(new Reference(), new NumberValue(42.5))
            )
        );
    }

    @Test
    void rejectsIntegerOutsideJavaRange() {
        assertThrows(
            UnparsableValueException.class,
            () -> new IntegerAttribute(
                new AttributeNameOf<>("age")
            ).valueFrom(
                new FieldOf(
                    new Reference(),
                    new TextValue(new TextOf("2147483648"))
                )
            )
        );
    }

    @Test
    void rejectsTextThatCannotBeInterpretedAsInteger() {
        assertThrows(
            UnparsableValueException.class,
            () -> new IntegerAttribute(
                new AttributeNameOf<>("age")
            ).valueFrom(
                new FieldOf(
                    new Reference(),
                    new TextValue(new TextOf("forty-two"))
                )
            )
        );
    }

    private record Reference() implements FieldReference {
    }
}
