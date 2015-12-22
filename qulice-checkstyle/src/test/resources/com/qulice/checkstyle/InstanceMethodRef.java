/**
 * Hello.
 */
package foo;
/**
 * Simple.
 * @version $Id $
 * @author John Smith (john@example.com)
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
    private void doSomething(final String value) {
        System.out.println(value);
    }
}

