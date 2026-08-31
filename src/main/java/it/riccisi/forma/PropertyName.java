package it.riccisi.forma;

/**
 * Named coordinate of a property inside a concrete data representation.
 *
 * <p>A property name belongs to the representation side of Forma and is distinct
 * from the semantic identity expressed by {@link AttributeName}. It is one
 * possible kind of {@link PropertyReference}; other representations may use
 * positions, paths, columns, or different coordinates.
 */
public interface PropertyName extends PropertyReference {
}
