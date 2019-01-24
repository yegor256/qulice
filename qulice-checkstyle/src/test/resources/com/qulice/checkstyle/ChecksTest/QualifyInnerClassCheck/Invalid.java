/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {

    public void method(int num) {
        new Bar(num);
    }

    private static final class Bar {

        private final int member;

        Bar(int num) {
            this.member = num;
        }
    }
}
