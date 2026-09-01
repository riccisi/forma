package it.riccisi.forma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class AttributeNameOfTest {

    @Test
    void identifiesEqualNamesByText() {
        final AttributeName<String> first = new AttributeNameOf<>("email");
        final AttributeName<String> second = new AttributeNameOf<>("email");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void distinguishesDifferentNames() {
        assertNotEquals(
            new AttributeNameOf<String>("email"),
            new AttributeNameOf<String>("name")
        );
    }

    @Test
    void rejectsBlankName() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new AttributeNameOf<String>("   ")
        );
    }

    @Test
    void rejectsSurroundingWhitespace() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new AttributeNameOf<String>(" email ")
        );
    }
}
