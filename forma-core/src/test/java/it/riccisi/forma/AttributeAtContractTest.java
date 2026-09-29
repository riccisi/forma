package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeAt;
import it.riccisi.forma.attribute.AttributeNameOf;
import it.riccisi.forma.attribute.StringAttribute;
import it.riccisi.forma.field.FieldOf;
import it.riccisi.forma.field.NamedReference;
import it.riccisi.forma.field.TextValue;
import it.riccisi.forma.metadata.MetadataOf;
import java.util.NoSuchElementException;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class AttributeAtContractTest {

    @Test
    void resolvesAttributeBySemanticName() {
        assertThat(
            new AttributeAt<String>(
                new AttributeNameOf<>("name"),
                new MetadataOf(
                    new StringAttribute(new AttributeNameOf<>("name"))
                )
            ).valueFrom(
                new FieldOf(
                    new NamedReference("name"),
                    new TextValue(new TextOf("Ada"))
                )
            ),
            equalTo("Ada")
        );
    }

    @Test
    void failsWhenSemanticNameIsAbsent() {
        assertThrows(
            NoSuchElementException.class,
            () -> new AttributeAt<String>(
                new AttributeNameOf<>("name"),
                new MetadataOf()
            ).name()
        );
    }
}
