package com.leetquery.backend;

import com.leetquery.backend.model.QueryResponse;
import com.leetquery.backend.service.QueryExecutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QueryExecutionServiceTest {

    @Autowired
    private QueryExecutionService queryExecutionService;

    @Test
    void testSelectQuery() throws Exception {
        String query = "SELECT * FROM students LIMIT 5;";
        QueryResponse response = queryExecutionService.executeQuery(query);
        
        assertTrue(response.isSuccess());
        assertEquals("SELECT", response.getQueryType());
        assertNotNull(response.getHeaders());
        assertNotNull(response.getRows());
        assertTrue(response.getRowCount() <= 5);
    }

    @Test
    void testShowTables() throws Exception {
        String query = "SHOW TABLES;";
        QueryResponse response = queryExecutionService.executeQuery(query);
        
        assertTrue(response.isSuccess());
        assertEquals("SHOW", response.getQueryType());
        assertNotNull(response.getRows());
        assertTrue(response.getRowCount() >= 5); // Should have at least 5 tables
    }

    @Test
    void testInsertQuery() throws Exception {
        String query = "INSERT INTO students (name, department_id, gpa, enrollment_year) VALUES ('Test Student', 1, 3.5, 2024);";
        QueryResponse response = queryExecutionService.executeQuery(query);
        
        assertTrue(response.isSuccess());
        assertEquals("INSERT", response.getQueryType());
        assertEquals(1, response.getRowCount());
    }
}
