-- ============================================
-- Tutorial Mode Challenges Data
-- ============================================
-- Complete extraction from SQL Tutorial Mode Blueprint
-- 12 Stages × 3 Difficulties = 36 Challenges
-- Scenario: Student Database Lifecycle
-- ============================================

-- Insert tutorial schema information
INSERT INTO tutorial_schema (level_id, schema_info) VALUES 
(0, 'Student Database Schema:\n• Student (student_id INT PRIMARY KEY, name VARCHAR(50), age INT CHECK (age > 0), department VARCHAR(20), marks INT DEFAULT 0)');

-- ============================================
-- STAGE 1: Create Table (DDL)
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 1, 'Create Table (DDL)', 'EASY', 'Easy: Create a table named ''Student'' with ''student_id'' (INT) and ''name'' (VARCHAR(50)).', 'CREATE TABLE Student (student_id INT, name VARCHAR(50))', 'Use CREATE TABLE table_name (col1 type, col2 type)', NULL, 'Table created! Start of a journey.', 'DDL'),
(0, 1, 'Create Table (DDL)', 'MEDIUM', 'Medium: Enhance ''Student'' table. Add ''age'' (INT) with a check constraint (age > 0) and ''department'' (VARCHAR(20)).', 'ALTER TABLE Student ADD age INT CHECK (age > 0), ADD department VARCHAR(20)', 'Use ALTER TABLE Student ADD col type constraint', NULL, 'Schema updated! Constraints protect data.', 'DDL'),
(0, 1, 'Create Table (DDL)', 'HARD', 'Hard: Completion. Add ''marks'' (INT) with a default value of 0, and set ''student_id'' as the PRIMARY KEY.', 'ALTER TABLE Student ADD marks INT DEFAULT 0, ADD PRIMARY KEY (student_id)', 'Use ALTER TABLE Student ADD col type DEFAULT val, ADD PRIMARY KEY (col)', NULL, 'Blueprint complete! Ready for data.', 'DDL');

-- ============================================
-- STAGE 2: Insert Data (DML)
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 2, 'Insert Data (DML)', 'EASY', 'Easy: Insert a student named ''Rahul'' (id=101, age=20, dept=''CS'', marks=85).', 'INSERT INTO Student VALUES (101, ''Rahul'', 20, ''CS'', 85)', 'Use INSERT INTO table VALUES (v1, v2...)', NULL, 'First record added.', 'DML'),
(0, 2, 'Insert Data (DML)', 'MEDIUM', 'Medium: Insert two students: ''Simran'' (102, 21, ''IT'', 90) and ''Amit'' (103, 22, ''CS'', 75).', 'INSERT INTO Student VALUES (102, ''Simran'', 21, ''IT'', 90), (103, ''Amit'', 22, ''CS'', 75)', 'Use INSERT INTO table VALUES (...), (...)', NULL, 'Bulk entry successful!', 'DML'),
(0, 2, 'Insert Data (DML)', 'HARD', 'Hard: Insert a student named ''Priya'' (104, 19, ''IT'') but leave ''marks'' to its default value.', 'INSERT INTO Student (student_id, name, age, department) VALUES (104, ''Priya'', 19, ''IT'')', 'Specify columns to skip the DEFAULT one.', NULL, 'Default values handled correctly.', 'DML');

-- ============================================
-- STAGE 3: Select Basics
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 3, 'Select Basics', 'EASY', 'Easy: Retrieve all columns for all students.', 'SELECT * FROM Student', 'Use SELECT * FROM Student', NULL, 'The whole class is here.', 'DQL'),
(0, 3, 'Select Basics', 'MEDIUM', 'Medium: Retrieve only the ''name'' and ''marks'' of all students.', 'SELECT name, marks FROM Student', 'List columns separated by commas.', NULL, 'Focused retrieval.', 'DQL'),
(0, 3, 'Select Basics', 'HARD', 'Hard: Display all unique department names from the ''Student'' table.', 'SELECT DISTINCT department FROM Student', 'Use DISTINCT keyword.', NULL, 'Department list generated.', 'DQL');

-- ============================================
-- STAGE 4: Filtering & Sorting
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 4, 'Filtering & Sorting', 'EASY', 'Easy: Find students who belong to the ''CS'' department.', 'SELECT * FROM Student WHERE department = ''CS''', 'Use WHERE clause.', NULL, 'CS Department found.', 'DQL'),
(0, 4, 'Filtering & Sorting', 'MEDIUM', 'Medium: Display students aged between 20 and 22, sorted by marks in descending order.', 'SELECT * FROM Student WHERE age BETWEEN 20 AND 22 ORDER BY marks DESC', 'Use BETWEEN and ORDER BY col DESC', NULL, 'Ranked list ready.', 'DQL'),
(0, 4, 'Filtering & Sorting', 'HARD', 'Hard: Find students whose name starts with ''A'' and have marks greater than 80.', 'SELECT * FROM Student WHERE name LIKE ''A%'' AND marks > 80', 'Use LIKE ''A%''', NULL, 'Top performers starting with A.', 'DQL');

-- ============================================
-- STAGE 5: Update & Delete
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 5, 'Update & Delete', 'EASY', 'Easy: Update Simran''s marks to 95.', 'UPDATE Student SET marks = 95 WHERE name = ''Simran''', 'Use UPDATE Student SET col = val WHERE condition', NULL, 'Simran''s marks updated.', 'DML'),
(0, 5, 'Update & Delete', 'MEDIUM', 'Medium: Increase marks by 5 for all students in the ''CS'' department.', 'UPDATE Student SET marks = marks + 5 WHERE department = ''CS''', 'Use arithmetic in SET clause.', NULL, 'CS students rewarded.', 'DML'),
(0, 5, 'Update & Delete', 'HARD', 'Hard: Delete students who have marks less than 50 (if any).', 'DELETE FROM Student WHERE marks < 50', 'Use DELETE FROM Student WHERE condition', NULL, 'Cleanup complete.', 'DML');

-- ============================================
-- STAGE 6: Aggregate & Grouping
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 6, 'Aggregate & Grouping', 'EASY', 'Easy: Calculate the average marks of all students.', 'SELECT AVG(marks) FROM Student', 'Use AVG(col) function.', NULL, 'Class average calculated.', 'DQL'),
(0, 6, 'Aggregate & Grouping', 'MEDIUM', 'Medium: Count how many students are in each department.', 'SELECT department, COUNT(*) FROM Student GROUP BY department', 'Use COUNT(*) and GROUP BY col', NULL, 'Department distribution ready.', 'DQL'),
(0, 6, 'Aggregate & Grouping', 'HARD', 'Hard: List departments where the average marks are greater than 80.', 'SELECT department, AVG(marks) FROM Student GROUP BY department HAVING AVG(marks) > 80', 'Use HAVING clause for aggregate conditions.', NULL, 'Elite departments identified.', 'DQL');

-- ============================================
-- STAGE 7: Subqueries
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 7, 'Subqueries', 'EASY', 'Easy: Find students who scored more than the overall average marks.', 'SELECT * FROM Student WHERE marks > (SELECT AVG(marks) FROM Student)', 'Place a SELECT inside the WHERE clause.', NULL, 'Above-average students.', 'DQL'),
(0, 7, 'Subqueries', 'MEDIUM', 'Medium: Find the student(s) with the highest marks without using LIMIT.', 'SELECT * FROM Student WHERE marks = (SELECT MAX(marks) FROM Student)', 'Find MAX in a subquery.', NULL, 'Topper(s) found.', 'DQL'),
(0, 7, 'Subqueries', 'HARD', 'Hard: List students who are in the same department as ''Rahul''.', 'SELECT * FROM Student WHERE department = (SELECT department FROM Student WHERE name = ''Rahul'')', 'Compare department to Rahul''s department.', NULL, 'Rahul''s colleagues.', 'DQL');

-- ============================================
-- STAGE 8: View
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 8, 'View', 'EASY', 'Easy: Create a view named ''TopStudents'' that contains only students with marks > 90.', 'CREATE VIEW TopStudents AS SELECT * FROM Student WHERE marks > 90', 'Use CREATE VIEW view_name AS SELECT...', NULL, 'View created! Virtual tables are powerful.', 'DDL'),
(0, 8, 'View', 'MEDIUM', 'Medium: Create a view named ''DeptSummary'' showing department names and their student count.', 'CREATE VIEW DeptSummary AS SELECT department, COUNT(*) as total_students FROM Student GROUP BY department', 'Views can store complex reports.', NULL, 'Reporting view ready.', 'DDL'),
(0, 8, 'View', 'HARD', 'Hard: Retrieve all data from your ''TopStudents'' view.', 'SELECT * FROM TopStudents', 'Query a view just like a table.', NULL, 'Elite list accessed via view.', 'DQL');

-- ============================================
-- STAGE 9: Index & Performance
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 9, 'Index & Performance', 'EASY', 'Easy: Create an index named ''idx_name'' on the ''name'' column.', 'CREATE INDEX idx_name ON Student(name)', 'Use CREATE INDEX index_name ON table(col)', NULL, 'Index created! Faster searches ahead.', 'DDL'),
(0, 9, 'Index & Performance', 'MEDIUM', 'Medium: Create a unique index named ''idx_id'' on ''student_id'' (even if already PK).', 'CREATE UNIQUE INDEX idx_id ON Student(student_id)', 'Use CREATE UNIQUE INDEX...', NULL, 'Uniqueness reinforced.', 'DDL'),
(0, 9, 'Index & Performance', 'HARD', 'Hard: Remove the ''idx_name'' index from the ''Student'' table.', 'DROP INDEX idx_name ON Student', 'Use DROP INDEX index_name ON table', NULL, 'Index removed.', 'DDL');

-- ============================================
-- STAGE 10: Transaction Control
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 10, 'Transaction Control', 'EASY', 'Easy: Undo the last change using ROLLBACK.', 'ROLLBACK', 'Simply type ROLLBACK', NULL, 'Transaction rolled back.', 'TCL'),
(0, 10, 'Transaction Control', 'MEDIUM', 'Medium: Create a checkpoint using SAVEPOINT sp1.', 'SAVEPOINT sp1', 'Use SAVEPOINT name', NULL, 'Checkpoint created.', 'TCL'),
(0, 10, 'Transaction Control', 'HARD', 'Hard: Save all changes permanently using COMMIT.', 'COMMIT', 'Simply type COMMIT', NULL, 'Data saved permanently!', 'TCL');

-- ============================================
-- STAGE 11: Data Control
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 11, 'Data Control', 'EASY', 'Easy: Grant SELECT on Student to a user ''guest''.', 'GRANT SELECT ON Student TO ''guest''', 'Use GRANT permission ON table TO user', NULL, 'Access granted.', 'DCL'),
(0, 11, 'Data Control', 'MEDIUM', 'Medium: Revoke the SELECT permission from ''guest''.', 'REVOKE SELECT ON Student FROM ''guest''', 'Use REVOKE permission ON table FROM user', NULL, 'Access revoked.', 'DCL'),
(0, 11, 'Data Control', 'HARD', 'Hard: Grant ALL permissions on Student to ''admin''.', 'GRANT ALL PRIVILEGES ON Student TO ''admin''', 'Use GRANT ALL PRIVILEGES...', NULL, 'Full access granted.', 'DCL');

-- ============================================
-- STAGE 12: Final Cleanup
-- ============================================

INSERT INTO challenges (level_id, stage_number, stage_title, difficulty, description, expected_query, hint, relational_algebra_hint, success_message, challenge_type) VALUES
(0, 12, 'Final Cleanup', 'EASY', 'Easy: Drop the index ''idx_id''.', 'DROP INDEX idx_id ON Student', 'Use DROP INDEX...', NULL, 'Index removed.', 'DDL'),
(0, 12, 'Final Cleanup', 'MEDIUM', 'Medium: Drop the ''DeptSummary'' view.', 'DROP VIEW DeptSummary', 'Use DROP VIEW view_name', NULL, 'View removed.', 'DDL'),
(0, 12, 'Final Cleanup', 'HARD', 'Hard: Drop the ''Student'' table entirely to finish the journey.', 'DROP TABLE Student', 'Use DROP TABLE Student', NULL, 'Journey complete!', 'DDL');
