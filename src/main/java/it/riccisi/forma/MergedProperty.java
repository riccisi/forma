package it.riccisi.forma;

import java.util.Objects;
import lombok.NonNull;

/**
 * Property resulting from overlaying one represented property over another.
 *
 * <p>The two properties must identify the same representation coordinate.
 * Composition itself does not observe either value. The overlay value becomes
 * effective only when this property's value is observed.
 */
public final class MergedProperty implements Property {

    private final Property base;
    private final Property overlay;

    public MergedProperty(
        @NonNull final Property base,
        @NonNull final Property overlay
    ) {
        if (!Objects.equals(base.reference(), overlay.reference())) {
            throw new IllegalArgumentException(
                "Cannot merge properties with different references"
            );
        }
        this.base = base;
        this.overlay = overlay;
    }

    @Override
    public PropertyReference reference() {
        return this.overlay.reference();
    }

    @Override
    public PropertyValue value() {
        return this.overlay.value();
    }
}
