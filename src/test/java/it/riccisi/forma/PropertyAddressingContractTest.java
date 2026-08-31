package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.cactoos.Text;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

final class PropertyAddressingContractTest {

    @Test
    void namedRepresentationDoesNotAssumeSemanticNameEquality() {
        final AttributeName<String> email = new SemanticName();
        final PropertyReference field = new NamedReference("e_mail_address");
        final Metadata metadata = new SingleAttributeMetadata(new StringAttribute(email));
        final Data data = new ReferencedData(
            Map.of(field, new TextProperty(field, "alice@example.com"))
        );

        final Model model = metadata.bind(
            data,
            new ExplicitMapping(Map.of(email, field))
        );

        assertEquals("alice@example.com", new AttributeOf<>(email, model).value());
    }

    @Test
    void positionalRepresentationUsesTheSameBindingProtocol() {
        final AttributeName<String> email = new SemanticName();
        final PropertyReference position = new PositionalReference(7);
        final Metadata metadata = new SingleAttributeMetadata(new StringAttribute(email));
        final Data data = new ReferencedData(
            Map.of(position, new TextProperty(position, "alice@example.com"))
        );

        final Model model = metadata.bind(
            data,
            new ExplicitMapping(Map.of(email, position))
        );

        assertEquals("alice@example.com", new AttributeOf<>(email, model).value());
    }

    @Test
    void nestedRepresentationUsesTheSameBindingProtocol() {
        final AttributeName<String> email = new SemanticName();
        final PropertyReference path = new PathReference(List.of("contact", "email"));
        final Metadata metadata = new SingleAttributeMetadata(new StringAttribute(email));
        final Data data = new ReferencedData(
            Map.of(path, new TextProperty(path, "alice@example.com"))
        );

        final Model model = metadata.bind(
            data,
            new ExplicitMapping(Map.of(email, path))
        );

        assertEquals("alice@example.com", new AttributeOf<>(email, model).value());
    }

    @Test
    void mappingBelongsToEachBindingRelationship() {
        final AttributeName<String> email = new SemanticName();
        final PropertyReference first = new NamedReference("email");
        final PropertyReference second = new NamedReference("e_mail");
        final Metadata metadata = new SingleAttributeMetadata(new StringAttribute(email));
        final Data data = new ReferencedData(
            Map.of(
                first, new TextProperty(first, "first@example.com"),
                second, new TextProperty(second, "second@example.com")
            )
        );

        final Model firstModel = metadata.bind(
            data,
            new ExplicitMapping(Map.of(email, first))
        );
        final Model secondModel = metadata.bind(
            data,
            new ExplicitMapping(Map.of(email, second))
        );

        assertSame(data, firstModel.data());
        assertSame(data, secondModel.data());
        assertEquals("first@example.com", new AttributeOf<>(email, firstModel).value());
        assertEquals("second@example.com", new AttributeOf<>(email, secondModel).value());
    }

    private record SemanticName() implements AttributeName<String> {
    }

    private record NamedReference(String value) implements PropertyReference {
    }

    private record PositionalReference(int value) implements PropertyReference {
    }

    private record PathReference(List<String> segments) implements PropertyReference {
    }

    private record TextProperty(
        PropertyReference reference,
        String text
    ) implements Property {

        @Override
        public PropertyValue value() {
            return new TextValue(new TextOf(this.text));
        }
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
            try {
                return new BoundAttribute<>(this.name, value.asString());
            } catch (final Exception err) {
                throw new IllegalArgumentException(err);
            }
        }
    }

    private record ReferencedData(
        Map<PropertyReference, Property> properties
    ) implements Data {

        @Override
        public Iterator<Property> iterator() {
            return this.properties.values().iterator();
        }
    }

    private record ExplicitMapping(
        Map<AttributeName<?>, PropertyReference> references
    ) implements PropertyMapping {

        @Override
        public PropertyReference property(final AttributeName<?> attribute) {
            return this.references.get(attribute);
        }
    }

    private record SingleAttributeMetadata(Attribute<?> attribute) implements Metadata {

        @Override
        public Model bind(final Data data, final PropertyMapping mapping) {
            final ModelAttribute<?> bound = this.attribute.bind(
                new PropertyAt(mapping.property(this.attribute.name()), data)
            );
            return new BoundModel(this, data, List.of(bound));
        }

        @Override
        public Iterator<Attribute<?>> iterator() {
            return List.<Attribute<?>>of(this.attribute).iterator();
        }
    }

    private record BoundAttribute<T>(
        AttributeName<T> name,
        T value
    ) implements ModelAttribute<T> {
    }

    private record BoundModel(
        Metadata metadata,
        Data data,
        List<ModelAttribute<?>> attributes
    ) implements Model {

        @Override
        public Iterator<ModelAttribute<?>> iterator() {
            return this.attributes.iterator();
        }
    }
}
