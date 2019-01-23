package foo;

public final class StringLengthEqualsZeroThis {

    private final String somestring;

    public StringLengthEqualsZeroThis(final String str) {
        this.somestring = str;
    }

    public boolean sizeIsLessThanOne() {
        return this.somestring.length() < 1;
    }
}
