package foo;

public class CloneMethodMustBePublic implements Cloneable {
    @Override
    protected CloneMethodMustBePublic clone() throws CloneNotSupportedException {
        return ((CloneMethodMustBePublic) super.clone());
    }
}
