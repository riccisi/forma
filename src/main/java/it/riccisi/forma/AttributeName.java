package it.riccisi.forma;

import org.cactoos.Text;

/**
 * Typed semantic name of an attribute within metadata.
 *
 * <p>An attribute name is a value object rather than a representation
 * coordinate. Its textual value identifies the attribute inside a semantic
 * {@link Metadata}, while the generic parameter lets APIs express the expected
 * semantic value type without casts at the call site.
 *
 * @param <T> semantic value type
 */
public interface AttributeName<T> extends Text {
}
