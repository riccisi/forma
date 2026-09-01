package it.riccisi.forma;

import org.cactoos.Text;

/**
 * Typed identity of a semantic attribute.
 *
 * <p>The generic parameter lets public APIs express the expected semantic value
 * type without casts at the call site. The textual name enables conventions such
 * as representation mappings, but does not by itself define semantic identity.
 * Two distinct attribute identities may therefore expose the same text.
 *
 * @param <T> semantic value type
 */
public interface AttributeName<T> {

    /**
     * Conventional textual name of this semantic attribute.
     *
     * @return textual name
     */
    Text text();
}
