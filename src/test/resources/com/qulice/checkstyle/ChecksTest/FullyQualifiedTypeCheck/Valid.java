/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
import java.awt.List;
import java.util.Map;
public final class Valid {
    private static final int MAX = 10;
    private final List names = null;
    private final java.util.List<String> spans = null;
    private final Map<String, Integer> pairs = null;
    public String text() {
        return java.lang.String.valueOf(Valid.MAX);
    }
    public void print() {
        System.out.println(this.names.toString());
    }
    public com.qulice.checkstyle.Valid self() {
        return this;
    }
    public int limit(Holder holder) {
        return holder.limits.MAX;
    }
    public Object inner() {
        return Valid.Nested.EMPTY;
    }
    private static final class Nested {
        private static final Object EMPTY = null;
    }
}
