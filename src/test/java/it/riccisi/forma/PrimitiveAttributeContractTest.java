package it.riccisi.forma;

import org.cactoos.Text;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Contract spike for primitive semantic attributes.
 */
final class PrimitiveAttributeContractTest {

    @Test
    void bindsTextWithoutKnowingItsRepresentation() {
        final AttributeName<String> name = new Name<>();
        final Attribute<String> attribute = new StringAttribute(name);
        final ModelAttribute<String> bound = attribute.bind(
            new ValueProperty(new Reference(), new TextValue(new TextOf("Ada")))
        );
        assertEquals(name, bound.name());
        assertEquals("Ada", bound.value());
    }

    @Test
    void bindsIntegerFromNumericRepresentation() {
        final AttributeName<Integer> name = new Name<>();
        final Attribute<Integer> attribute = new IntegerAttribute(name);
        final ModelAttribute<Integer> bound = attribute.bind(
            new ValueProperty(new Reference(), new NumberValue(42))
        );
        assertEquals(name, bound.name());
        assertEquals(42, bound.value());
    }

    @Test
    void bindsIntegerFromConvertibleTextRepresentation() {
        final AttributeName<Integer> name = new Name<>();
        final Attribute<Integer> attribute = new IntegerAttribute(name);
        final ModelAttribute<Integer> bound = attribute.bind(
            new ValueProperty(new Reference(), new TextValue(new TextOf("42")))
        );
        assertEquals(42, bound.value());
    }

    @Test
    void rejectsNonIntegralNumberAsIntegerSemantics() {
        final Attribute<Integer> attribute = new IntegerAttribute(new Name<>());
        assertThrows(
            IllegalArgumentException.class,
            () -> attribute.bind(
                new ValueProperty(new Reference(), new NumberValue(42.5))
            )
        );
    }

    @Test
    void rejectsIntegerOutsideJavaRange() {
        final Attribute<Integer> attribute = new IntegerAttribute(new Name<>());
        assertThrows(
            IllegalArgumentException.class,
            () -> attribute.bind(
                new ValueProperty(
                    new Reference(),
                    new TextValue(new TextOf("2147483648"))
                )
            )
        );
    }

    @Test
    void rejectsTextThatCannotBeInterpretedAsInteger() {
        final Attribute<Integer> attribute = new IntegerAttribute(new Name<>());
        assertThrows(
            IllegalArgumentException.class,
            () -> attribute.bind(
                new ValueProperty(
                    new Reference(),
                    new TextValue(new TextOf("forty-two"))
                )
            )
        );
    }

    private record Name<T>() implements AttributeName<T> {
    }

    private record Reference() implements PropertyReference {
    }

    private static final class StringAttribute extends TextAttribute<String> {

        private final AttributeName<String> name;

        private StringAttribute(final AttributeName<String> name) {
            this.name = name;
        }

        @Override
        public AttributeName<String> name() {
            return this.name;
        }

        @Override
        protected ModelAttribute<String> bind(final Text value) {
            return new BoundAttribute<>(this.name, value.asString());
        }
    }
}
