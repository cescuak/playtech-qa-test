package com.cescuakristiin.playtech.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestOutput {

    private final StringBuilder sb = new StringBuilder();

    public void line(String text) {
        sb.append(text).append(System.lineSeparator());
        System.out.println(text);
    }

    public void section(String title) {
        line("");
        line("==== " + title + " ====");
    }

    public Path writeToFile(String relativePath) throws IOException {
        Path path = Paths.get(relativePath);
        Files.createDirectories(path.getParent());

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String header = "Generated: " + timestamp + System.lineSeparator() + System.lineSeparator();

        Files.writeString(
                path,
                header + sb,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        return path;
    }
}