package it.riccisi.forma;

/**
 * Interpretation of the fundamental reasons a represented property may fail
 * to establish a semantic model attribute.
 *
 * @param <T> interpretation result type
 */
public interface BindingReasonSelection<T> {

    T missingProperty();

    T uninterpretableValue();

    T rejectedValue();
}
