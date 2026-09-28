package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.data.HashtableData;
import it.riccisi.forma.mapping.ExplicitMapping;
import it.riccisi.forma.property.NamedReference;
import it.riccisi.forma.property.NumberValue;
import it.riccisi.forma.property.PropertyAt;
import it.riccisi.forma.property.TextValue;
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
    void resolvesTextThroughPropertyAt() {
        assertThat(
            new UncheckedText(
                new PropertyAt(
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
    void resolvesNumberThroughPropertyAt() {
        assertThat(
            new PropertyAt(
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
    void participatesInThePropertyMappingProtocol() {
        assertThat(
            new UncheckedText(
                new PropertyAt(
                    new ExplicitMapping(
                        Map.of(new AttributeNameOf<String>("email"),
                            new NamedReference("e_mail_address"))
                    ).property(new AttributeNameOf<String>("email")),
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
    void propertiesCarryTheirRepresentationCoordinates() {
        assertThat(
            new HashtableData(Map.of(
                new NamedReference("status"), new TextValue(new TextOf("ACTIVE"))
            )).iterator().next().reference(),
            equalTo(new NamedReference("status"))
        );
    }

    @Test
    void preservesUnconsumedProperties() {
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
    void preservesFirstUnconsumedPropertyValue() {
        assertThat(
            new UncheckedText(
                new PropertyAt(
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
    void preservesSecondUnconsumedPropertyValue() {
        assertThat(
            new UncheckedText(
                new PropertyAt(
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
    void propertyAtFailsWhenReferenceIsAbsent() {
        assertThrows(
            MissingProperty.class,
            () -> new PropertyAt(
                new NamedReference("missing"), new HashtableData(Map.of())
            ).value()
        );
    }

    private record PositionalReference(int value) implements PropertyReference {
    }
}
