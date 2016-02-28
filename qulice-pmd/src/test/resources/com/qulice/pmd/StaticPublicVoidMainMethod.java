package foo;

public class StaticPublicVoidMainMethod {

    public static final InnerClass INNER = new InnerClass(10);

    public static void main(final String... args) {
        // allow main method
    }

    public void doNothing() {
        //do nothing here
    }

    public static class InnerClass {
        private final int number;

        public InnerClass(final int num) {
            this.number = num;
        }

        public int calculate() {
            return number;
        }
    }
}