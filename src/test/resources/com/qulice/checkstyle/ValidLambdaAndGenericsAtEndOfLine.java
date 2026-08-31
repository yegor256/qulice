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
     * Ctor.
     */
    public ValidLambdaAndGenericsAtEndOfLine() {
        // nothing
    }

    /**
     * Main method.
     */
    public void main() {
        final Proc proc = () ->
            this.zero;
        final Func func = num ->
            num * 2;
        final BiFunc bifunc = (param, par) ->
            param * par;
        final List<Integer>
            list = new ArrayList<>();
        list.add(proc.hashCode() + func.hashCode() + bifunc.hashCode());
    }
}
