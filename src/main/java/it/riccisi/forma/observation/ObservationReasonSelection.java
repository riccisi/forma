package it.riccisi.forma.observation;

/**
 * Interpretation of the fundamental reasons a semantic observation may fail.
 *
 * @param <T> interpretation result type
 */
public interface ObservationReasonSelection<T> {

    T missingProperty();

    T uninterpretableValue();

    T rejectedValue();
}
