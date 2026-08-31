package it.riccisi.forma;

/**
 * Typed identity of a semantic attribute.
 *
 * <p>The generic parameter lets public APIs express the expected semantic value
 * type without casts at the call site. Runtime safety is established by
 * constructing a {@link Model} through {@link Metadata}, which must associate a
 * name only with a compatible {@link ModelAttribute}.
 *
 * @param <T> semantic value type
 */
public interface AttributeName<T> {
}
