package com.bupt.tarecruit.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

final class TestDataWorkspace {
    private TestDataWorkspace() {
    }

    static Path copyProjectData() throws IOException {
        Path root = Files.createTempDirectory("ta-recruit-test");
        Path source = java.nio.file.Paths.get("data");
        copyDirectory(source, root.resolve("data"));
        return root;
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(path -> {
            Path relative = source.relativize(path);
            Path destination = target.resolve(relative);
            try {
                if (Files.isDirectory(path)) {
                    Files.createDirectories(destination);
                } else {
                    Files.createDirectories(destination.getParent());
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
