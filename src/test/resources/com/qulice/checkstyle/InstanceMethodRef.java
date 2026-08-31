/*
 * Hello.
 */
package foo;
/**
 * Simple.
 * @since 1.0
 */
public final class InstanceMethodRef {

    /**
     * Ctor.
     */
    public InstanceMethodRef() {
        // nothing
    }

    private void start() {
        Collections.singletonList("1")
            .forEach(this::doSomething);
    }

    private static void doSomething(final String value) {
        System.out.println(value);
    }
}
