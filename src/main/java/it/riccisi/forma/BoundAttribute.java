package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Internal immutable result of a successful semantic binding.
 *
 * <p>This object is deliberately not part of the public construction API.
 * Attributes own interpretation and validation; this type only preserves the
 * already-established semantic result inside a {@link Model}.
 *
 * @param <T> semantic value type
 */
@RequiredArgsConstructor
final class BoundAttribute<T> implements ModelAttribute<T> {

    @NonNull private final AttributeName<T> name;
    @NonNull private final T value;

    @Override
    public AttributeName<T> name() {
        return this.name;
    }

    @Override
    public T value() {
        return this.value;
    }
}
