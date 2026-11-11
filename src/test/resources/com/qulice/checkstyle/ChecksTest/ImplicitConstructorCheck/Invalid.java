/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class Invalid {
    public static class Nested {
        public int one() {
            return 1;
        }
    }
    protected static class Guarded {
        public int two() {
            return 2;
        }
    }
    private static class Hidden {
        Hidden() {
        }
    }
    public Object make() {
        return new Invalid.Hidden();
    }
}
