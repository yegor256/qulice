/**
 * Hello.
 */
package foo;
/**
 * Simple.
 * @version $Id $
 * @author John Smith (john@example.com)
 * @since 1.0
 */
public final class InstanceMethodRef {
    /**
     * Start. Check fails in this method.
     */
    private void start() {
        Collections.singletonList("1")
            .forEach(this::doSomething);
    }
    /**
     * Method to be referenced.
     * @param value Value to print
     */
    private static void doSomething(final String value) {
        System.out.println(value);
    }
}

