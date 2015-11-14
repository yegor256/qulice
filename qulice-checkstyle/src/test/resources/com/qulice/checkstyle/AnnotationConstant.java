/**
 * Hello.
 */
package foo;

/**
 * Simple.
 * @version $Id$
 * @author John Smith (john@example.com)
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
     * Some data.
     * @return Some data.
     */
    @SuppressWarnings(AnnotationConstant.TEXT1)
    public String data() {
        return "some data1";
    }

    /**
     * Some other data.
     * @return Some other data.
     */
    @SuppressWarnings(value = AnnotationConstant.TEXT2)
    public String other() {
        return "some data2";
    }

    /**
     * Some other data.
     * @return Some other data.
     */
    @SuppressWarnings({ AnnotationConstant.TEXT3, AnnotationConstant.TEXT4})
    public String other() {
        return "some data3";
    }

    /**
     * Some other data.
     * @return Some other data.
     */
    @SuppressWarnings(value = { AnnotationConstant.TEXT5,
        AnnotationConstant.TEXT6 })
    public String other() {
        return "some data4";
    }
}

