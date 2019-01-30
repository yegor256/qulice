package foo;

public final class StringLengthMinorOrEqualsThanZero {

    private final String somestring;

    public StringLengthMinorOrEqualsThanZero(final String str) {
        this.somestring = str;
    }

    public boolean sizeMinorOrEqualsThanZero() {
        return somestring.length() <= 0;
    }

}
