package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.stream.StreamSupport;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

/**
 * End-to-end contract for valid model construction.
 */
final class ModelConstructionContractTest {

    @Test
    void constructsCompleteModelFromMetadataAndData() throws Exception {
        final AttributeName<String> name = new AttributeNameOf<>("name");
        final AttributeName<Integer> age = new AttributeNameOf<>("age");
        final PropertyReference nameref = new NamedReference("student_name");
        final PropertyReference ageref = new NamedReference("student_age");
        final PropertyReference description = new NamedReference("description");
        final Metadata metadata = new MetadataOf(
            new NonBlankAttribute(new StringAttribute(name)),
            new IntegerAttribute(age)
        );
        final Data data = new HashtableData(
            Map.of(
                nameref, new TextValue(new TextOf("Ada")),
                ageref, new NumberValue(42),
                description, new TextValue(new TextOf("Preserved source data"))
            )
        );
        final PropertyMapping mapping = new ExplicitMapping(
            Map.of(name, nameref, age, ageref)
        );

        final Model model = new ModelOf(metadata, data, mapping);

        assertSame(metadata, model.metadata());
        assertSame(data, model.data());
        assertEquals("Ada", new AttributeOf<>(name, model).value());
        assertEquals(42, new AttributeOf<>(age, model).value());
        assertEquals(
            2L,
            StreamSupport.stream(model.spliterator(), false).count()
        );
        assertEquals(
            "Preserved source data",
            new PropertyAt(description, model.data()).value().asText().asString()
        );
    }

    @Test
    void rejectsModelWhenAnySemanticAttributeFails() {
        final AttributeName<Integer> age = new AttributeNameOf<>("age");
        final AttributeName<String> name = new AttributeNameOf<>("name");
        final PropertyReference ageref = new NamedReference("student_age");
        final PropertyReference nameref = new NamedReference("student_name");
        final Metadata metadata = new MetadataOf(
            new IntegerAttribute(age),
            new NonBlankAttribute(new StringAttribute(name))
        );
        final Data data = new HashtableData(
            Map.of(
                ageref, new NumberValue(42),
                nameref, new TextValue(new TextOf("   "))
            )
        );
        final PropertyMapping mapping = new ExplicitMapping(
            Map.of(age, ageref, name, nameref)
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new ModelOf(metadata, data, mapping)
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
