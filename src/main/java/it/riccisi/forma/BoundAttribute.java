package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Successfully bound semantic attribute value.
 *
 * @param <T> semantic value type
 */
@RequiredArgsConstructor
public final class BoundAttribute<T> implements ModelAttribute<T> {

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
