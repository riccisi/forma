package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

final class HashtableDataTest {

    @Test
    void resolvesValuesThroughGenericIteration() throws Exception {
        final PropertyReference name = new NamedReference("name");
        final PropertyReference age = new PositionalReference(1);
        final Data data = new HashtableData(
            Map.of(
                name, new TextValue(new TextOf("Alice")),
                age, new NumberValue(42)
            )
        );

        assertEquals("Alice", propertyAt(data, name).value().asText().asString());
        assertEquals(42, propertyAt(data, age).value().asNumber().intValue());
    }

    @Test
    void participatesInThePropertyMappingProtocol() throws Exception {
        final AttributeName<String> email = new SemanticName();
        final PropertyReference field = new NamedReference("e_mail_address");
        final Data data = new HashtableData(
            Map.of(field, new TextValue(new TextOf("alice@example.com")))
        );
        final PropertyMapping mapping = attribute -> field;

        assertEquals(
            "alice@example.com",
            propertyAt(data, mapping.property(email)).value().asText().asString()
        );
    }

    @Test
    void propertiesCarryTheirRepresentationCoordinates() {
        final PropertyReference status = new NamedReference("status");
        final Data data = new HashtableData(
            Map.of(status, new TextValue(new TextOf("ACTIVE")))
        );

        assertEquals(status, data.iterator().next().reference());
    }

    @Test
    void preservesInformationNotConsumedByASemanticBinding() throws Exception {
        final PropertyReference status = new NamedReference("status");
        final PropertyReference description = new NamedReference("description");
        final Data data = new HashtableData(
            Map.of(
                status, new TextValue(new TextOf("ACTIVE")),
                description, new TextValue(new TextOf("Imported externally"))
            )
        );

        assertEquals("ACTIVE", propertyAt(data, status).value().asText().asString());
        assertEquals(
            "Imported externally",
            propertyAt(data, description).value().asText().asString()
        );
        assertEquals(2L, StreamSupport.stream(data.spliterator(), false).count());
    }

    @Test
    void genericLookupFailsWhenReferenceIsAbsent() {
        final Data data = new HashtableData(Map.of());

        assertThrows(
            NoSuchElementException.class,
            () -> propertyAt(data, new NamedReference("missing"))
        );
    }

    private static Property propertyAt(
        final Data data,
        final PropertyReference reference
    ) {
        return StreamSupport.stream(data.spliterator(), false)
            .filter(property -> property.reference().equals(reference))
            .findFirst()
            .orElseThrow();
    }

    private record SemanticName() implements AttributeName<String> {
    }

    private record NamedReference(String value) implements PropertyReference {
    }

    private record PositionalReference(int value) implements PropertyReference {
    }
}
