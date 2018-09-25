package foo;

public final class StringFromMethodLength {

    public boolean sizeGreaterOrEqualOne() {
        return getString().length() >= 1;
    }

    public String getString() {
        return "somestring";
    }

}
