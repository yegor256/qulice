package foo;

public class CloneMethodReturnTypeMustMatchClassName implements Cloneable {
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
