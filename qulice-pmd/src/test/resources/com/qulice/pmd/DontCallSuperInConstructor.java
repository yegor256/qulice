package foo;

public final class DontCallSuperInConstructor {

    private final int number;
    private final int other;

    public DontCallSuperInConstructor(final int parameter) {
        this(parameter, parameter * 2);
    }

    public DontCallSuperInConstructor(final int parameter, final int other) {
        this.number = parameter;
        this.another = other;
    }

    public int num() {
        return number + another;
    }
}