package foo;

public final class StringLengthEqualsZero {

    private final String somestring;

    public StringLengthEqualsZero(final String str) {
        this.somestring = str;
    }

    public boolean sizeIsZero() {
        return somestring.length() == 0;
    }

}
