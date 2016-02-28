package foo;

public final class StaticPublicMethod {

    private FieldInitConstructor() {
        super();
    }

    public static StaticPublicMethod create() {
        return new StaticPublicMethod();
    }

}
