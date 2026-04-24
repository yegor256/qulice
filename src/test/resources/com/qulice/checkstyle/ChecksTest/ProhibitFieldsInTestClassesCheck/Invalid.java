/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class InvalidTest {
    public File tempFile;
    private Object mock = Mockito.mock(Object.class);
    protected String name;

    @Test
    public void test() {
        tempFile.createNewFile();
    }
}
