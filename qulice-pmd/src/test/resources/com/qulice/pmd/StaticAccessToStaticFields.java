package foo;

public final class StaticAccessToStaticFields {
    private static int num = 1;

    public static int number() {
        return StaticAccessToStaticFields.num;
    }

    public int another() {
        return 0;
    }
}
