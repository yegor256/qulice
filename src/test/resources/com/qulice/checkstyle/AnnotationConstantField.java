/*
 * Hello.
 */
package foo;

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
     * Some data.
     * @return Some data
     */
    public int data() {
        return this.dat + this.other;
    }

    /**
     * Some inner class.
     * @since 1.0
     */
    @SuppressWarnings(AnnotationConstantField.TEXT4)
    private static final class Inner {

        /**
         * Random source.
         */
        private final java.util.Random rnd = new java.util.Random();

        /**
         * Returns dummy.
         * @return Dummy
         */
        public int dummy() {
            return this.rnd.nextInt();
        }
    }
}
