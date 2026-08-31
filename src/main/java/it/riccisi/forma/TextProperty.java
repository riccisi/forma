package it.riccisi.forma;

import org.cactoos.Text;

/**
 * A property whose represented information can be interpreted as text.
 *
 * <p>This is a representation capability, not a business-semantic type. JSON
 * strings, JDBC character values, POJO strings, and other representations may
 * implement this capability without exposing their concrete representation to
 * semantic attributes.
 */
public interface TextProperty extends Property {

    /**
     * Returns this property's textual representation.
     *
     * @return represented text
     */
    Text text();
}
