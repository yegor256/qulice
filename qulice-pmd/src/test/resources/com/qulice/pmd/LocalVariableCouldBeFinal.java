package foo;

public final class LocalVariableCouldBeFinal {

    public int method() {
        int nonfinal = 0;
        return nonfinal;
    }
}
