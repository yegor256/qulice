package foo;

public final class StringLengthEqualsZeroMethod {

    private final String somestring;

    public StringLengthEqualsZeroMethod(final String str) {
        this.somestring = str;
    }

    public String someMethod() {
        return this.somestring;
    }

    public boolean sizeGreaterOrEqualOne() {
        return this.someMethod().length() >= 1;
    }
}
