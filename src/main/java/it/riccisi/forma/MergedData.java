package it.riccisi.forma;

import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Data obtained by overlaying one representation over another.
 *
 * <p>Properties are related by {@link PropertyReference} equality. A property
 * present only in the base or overlay is preserved as-is. When both sources
 * contain the same coordinate, iteration exposes a {@link MergedProperty}.
 *
 * <p>Merge is observational: constructing or iterating this object does not
 * observe property values and does not establish semantic validity.
 */
@RequiredArgsConstructor
public final class MergedData implements Data {

    @NonNull
    private final Data base;

    @NonNull
    private final Data overlay;

    @Override
    public Iterator<Property> iterator() {
        return new MergedIterator(this.base, this.overlay);
    }

    /**
     * Iterator preserving base order and appending overlay-only properties.
     */
    private static final class MergedIterator implements Iterator<Property> {

        private final Data base;
        private final Data overlay;
        private final Iterator<Property> originals;
        private final Iterator<Property> additions;
        private Property next;

        private MergedIterator(final Data base, final Data overlay) {
            this.base = base;
            this.overlay = overlay;
            this.originals = base.iterator();
            this.additions = overlay.iterator();
        }

        @Override
        public boolean hasNext() {
            if (this.next == null) {
                this.next = this.find();
            }
            return this.next != null;
        }

        @Override
        public Property next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final Property current = this.next;
            this.next = null;
            return current;
        }

        private Property find() {
            if (this.originals.hasNext()) {
                final Property property = this.originals.next();
                final Property replacement = this.at(property.reference());
                if (replacement == null) {
                    return property;
                }
                return new MergedProperty(property, replacement);
            }
            while (this.additions.hasNext()) {
                final Property property = this.additions.next();
                if (!this.contains(property.reference())) {
                    return property;
                }
            }
            return null;
        }

        private Property at(final PropertyReference reference) {
            for (final Property property : this.overlay) {
                if (property.reference().equals(reference)) {
                    return property;
                }
            }
            return null;
        }

        private boolean contains(final PropertyReference reference) {
            for (final Property property : this.base) {
                if (property.reference().equals(reference)) {
                    return true;
                }
            }
            return false;
        }
    }
}
