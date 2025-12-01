package com.bajaj.qualifier.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SolutionStorageService {

    private static final Logger log = LoggerFactory.getLogger(SolutionStorageService.class);
    private static final Path OUTPUT_FILE = Path.of("target", "generated-sql-solution.sql");

    public void persist(String sql) {
        try {
            Files.createDirectories(OUTPUT_FILE.getParent());
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String content = "-- Generated at " + timestamp + System.lineSeparator() + sql + System.lineSeparator();
            Files.writeString(OUTPUT_FILE, content);
            log.info("Stored SQL solution at {}", OUTPUT_FILE.toAbsolutePath());
        } catch (IOException e) {
            log.error("Unable to store SQL solution", e);
        }
    }
}
