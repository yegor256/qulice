/*
 * Hello.
 */
package foo;

/**
 * Simple.
 * @since 1.0
 */
public final class BlankLinesOutsideMethodsPass {
    /**
     * Method with space between anonymous innter class methods.
     */
    public void methodwithAnonymousInnerClass() {
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                this.doSomething();
            }

            private void doSomething() {
            }

        };
        final Runnable runnables = new Runnable() {

            @Override
            public void run() {
                this.doSomething();
            }

            private void doSomething() {
            }

        };
    }

}
