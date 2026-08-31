package it.riccisi.forma;

/**
 * A typed witness that a generic represented property exposes a particular
 * interpretation capability.
 *
 * <p>The witness keeps runtime capability recognition outside semantic
 * attributes and outside metadata binding code. Concrete capability witnesses
 * may use the Java type system of the representation-facing interfaces to
 * establish the requested capability.
 *
 * @param <P> represented property capability
 */
public interface PropertyCapability<P extends Property> {

    /**
     * Requires the requested capability from a generic property.
     *
     * @param property represented property
     * @return the same represented property viewed through this capability
     * @throws IllegalArgumentException when the property does not expose it
     */
    P require(Property property);
}
