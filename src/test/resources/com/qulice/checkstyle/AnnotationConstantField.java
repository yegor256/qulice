/*
 * Hello.
 */
package foo;

import java.util.Random;

/**
 * Simple.
 * @since 1.0
 */
@SuppressWarnings(AnnotationConstantField.TEXT1)
public final class AnnotationConstantField {

    /**
     * Used in class annotation above.
     */
    private static final String TEXT1 = "some static text1";

    /**
     * Used in field annotation below.
     */
    private static final String TEXT2 = "some static text2";

    /**
     * Used in field annotation below.
     */
    private static final String TEXT3 = "some static text3";

    /**
     * Used in inner-class annotation below.
     */
    private static final String TEXT4 = "some static text4";

    /**
     * Dummy data.
     */
    @SuppressWarnings(AnnotationConstantField.TEXT2)
    private int dat = 1;

    /**
     * Other dummy data.
     */
    @SuppressWarnings(AnnotationConstantField.TEXT3)
    private int other = 2;

    /**
     * Ctor.
     */
    public AnnotationConstantField() {
        // nothing
    }

    /**
     * Some data.
     * @return Some data
     */
    public int data() {
        return this.dat + this.other;
    }

    /**
     * All texts together, so that no constant is used only once.
     * @return All of them
     */
    public static String all() {
        return String.format(
            "%s%s%s%s",
            AnnotationConstantField.TEXT1, AnnotationConstantField.TEXT2,
            AnnotationConstantField.TEXT3, AnnotationConstantField.TEXT4
        );
    }

    /**
     * Some inner class.
     * @since 1.0
     */
    @SuppressWarnings(AnnotationConstantField.TEXT4)
    private final class Inner {

        /**
         * Random source.
         */
        private final Random rnd = new Random();

        /**
         * Returns dummy.
         * @return Dummy
         */
        public int dummy() {
            return this.rnd.nextInt();
        }
    }
}
