package com.qulice.pmd;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class ValidTryWithResources {
    private static long folderSizeInMb(final Path path) throws IOException {
        try (Stream<Path> paths = Files.walk(path)) {
            return paths.filter(Files::isRegularFile).mapToLong(
                p -> {
                    try {
                        return Files.size(p);
                    } catch (final IOException exception) {
                        throw new IllegalStateException("Failed", exception);
                    }
                }
            ).sum() / 1024L / 1024L;
        }
    }
}