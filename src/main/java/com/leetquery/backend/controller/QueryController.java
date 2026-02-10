package com.leetquery.backend.controller;

import com.leetquery.backend.model.ErrorResponse;
import com.leetquery.backend.model.QueryRequest;
import com.leetquery.backend.model.QueryResponse;
import com.leetquery.backend.service.QueryExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class QueryController {

    private final QueryExecutionService queryExecutionService;

    /**
     * POST /executeQuery - Executes a SQL query and returns results
     */
    @PostMapping("/executeQuery")
    public ResponseEntity<?> executeQuery(@Valid @RequestBody QueryRequest request) {
        try {
            log.info("Received query execution request");
            QueryResponse response = queryExecutionService.executeQuery(request.getQuery());
            return ResponseEntity.ok(response);
            
        } catch (SQLException e) {
            log.error("SQL error: {}", e.getMessage());
            QueryResponse errorResponse = QueryResponse.builder()
                    .success(false)
                    .queryType("ERROR")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.ok(errorResponse);
            
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            QueryResponse errorResponse = QueryResponse.builder()
                    .success(false)
                    .queryType("ERROR")
                    .message("Internal server error: " + e.getMessage())
                    .build();
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * GET /health - Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("LeetQuery Backend is running!");
    }

    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(errorMessage)
                .sqlState(null)
                .errorCode(null)
                .queryType("VALIDATION_ERROR")
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
