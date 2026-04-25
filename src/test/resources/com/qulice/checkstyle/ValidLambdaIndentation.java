/*
 * Hello.
 */
package foo;

import java.util.function.Supplier;

/**
 * Simple.
 * @since 1.0
 */
public final class ValidLambdaIndentation {

    /**
     * Cached.
     */
    private final Supplier<Supplier<Float>> cached;

    /**
     * Ctor.
     * @param value Value
     */
    public ValidLambdaIndentation(final float value) {
        this(
            () -> () -> new ValidLambdaIndentation.Boxed<>(
                () -> value
            ).value()
        );
    }

    /**
     * Ctor.
     * @param wrapped Wrapped
     */
    private ValidLambdaIndentation(final Supplier<Supplier<Float>> wrapped) {
        this.cached = wrapped;
    }

    /**
     * Value.
     * @return Float
     */
    public Float val() {
        return this.cached.get().get();
    }

    /**
     * Boxed.
     * @param <T> Type
     * @since 1.0
     */
    private static final class Boxed<T> {

        /**
         * Inner.
         */
        private final Supplier<T> inner;

        /**
         * Ctor.
         * @param spr Supplier
         */
        Boxed(final Supplier<T> spr) {
            this.inner = spr;
        }

        /**
         * Value.
         * @return Value
         */
        public T value() {
            return this.inner.get();
        }
    }
}
