/*
 * Hello.
 */
package foo;

import java.util.function.Supplier;

/**
 * Simple.
 * @since 0.24.2
 */
public final class LongLambdaInBody {

    /**
     * Build a supplier.
     * @return The supplier
     */
    public Supplier<Integer> build() {
        return () -> {
            int sum = 0;
            sum = sum + 1;
            sum = sum + 2;
            sum = sum + 3;
            sum = sum + 4;
            sum = sum + 5;
            sum = sum + 6;
            sum = sum + 7;
            sum = sum + 8;
            sum = sum + 9;
            sum = sum + 10;
            sum = sum + 11;
            sum = sum + 12;
            sum = sum + 13;
            sum = sum + 14;
            sum = sum + 15;
            sum = sum + 16;
            sum = sum + 17;
            sum = sum + 18;
            sum = sum + 19;
            sum = sum + 20;
            sum = sum + 21;
            return sum;
        };
    }
}
