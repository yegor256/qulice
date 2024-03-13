package foo;

public final class StaticAccessToStaticFields {
    private static int num = 1;

    private static int number() {
        return StaticAccessToStaticFields.num;
    }

    public int another() {
        return 0;
    }

    public int addToNum(final int another) {
        return another + StaticAccessToStaticFields.number() + this.another();
    }

    class InternalClass {
        final int num;

        InternalClass(final int par) {
            this.num = par;
        }
        static int another() {
            return 1;
        }

        public int add(final int a) {
            return a + this.num;
        }
    }
}
