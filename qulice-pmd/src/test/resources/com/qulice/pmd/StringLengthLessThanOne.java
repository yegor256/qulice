package foo;

public final class StringLengthLessThanOne {

    private final String somestring;

    public StringLengthLessThanOne(final String str) {
        this.somestring = str;
    }

    public String someMethod() {
        return this.somestring;
    }

    public boolean lengthOnMethodWithThis() {
        return this.someMethod().length() < 1;
    }

    public boolean lengthOnMethodWithThisInversed() {
        return 1 > this.someMethod().length();
    }

    public boolean lengthOnFieldWithThis() {
        return this.somestring.length() < 1;
    }

    public boolean lengthOnFieldWithThisInversed() {
        return 1 > this.somestring.length();
    }

    public boolean lengthOnVariable(final String somestring) {
        return somestring.length() < 1;
    }

}
