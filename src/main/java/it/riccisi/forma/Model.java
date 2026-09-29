package it.riccisi.forma;

/**
 * An instance of metadata over represented data.
 *
 * <p>Construction composes {@link Data} and {@link Metadata} without resolving
 * represented values. Iteration exposes the values of the metadata attributes
 * within this model.
 */
public interface Model extends Iterable<AttributeValue<?>> {

    Metadata metadata();

    Data data();
}
