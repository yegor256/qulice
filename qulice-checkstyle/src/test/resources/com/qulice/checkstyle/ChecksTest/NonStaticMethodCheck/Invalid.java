/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class InvalidTest {
    public String name() {
      return "test";
    }
    public String name() {
        return "this";
    }
    @Deprecated
    public String name() {
        return "method with non-overide annotation";
    }
    public synchronized String name() {
        return "method with non-native and non-abstract modifier";
    }
    public String name() {
        try {
            ThatClass.name();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
