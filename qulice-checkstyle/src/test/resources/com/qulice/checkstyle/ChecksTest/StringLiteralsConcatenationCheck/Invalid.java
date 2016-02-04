/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    public void foo1() {
	if (true) {
	    String str = "a" + "b";
	}
    }

    public void foo2() {
	String a = "a";
	String str = a + "b";
    }

    public void foo3() {
	System.out.println("File not found: " + file);
    }

    public void foo4() {
	String x = "x";
	x += "done";
    }

    public void foo5() {
        final Exception ex = new RuntimeException();
        throw new IllegalStateException("Failed to create checker"
            + " test" + ex.getMessage(), ex
        );
    }
}
