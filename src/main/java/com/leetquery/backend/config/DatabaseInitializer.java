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
