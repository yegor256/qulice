/*
 * Hello.
 */
package foo;

/**
 * Description of the class.
 * @param <T> Type of the items
 * @since 1.0
 */
public interface ValidTypeParamDescription<T> {

    /**
     * Take an element.
     * @param <E> Type of the element
     * @return The element
     */
    <E> E element();
}
