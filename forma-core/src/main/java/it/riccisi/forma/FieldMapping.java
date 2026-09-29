package it.riccisi.forma;

/**
 * Relates semantic attribute names to representation field references.
 */
public interface FieldMapping {

    FieldReference reference(AttributeName<?> attribute);
}
