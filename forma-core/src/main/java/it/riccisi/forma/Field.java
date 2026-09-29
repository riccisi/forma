package it.riccisi.forma;

/**
 * An addressable portion of represented data.
 *
 * <p>A field belongs to the representation side of Forma. It carries the
 * coordinate by which it is identified inside its data representation and
 * exposes its represented value through the representation-neutral
 * {@link FieldValue} abstraction.
 */
public interface Field {

    FieldReference reference();

    FieldValue value();
}
