package foo;

public final class FieldInitNoConstructor {
    private final transient int number = 1;

    public int num() {
        return number;
    }
}
