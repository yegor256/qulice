/*
 * Hello.
 */
package foo;

/**
 * Description of the class.
 * @param <T> type of the items
 * @since 1.0
 */
public interface InvalidTypeParamDescription<T> {

    /**
     * Take an element.
     * @param <E> type of the element
     * @return The element
     */
    <E> E element();
}
