/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    public void main() {
        int works = true;
        new Take() {
            public Response act(final Request req) {
                ref.set(req);
                return new Response() {
                    public Iterable<String> head() throws IOException {
                        return Collections.singletonList("HTTP/1.1 200 OK");
                    }

                    public InputStream body() throws IOException {
                        return req.body();
                    }
                };
            }
        };
    }
}
