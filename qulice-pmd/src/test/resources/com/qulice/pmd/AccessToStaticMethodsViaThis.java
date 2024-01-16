package foo;

public final class AccessToStaticMethodsViaThis {
    private static int number() {
        return 1;
    }

    public int another() {
        return 1 + this.number();
    }
}
