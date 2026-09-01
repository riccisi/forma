package it.riccisi.forma;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.scalar.FirstOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

import java.util.NoSuchElementException;

/**
 * Property addressed by a representation coordinate inside data.
 *
 * <p>The lookup itself is modeled as a property. Resolution is derived from the
 * iterable {@link Data} contract rather than being a responsibility of Data.
 */
@RequiredArgsConstructor
public final class PropertyAt implements Property {

    @NonNull private final Scalar<Property> property;

    public PropertyAt(final PropertyReference reference, final Data data) {
        this(
            new Sticky<>(
                new FirstOf<>(
                    prop -> prop.reference().equals(reference),
                    data,
                    () -> {
                        throw new NoSuchElementException(
                            "No property exists at the supplied reference"
                        );
                    }
                )
            )
        );
    }

    @Override
    public PropertyReference reference() {
        return this.property().reference();
    }

    @Override
    public PropertyValue value() {
        return this.property().value();
    }

    private Property property() {
        return new Unchecked<>(this.property).value();
    }
}