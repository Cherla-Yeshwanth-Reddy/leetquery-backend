package com.leetquery.backend.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import javax.sql.DataSource;

/**
 * Flyway Database Migration Configuration
 * 
 * Purpose: Explicit Flyway configuration for database versioning and schema management
 * 
 * Responsibilities:
 * - Ensures migrations run on application startup
 * - Validates migration history
 * - Provides clean/validate operations
 * 
 * Cluster 4: Database Migration Infrastructure
 * Production Readiness: Database schema versioning with rollback capability
 */
@Configuration
@ConditionalOnProperty(
    name = "spring.flyway.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class FlywayConfiguration {

    @Value("${spring.flyway.locations:classpath:db/migration}")
    private String migrationsLocationPath;

    @Value("${spring.flyway.baseline-on-migrate:true}")
    private boolean baselineOnMigrate;

    @Value("${spring.flyway.validate-on-migrate:true}")
    private boolean validateOnMigrate;

    @Value("${spring.flyway.clean-disabled:true}")
    private boolean cleanDisabled;

    /**
     * Configure and instantiate Flyway bean
     * 
     * This bean is created after DataSource to ensure migrations run after connection is configured
     * 
     * @param dataSource Primary datasource (PostgreSQL)
     * @return Configured Flyway instance
     */
    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway.FlywayBuilder builder = Flyway.configure()
                .dataSource(dataSource)
                .locations(migrationsLocationPath)
                .baselineOnMigrate(baselineOnMigrate)
                .validateOnMigrate(validateOnMigrate)
                .cleanDisabled(cleanDisabled)
                .schemas("public")
                .sqlMigrationPrefix("V")
                .sqlMigrationSeparator("__")
                .sqlMigrationSuffixes(".sql")
                .placeholderPrefix("${")
                .placeholderSuffix("}")
                .placeholderReplacement(true)
                .outOfOrder(false)
                .ignoreIgnoredMigrations(false)
                .loggers(org.flywaydb.core.api.logging.LoggerFactory.getLogger(FlywayConfiguration.class));

        Flyway flyway = builder.load();
        
        // Run migrations on startup
        // Migration steps:
        // 1. Check flyway_schema_history table
        // 2. Identify new migration files (V1__, V2__, etc.)
        // 3. Execute migrations in order
        // 4. Update history table
        // 5. Return validation results
        flyway.migrate();
        
        return flyway;
    }
}
