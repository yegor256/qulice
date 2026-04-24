/*
 * Hello.
 */
package foo;
/**
 * Allows 'id' as a method name.
 * @since 1.0
 */
public final class IdMethodName {

    /**
     * The id of this object.
     */
    private final int number;

    /**
     * Ctor.
     * @param num The id value
     */
    public IdMethodName(final int num) {
        this.number = num;
    }

    /**
     * Returns the id of this object.
     * @return The id
     */
    public int id() {
        return this.number;
    }
}
