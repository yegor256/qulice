/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    public void main() {
	String a = "";
	String c = a.concat("b");
    }

    public long sum(java.util.zip.ZipFile zip) {
	return zip.getEntry("routes.txt").getSize()
	    + zip.getEntry("stops.txt").getSize()
	    + zip.getEntry("trips.txt").getSize();
    }

    public int numeric() {
	return Integer.valueOf("1") + 1;
    }

    public int numericFromIterator() {
	return Integer.valueOf(list("some").iterator().next()) + 1;
    }

    private java.util.List<String> list(String value) {
	return java.util.Collections.singletonList(value);
    }
}
