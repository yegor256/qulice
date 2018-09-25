/*
 * Hello.
 */
package foo;

/**
 * Simple.
 * @since 1.0
 */
public final class AnnotationConstant {
    /**
     * Description of annotation.
     */
    private static final String TEXT1 = "some static text1";

    /**
     * Description of annotation.
     */
    private static final String TEXT2 = "some static text2";

    /**
     * Description of annotation.
     */
    private static final String TEXT3 = "some static text3";

    /**
     * Description of annotation.
     */
    private static final String TEXT4 = "some static text4";

    /**
     * Description of annotation.
     */
    private static final String TEXT5 = "some static text5";

    /**
     * Description of annotation.
     */
    private static final String TEXT6 = "some static text6";

    /**
     * Dummy data.
     */
    private int dat = 1;

    /**
     * Some data.
     * @return Some data.
     */
    @SuppressWarnings(AnnotationConstant.TEXT1)
    public static String data() {
        return "some data1";
    }

    /**
     * Some other data.
     * @return Some other data.
     */
    @SuppressWarnings(value = AnnotationConstant.TEXT2)
    public static String other() {
        return "some data2";
    }

    /**
     * Some other data.
     * @return Some other data.
     */
    @SuppressWarnings({ AnnotationConstant.TEXT3, AnnotationConstant.TEXT4})
    public static String other() {
        return "some data3";
    }

    /**
     * Some other data.
     * @return Some other data.
     */
    @SuppressWarnings(value = { AnnotationConstant.TEXT5,
        AnnotationConstant.TEXT6 })
    public String other() {
        return String.valueOf(this.dat);
    }
}

