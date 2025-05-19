/*
 * Hello.
 */
package foo;

/**
 * Simple.
 * @since 1.0
 */
public final class ValidLambdaAndGenericsAtEndOfLine {

    /**
     * Final.
     */
    private final int zero = 0;

    /**
     * Main method.
     */
    public void main() {
        final Proc proc = () ->
            this.zero;
        final Func func = var ->
            var * 2;
        final BiFunc bifunc = (param, par) ->
            param * par;
        final List<Integer>
            list = new ArrayList<>();
    }
}
