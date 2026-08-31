package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class ModelContractTest {

    @Test
    void bindsDataIntoTrustworthyModel() {
        final AttributeName<String> name = new NamedAttributeName<>();
        final Data data = new SinglePropertyData(new TextProperty("active"));
        final Metadata metadata = new SingleAttributeMetadata(new TextAttribute(name));

        final Model model = metadata.bind(data);

        assertSame(metadata, model.metadata());
        assertSame(data, model.data());
        assertEquals("active", model.value(name));
    }

    private record NamedAttributeName<T>() implements AttributeName<T> {
    }

    private record SinglePropertyData(Property property) implements Data {

        @Override
        public Iterator<Property> iterator() {
            return List.of(this.property).iterator();
        }
    }

    private record TextProperty(String text) implements Property {

        @Override
        @SuppressWarnings("unchecked")
        public <T> T describe(PropertyValue<T> value) {
            return (T) this.text;
        }
    }

    private record TextAttribute(AttributeName<String> name) implements Attribute<String> {

        @Override
        public ModelAttribute<String> bind(Property property) {
            return new BoundModelAttribute<>(this.name, property.describe(new TextValue()));
        }
    }

    private record TextValue() implements PropertyValue<String> {
    }

    private record BoundModelAttribute<T>(
        AttributeName<T> name,
        T value
    ) implements ModelAttribute<T> {
    }

    private record SingleAttributeMetadata(Attribute<?> attribute) implements Metadata {

        @Override
        public Model bind(Data data) {
            final Property property = data.iterator().next();
            final ModelAttribute<?> modelAttribute = this.attribute.bind(property);
            return new BoundModel(this, data, Map.of(modelAttribute.name(), modelAttribute.value()));
        }

        @Override
        public Iterator<Attribute<?>> iterator() {
            return List.<Attribute<?>>of(this.attribute).iterator();
        }
    }

    private record BoundModel(
        Metadata metadata,
        Data data,
        Map<AttributeName<?>, Object> values
    ) implements Model {

        @Override
        @SuppressWarnings("unchecked")
        public <T> T value(AttributeName<T> name) {
            return (T) this.values.get(name);
        }
    }
}
