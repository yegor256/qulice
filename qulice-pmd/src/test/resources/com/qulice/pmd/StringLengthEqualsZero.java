package foo;

public final class StringLengthEqualsZero {

    private final String somestring;

    public StringLengthEqualsZero(final String str) {
        this.somestring = str;
    }

    public String someMethod() {
        return this.somestring;
    }

    public boolean lengthOnMethodWithThis() {
        return this.someMethod().length() == 0;
    }

    public boolean lengthOnMethodWithThisInversed() {
        return 0 == this.someMethod().length();
    }

    public boolean lengthOnFieldWithThis() {
        return this.somestring.length() == 0;
    }

    public boolean lengthOnFieldWithThisInversed() {
        return 0 == this.somestring.length();
    }

    public boolean lengthOnVariable(final String somestring) {
        return somestring.length() == 0;
    }

}
