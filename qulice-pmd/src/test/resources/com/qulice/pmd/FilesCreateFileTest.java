package foo;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class FilesCreateFileTest {
    @Test
    public void test() {
        Files.createFile(Paths.get("test"));
    }
}
