package it.riccisi.forma;

import it.riccisi.forma.attribute.AttributeNameOf;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class AttributeNameOfTest {

    @Test
    void identifiesEqualNamesByText() {
        assertThat(new AttributeNameOf<String>("email"), equalTo(new AttributeNameOf<>("email")));
    }

    @Test
    void equalNamesHaveEqualHashCodes() {
        assertThat(
            new AttributeNameOf<String>("email").hashCode(),
            equalTo(new AttributeNameOf<String>("email").hashCode())
        );
    }

    @Test
    void distinguishesDifferentNames() {
        assertThat(
            new AttributeNameOf<String>("email"),
            not(equalTo(new AttributeNameOf<String>("name")))
        );
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new AttributeNameOf<String>("   "));
    }

    @Test
    void rejectsSurroundingWhitespace() {
        assertThrows(IllegalArgumentException.class, () -> new AttributeNameOf<String>(" email "));
    }
}
