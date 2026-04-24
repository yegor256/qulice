/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
@RunWith(Parameterized.class)
public class ValidTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @ClassRule
    public static TemporaryFolder shared = new TemporaryFolder();

    @Parameter
    public int parameter;

    @Mock
    private Service service;

    private static final String CONSTANT = "hello";
    private static int counter;

    @Test
    public void test() {
        Object mock = Mockito.mock(Object.class);
    }
}
