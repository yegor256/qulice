package foo;

public final class LambdaInConstructor {
    private final transient int number;
    private final transient int another;

    public LambdaInConstructor(final int parameter) {
        this(
            parameter,
             () -> {
                final int ret;
                if (parameter % 2 == 0){
                    ret = 10;
                } else {
                    ret = 20;
                }
                return ret;
             }
        );
    }

    public LambdaInConstructor(final int parameter, final int other) {
        this.number = parameter;
        this.another = other;
    }

    public int num() {
        return number + another;
    }
}