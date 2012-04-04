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
