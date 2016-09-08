package foo;

public final class LocalStringLength {

    public boolean sizeLessThanOne() {
    	final String str = "somestring";
        return str.length() < 1;
    }

}
