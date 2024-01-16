package foo;

public final class AccessToStaticFieldsViaThis {
    private static final int num = 1;

    public int number() {
        return this.num;
    }
}
