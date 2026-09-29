package it.riccisi.forma;

import it.riccisi.forma.exception.MissingFieldException;
import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.data.HashtableData;
import it.riccisi.forma.mapping.ExplicitMapping;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

final class HashtableDataTest {

    @Test
    void resolvesTextThroughFieldAt() {
        assertThat(
            new UncheckedText(
                new FieldAt(
                    new NamedReference("name"),
                    new HashtableData(Map.of(
                        new NamedReference("name"), new TextValue(new TextOf("Alice")),
                        new PositionalReference(1), new NumberValue(42)
                    ))
                ).value().asText()
            ).asString(),
            equalTo("Alice")
        );
    }

    @Test
    void resolvesNumberThroughFieldAt() {
        assertThat(
            new FieldAt(
                new PositionalReference(1),
                new HashtableData(Map.of(
                    new NamedReference("name"), new TextValue(new TextOf("Alice")),
                    new PositionalReference(1), new NumberValue(42)
                ))
            ).value().asNumber().intValue(),
            equalTo(42)
        );
    }

    @Test
    void participatesInTheFieldMappingProtocol() {
        assertThat(
            new UncheckedText(
                new FieldAt(
                    new ExplicitMapping(
                        Map.of(new AttributeNameOf<String>("email"),
                            new NamedReference("e_mail_address"))
                    ).reference(new AttributeNameOf<String>("email")),
                    new HashtableData(Map.of(
                        new NamedReference("e_mail_address"),
                        new TextValue(new TextOf("alice@example.com"))
                    ))
                ).value().asText()
            ).asString(),
            equalTo("alice@example.com")
        );
    }

    @Test
    void fieldsCarryTheirRepresentationCoordinates() {
        assertThat(
            new HashtableData(Map.of(
                new NamedReference("status"), new TextValue(new TextOf("ACTIVE"))
            )).iterator().next().reference(),
            equalTo(new NamedReference("status"))
        );
    }

    @Test
    void preservesUnconsumedFields() {
        assertThat(
            StreamSupport.stream(
                new HashtableData(Map.of(
                    new NamedReference("status"), new TextValue(new TextOf("ACTIVE")),
                    new NamedReference("description"),
                    new TextValue(new TextOf("Imported externally"))
                )).spliterator(), false
            ).count(),
            equalTo(2L)
        );
    }

    @Test
    void preservesFirstUnconsumedFieldValue() {
        assertThat(
            new UncheckedText(
                new FieldAt(
                    new NamedReference("status"),
                    new HashtableData(Map.of(
                        new NamedReference("status"), new TextValue(new TextOf("ACTIVE")),
                        new NamedReference("description"),
                        new TextValue(new TextOf("Imported externally"))
                    ))
                ).value().asText()
            ).asString(),
            equalTo("ACTIVE")
        );
    }

    @Test
    void preservesSecondUnconsumedFieldValue() {
        assertThat(
            new UncheckedText(
                new FieldAt(
                    new NamedReference("description"),
                    new HashtableData(Map.of(
                        new NamedReference("status"), new TextValue(new TextOf("ACTIVE")),
                        new NamedReference("description"),
                        new TextValue(new TextOf("Imported externally"))
                    ))
                ).value().asText()
            ).asString(),
            equalTo("Imported externally")
        );
    }

    @Test
    void fieldAtFailsWhenReferenceIsAbsent() {
        assertThrows(
            MissingFieldException.class,
            () -> new FieldAt(
                new NamedReference("missing"), new HashtableData(Map.of())
            ).value()
        );
    }

    private record PositionalReference(int value) implements FieldReference {
    }
}
