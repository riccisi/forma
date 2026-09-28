package it.riccisi.forma.observation;

import it.riccisi.forma.AttributeName;
import it.riccisi.forma.PropertyReference;
import lombok.NonNull;

/**
 * Failure to establish a requested semantic observation.
 *
 * <p>The failure enriches an observation reason with the semantic attribute and
 * representation coordinate involved. The lower-level object that understands
 * the reason remains responsible for giving it meaning.
 */
public final class ObservationFailure extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    @NonNull private final AttributeName<?> attribute;
    @NonNull private final PropertyReference property;
    @NonNull private final ObservationReason reason;

    public ObservationFailure(
        @NonNull final AttributeName<?> attribute,
        @NonNull final PropertyReference property,
        @NonNull final ObservationReason reason
    ) {
        super("Unable to establish the semantic observation", reason);
        this.attribute = attribute;
        this.property = property;
        this.reason = reason;
    }

    public AttributeName<?> attribute() {
        return this.attribute;
    }

    public PropertyReference property() {
        return this.property;
    }

    public ObservationReason reason() {
        return this.reason;
    }
}
