package com.leetquery.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) {
        try {
            log.info("Starting database initialization...");
            
            // Check if challenges table already has data
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM challenges WHERE level_id = 0", 
                Integer.class
            );
            
            if (count != null && count > 0) {
                log.info("Database already initialized with {} challenges", count);
                return;
            }
            
            log.info("No challenges found, initializing database...");
            
            // Execute schema.sql
            executeSqlFile("schema.sql");
            
            // Execute data.sql
            executeSqlFile("data.sql");
            
            log.info("Database initialization completed successfully!");
            
        } catch (Exception e) {
            log.error("Error during database initialization", e);
            // Don't throw exception - let app start even if init fails
        }
    }
    
    private void executeSqlFile(String filename) throws Exception {
        log.info("Executing SQL file: {}", filename);
        
        ClassPathResource resource = new ClassPathResource(filename);
        String sql = new BufferedReader(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
        
        // Split by semicolon and execute each statement
        String[] statements = sql.split(";");
        for (String statement : statements) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                try {
                    jdbcTemplate.execute(trimmed);
                } catch (Exception e) {
                    log.warn("Failed to execute statement: {}", trimmed.substring(0, Math.min(50, trimmed.length())));
                    log.warn("Error: {}", e.getMessage());
                }
            }
        }
        
        log.info("Completed executing {}", filename);
    }
}
