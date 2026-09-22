package it.riccisi.forma;

import lombok.NonNull;

/**
 * Failure to establish one semantic attribute while constructing a model.
 *
 * <p>The failure enriches a semantic binding reason with the attribute and
 * representation coordinate involved in the attempted construction. The
 * lower-level object that understands the failure remains responsible for
 * giving that failure its meaning.
 */
public final class BindingFailure extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    @NonNull private final AttributeName<?> attribute;
    @NonNull private final PropertyReference property;
    @NonNull private final BindingReason reason;

    public BindingFailure(
        @NonNull final AttributeName<?> attribute,
        @NonNull final PropertyReference property,
        @NonNull final BindingReason reason
    ) {
        super("Unable to bind the semantic attribute from the represented property", reason);
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

    public BindingReason reason() {
        return this.reason;
    }
}
