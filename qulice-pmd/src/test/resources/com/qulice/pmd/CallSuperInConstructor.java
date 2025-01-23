package foo;

public final class CallSuperInConstructor {

    private final int number;
    private final int other;

    public CallSuperInConstructor(final int parameter) {
        this(parameter, parameter * 2);
    }

    public CallSuperInConstructor(final int parameter, final int other) {
        this.number = parameter;
        this.another = other;
    }

    public int num() {
        return number + another;
    }
}
