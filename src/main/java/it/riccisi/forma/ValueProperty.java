package it.riccisi.forma;

import lombok.RequiredArgsConstructor;

/**
 * Property backed directly by a representation reference and value.
 *
 * <p>This is the smallest concrete property useful for representation-neutral
 * examples and tests. It introduces no semantic meaning of its own.
 */
@RequiredArgsConstructor
public final class ValueProperty implements Property {

    private final PropertyReference reference;
    private final PropertyValue value;

    @Override
    public PropertyReference reference() {
        return this.reference;
    }

    @Override
    public PropertyValue value() {
        return this.value;
    }
}
