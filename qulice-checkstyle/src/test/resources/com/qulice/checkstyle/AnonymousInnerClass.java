/**
 * Hello.
 */
package foo;

/**
 * Simple.
 * @author John Smith (john@example.com)
 * @version $Id$
 * @since 1.0
 */
public final class AnonymousInnerClass {
    /**
     * Method with space inbetween anonymous innter class methods.
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

        new Runnable() {
            @Override
            public void run() {
                this.doSomething();
            }

            private void doSomething() {
            }
        }.run();
    }
}
