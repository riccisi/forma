package it.riccisi.forma;

import lombok.NonNull;

/**
 * Failure to establish one semantic attribute while constructing a model.
 *
 * <p>The failure enriches the originating exception with the semantic attribute
 * and representation coordinate involved in the attempted binding. The original
 * cause remains available without requiring lower-level representation or
 * attribute objects to know about model construction.
 */
public final class BindingFailure extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    @NonNull private final AttributeName<?> attribute;
    @NonNull private final PropertyReference property;

    /**
     * New contextual binding failure.
     *
     * @param attribute semantic attribute being established
     * @param property representation coordinate selected for that attribute
     * @param cause originating lookup, interpretation, or semantic failure
     */
    public BindingFailure(
        @NonNull final AttributeName<?> attribute,
        @NonNull final PropertyReference property,
        @NonNull final RuntimeException cause
    ) {
        super("Unable to bind the semantic attribute from the represented property", cause);
        this.attribute = attribute;
        this.property = property;
    }

    /**
     * Semantic attribute involved in the failed binding.
     *
     * @return semantic attribute name
     */
    public AttributeName<?> attribute() {
        return this.attribute;
    }

    /**
     * Representation coordinate involved in the failed binding.
     *
     * @return represented property coordinate
     */
    public PropertyReference property() {
        return this.property;
    }
}
