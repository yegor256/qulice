/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class ValidTest {
    private static final String TEST = "test";
    public static String name() {
      return "test";
    }
    public String test() {
        return this.TEST;
    }
}

public interface Foo {
    void func();
}

public interface Foo {
    @Test
    public void someTest() {
        // this method is not static, but it's a unit test
    }
}

public class Bar {
    @Override
    public void someOverrideMethod() {
        // this method is not static, but it has "@Override" annotation
    }

    // this method is not static, but it is abstract
    public abstract void someAbstractMethod();

    // this method is not static, but it is native
    public native void someNativeMethod();

    public void someUnimplementedMethod() {
        // this method is not yet implemented
        throw new UnsupportedOperationException();
    }

    public void anotherThrowsExceptionMethod() {
        // this methods throws an exception with a description that spans
        // more than one and and has a commented out line of code.
        // throw new OldException();
        throw new IllegalStateException("This method is not to be called," +
            " because it has a really long description that spans more than" +
            " one line.");
    }
}
