/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public interface ValidTypes {
    int one();
    enum Kind {
        FIRST, SECOND
    }
    record Pair(String left, String right) {
    }
    @interface Marker {
    }
}
