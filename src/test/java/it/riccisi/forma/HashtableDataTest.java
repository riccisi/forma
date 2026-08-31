package it.riccisi.forma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

final class HashtableDataTest {

    @Test
    void resolvesValuesThroughOpaquePropertyReferences() throws Exception {
        final PropertyReference name = new NamedReference("name");
        final PropertyReference age = new PositionalReference(1);
        final Data data = new HashtableData(
            Map.of(
                name, new TextValue(new TextOf("Alice")),
                age, new NumberValue(42)
            )
        );

        assertEquals("Alice", data.property(name).value().asText().asString());
        assertEquals(42, data.property(age).value().asNumber().intValue());
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

        assertEquals("ACTIVE", data.property(status).value().asText().asString());
        assertEquals(
            "Imported externally",
            data.property(description).value().asText().asString()
        );
        assertEquals(2L, data.spliterator().getExactSizeIfKnown());
    }

    @Test
    void failsWhenTheRepresentationDoesNotContainTheReference() {
        final Data data = new HashtableData(Map.of());

        assertThrows(
            IllegalArgumentException.class,
            () -> data.property(new NamedReference("missing"))
        );
    }

    private record NamedReference(String value) implements PropertyReference {
    }

    private record PositionalReference(int value) implements PropertyReference {
    }
}
