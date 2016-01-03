package foo;

public final class CodeInConstructor {
    private final transient int number;
    private final transient int another;

    public CodeInConstructor() {
        this.number = 2;
        int a = number + 3;
        this.another = a;
    }

    public int num() {
        return number + another;
    }
}
