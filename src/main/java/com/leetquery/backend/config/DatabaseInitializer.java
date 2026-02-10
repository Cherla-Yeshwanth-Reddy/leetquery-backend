package com.leetquery.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) {
        try {
            log.info("Starting database initialization check...");
            
            // Wait for DB connection to be fully ready
            log.info("Verifying database connection...");
            jdbcTemplate.execute("SELECT 1");
            
            try {
                jdbcTemplate.execute("SELECT 1 FROM tutorial_schema LIMIT 1");
                log.info("tutorial_schema table exists");
                
                // Also check if we have data for level 0
                Integer schemaCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM tutorial_schema WHERE level_id = 0", 
                    Integer.class
                );
                if (schemaCount == null || schemaCount == 0) {
                    log.info("Seeding tutorial_schema table...");
                    jdbcTemplate.execute("INSERT INTO tutorial_schema (level_id, schema_info) VALUES (0, 'Student Database Schema:\\n• Student (student_id INT PRIMARY KEY, name VARCHAR(50), age INT CHECK (age > 0), department VARCHAR(20), marks INT DEFAULT 0)')");
                }
            } catch (Exception e) {
                log.info("tutorial_schema table missing or seeding failed, creating...");
                jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tutorial_schema (id INT PRIMARY KEY AUTO_INCREMENT, level_id INT NOT NULL DEFAULT 0, schema_info TEXT NOT NULL)");
                jdbcTemplate.execute("INSERT INTO tutorial_schema (level_id, schema_info) VALUES (0, 'Student Database Schema:\\n• Student (student_id INT PRIMARY KEY, name VARCHAR(50), age INT CHECK (age > 0), department VARCHAR(20), marks INT DEFAULT 0)')");
                log.info("tutorial_schema table created and seeded!");
            }
            
            // Check if challenges table exists and has data
            boolean tablesExist = false;
            try {
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM challenges WHERE level_id = 0", 
                    Integer.class
                );
                if (count != null && count > 0) {
                    log.info("Database already initialized with {} challenges", count);
                    tablesExist = true;
                }
            } catch (Exception e) {
                log.info("Challenges table does not exist or is empty, creating...");
            }
            
            if (!tablesExist) {
                log.info("Running schema.sql and data.sql...");
                try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
                    // ScriptUtils handles splitting and complex SQL better
                    ScriptUtils.executeSqlScript(connection, new ClassPathResource("schema.sql"));
                    log.info("schema.sql executed successfully!");
                    
                    ScriptUtils.executeSqlScript(connection, new ClassPathResource("data.sql"));
                    log.info("data.sql executed successfully!");
                }
                log.info("Database initialization completed successfully!");
            }
            
        } catch (Exception e) {
            log.error("CRITICAL: Error during database initialization!", e);
        }
    }
}
