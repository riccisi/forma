package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Property at one coordinate of two composed Data representations.
 *
 * <p>The overlay has precedence when it represents this coordinate; otherwise
 * the value is obtained from the base. Neither source value is observed until
 * {@link #value()} is requested.
 */
@RequiredArgsConstructor
public final class MergedProperty implements Property {

    @NonNull
    private final PropertyReference reference;

    @NonNull
    private final Data base;

    @NonNull
    private final Data overlay;

    @Override
    public PropertyReference reference() {
        return this.reference;
    }

    @Override
    public PropertyValue value() {
        Property property = new PropertyAt(this.reference, this.overlay);
        try {
            property.reference();
        } catch (final MissingProperty missing) {
            property = new PropertyAt(this.reference, this.base);
        }
        return property.value();
    }
}
