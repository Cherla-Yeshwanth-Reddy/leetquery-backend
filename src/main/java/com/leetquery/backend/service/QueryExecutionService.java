package com.leetquery.backend.service;

import com.leetquery.backend.model.QueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryExecutionService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Executes a SQL query and returns formatted results
     */
    public QueryResponse executeQuery(String query) throws SQLException {
        String trimmedQuery = query.trim();
        String queryType = detectQueryType(trimmedQuery);
        
        log.info("Executing {} query: {}", queryType, trimmedQuery);

        try {
            switch (queryType) {
                case "SELECT":
                case "SHOW":
                case "DESCRIBE":
                case "EXPLAIN":
                    return executeSelectQuery(trimmedQuery, queryType);
                    
                case "INSERT":
                case "UPDATE":
                case "DELETE":
                    return executeDMLQuery(trimmedQuery, queryType);
                    
                case "CREATE":
                case "ALTER":
                case "DROP":
                case "TRUNCATE":
                    return executeDDLQuery(trimmedQuery, queryType);
                    
                default:
                    return executeGenericQuery(trimmedQuery, queryType);
            }
        } catch (Exception e) {
            log.error("Query execution failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Executes SELECT queries and returns result set
     */
    private QueryResponse executeSelectQuery(String query, String queryType) {
        List<String> headers = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();

        jdbcTemplate.query(query, (ResultSet rs) -> {
            // Extract headers from metadata
            if (headers.isEmpty()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    headers.add(metaData.getColumnName(i));
                }
            }

            // Extract row data
            List<String> row = new ArrayList<>();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                Object value = rs.getObject(i);
                row.add(value != null ? value.toString() : "NULL");
            }
            rows.add(row);
        });

        return QueryResponse.builder()
                .success(true)
                .queryType(queryType)
                .headers(headers)
                .rows(rows)
                .rowCount(rows.size())
                .message(rows.size() + " row(s) returned")
                .build();
    }

    /**
     * Executes INSERT, UPDATE, DELETE queries
     */
    private QueryResponse executeDMLQuery(String query, String queryType) {
        int affectedRows = jdbcTemplate.update(query);

        return QueryResponse.builder()
                .success(true)
                .queryType(queryType)
                .headers(List.of("Result"))
                .rows(List.of(List.of(affectedRows + " row(s) affected")))
                .rowCount(affectedRows)
                .message(affectedRows + " row(s) affected")
                .build();
    }

    /**
     * Executes DDL queries (CREATE, ALTER, DROP, etc.)
     */
    private QueryResponse executeDDLQuery(String query, String queryType) {
        jdbcTemplate.execute(query);

        return QueryResponse.builder()
                .success(true)
                .queryType(queryType)
                .headers(List.of("Result"))
                .rows(List.of(List.of(queryType + " statement executed successfully")))
                .rowCount(0)
                .message(queryType + " statement executed successfully")
                .build();
    }

    /**
     * Executes other query types
     */
    private QueryResponse executeGenericQuery(String query, String queryType) {
        jdbcTemplate.execute(query);

        return QueryResponse.builder()
                .success(true)
                .queryType(queryType)
                .headers(List.of("Result"))
                .rows(List.of(List.of("Query executed successfully")))
                .rowCount(0)
                .message("Query executed successfully")
                .build();
    }

    /**
     * Detects the type of SQL query
     */
    private String detectQueryType(String query) {
        String upperQuery = query.toUpperCase().trim();
        
        if (upperQuery.startsWith("SELECT")) return "SELECT";
        if (upperQuery.startsWith("INSERT")) return "INSERT";
        if (upperQuery.startsWith("UPDATE")) return "UPDATE";
        if (upperQuery.startsWith("DELETE")) return "DELETE";
        if (upperQuery.startsWith("CREATE")) return "CREATE";
        if (upperQuery.startsWith("ALTER")) return "ALTER";
        if (upperQuery.startsWith("DROP")) return "DROP";
        if (upperQuery.startsWith("TRUNCATE")) return "TRUNCATE";
        if (upperQuery.startsWith("SHOW")) return "SHOW";
        if (upperQuery.startsWith("DESCRIBE") || upperQuery.startsWith("DESC")) return "DESCRIBE";
        if (upperQuery.startsWith("EXPLAIN")) return "EXPLAIN";
        
        return "UNKNOWN";
    }
}
