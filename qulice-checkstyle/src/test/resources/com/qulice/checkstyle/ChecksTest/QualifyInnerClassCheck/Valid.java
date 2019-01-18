/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {

    public void method(int num) {
        new Valid.Bar(num);
    }

    private static final class Bar {
        private final int num;

        Bar(int num) {
            this.num = num;
        }
    }
}
