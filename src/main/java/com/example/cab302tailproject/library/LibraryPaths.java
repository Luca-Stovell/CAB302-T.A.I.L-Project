package com.example.cab302tailproject.library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class LibraryPaths {
    private LibraryPaths() {}

    public static final Path UPLOAD_DIR = Paths.get(
            System.getProperty("user.home"), "teacher-library", "uploads");

    static {
        try { Files.createDirectories(UPLOAD_DIR); }
        catch (IOException e) {
            throw new RuntimeException("Unable to create upload directory: " + UPLOAD_DIR, e);
        }
    }
}

