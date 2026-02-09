package com.leetquery.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        // Read MYSQL_URL from environment variable at runtime
        String mysqlUrl = System.getenv("MYSQL_URL");
        
        if (mysqlUrl == null || mysqlUrl.isEmpty()) {
            throw new IllegalStateException("MYSQL_URL environment variable is not set");
        }
        
        // Create DriverManagerDataSource
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        // Set the JDBC URL directly (includes user and password as query parameters)
        dataSource.setUrl(mysqlUrl);
        
        // Set the MySQL driver
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        return dataSource;
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

