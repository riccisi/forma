package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.IntegerAttribute;
import it.riccisi.forma.attribute.NonBlankAttribute;
import it.riccisi.forma.attribute.StringAttribute;
import it.riccisi.forma.data.HashtableData;
import it.riccisi.forma.mapping.ExplicitMapping;
import it.riccisi.forma.metadata.MetadataOf;
import it.riccisi.forma.model.ModelOf;
import it.riccisi.forma.field.NamedReference;
import it.riccisi.forma.field.NumberValue;
import it.riccisi.forma.field.FieldAt;
import it.riccisi.forma.field.TextValue;
import java.util.Map;
import java.util.stream.StreamSupport;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

/**
 * End-to-end contract for demand-driven model values.
 */
final class ModelConstructionContractTest {

    @Test
    void retainsMetadata() {
        final Student student = new Student();
        assertThat(student.model.metadata(), sameInstance(student.metadata));
    }

    @Test
    void retainsData() {
        final Student student = new Student();
        assertThat(student.model.data(), sameInstance(student.data));
    }

    @Test
    void resolvesTextualAttribute() {
        assertThat(new Student().name(), equalTo("Ada"));
    }

    @Test
    void resolvesNumericAttribute() {
        assertThat(new Student().age(), equalTo(42));
    }

    @Test
    void exposesAllMetadataAttributes() {
        assertThat(new Student().attributeCount(), equalTo(2L));
    }

    @Test
    void preservesUninterpretedSourceField() {
        assertThat(new Student().description(), equalTo("Preserved source data"));
    }

    private static final class Student {

        private final AttributeName<String> name;
        private final AttributeName<Integer> age;
        private final FieldReference description;
        private final Metadata metadata;
        private final Data data;
        private final Model model;

        private Student() {
            this.name = new AttributeNameOf<>("name");
            this.age = new AttributeNameOf<>("age");
            final FieldReference nameref = new NamedReference("student_name");
            final FieldReference ageref = new NamedReference("student_age");
            this.description = new NamedReference("description");
            this.metadata = new MetadataOf(
                new NonBlankAttribute(new StringAttribute(this.name)),
                new IntegerAttribute(this.age)
            );
            this.data = new HashtableData(Map.of(
                nameref, new TextValue(new TextOf("Ada")),
                ageref, new NumberValue(42),
                this.description, new TextValue(new TextOf("Preserved source data"))
            ));
            this.model = new ModelOf(
                this.metadata, this.data,
                new ExplicitMapping(Map.of(this.name, nameref, this.age, ageref))
            );
        }

        String name() {
            return this.model.valueOf(this.name);
        }

        Integer age() {
            return this.model.valueOf(this.age);
        }

        long attributeCount() {
            return StreamSupport.stream(this.model.metadata().spliterator(), false).count();
        }

        String description() {
            return new UncheckedText(
                new FieldAt(this.description, this.model.data()).value().asText()
            ).asString();
        }
    }
}
