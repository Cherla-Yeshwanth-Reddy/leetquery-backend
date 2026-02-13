-- ========== CLUSTER 4: Data Seeding Migration ==========
-- Migration: V2__Insert_Sample_Data.sql
-- Description: Load sample data for development and testing
-- Date: February 13, 2026

-- ========== Insert Administrator User ==========
INSERT INTO users (username, email, password, enabled, account_not_locked, account_not_expired, credentials_not_expired, created_by, updated_by)
VALUES (
    'admin',
    'admin@leetquery.local',
    -- Password: Admin@123 (BCrypt encoded with strength 12)
    '$2a$12$MkEPJLgcQC0LvnlsHXmgeu5mCdqcLK0xh3ntkPZvUDfB5bfSPcVwq',
    true,
    true,
    true,
    true,
    'system',
    'system'
);

-- ========== Insert Sample User Accounts ==========
INSERT INTO users (username, email, password, enabled, account_not_locked, account_not_expired, credentials_not_expired, created_by, updated_by)
VALUES
    (
        'john_learner',
        'john@leetquery.local',
        -- Password: Learn@123
        '$2a$12$aYjY3bZ5UtS6M8vXpK9n.eL3vL3vL3vL3vL3vL3vL3vL3vL3vL3v',
        true,
        true,
        true,
        true,
        'system',
        'system'
    ),
    (
        'sarah_student',
        'sarah@leetquery.local',
        -- Password: Learn@123
        '$2a$12$bXjY3bZ5UtS6M8vXpK9n.eL3vL3vL3vL3vL3vL3vL3vL3vL3vL3w',
        true,
        true,
        true,
        true,
        'system',
        'system'
    ),
    (
        'michael_dev',
        'michael@leetquery.local',
        -- Password: Learn@123
        '$2a$12$cXjY3bZ5UtS6M8vXpK9n.eL3vL3vL3vL3vL3vL3vL3vL3vL3vL3x',
        true,
        true,
        true,
        true,
        'system',
        'system'
    );

-- ========== Insert Sample Problems for Stage 1: SELECT Basics ==========
INSERT INTO problems (stage_id, title, description, difficulty, sql_template, expected_output, hints, solution_explanation, display_order, created_by, updated_by)
VALUES
    (
        1,
        'Select All Users',
        'Write a SQL query to select all users from the database.',
        'EASY',
        'SELECT * FROM users;',
        'All user records with all columns',
        'Use SELECT * to get all columns, or specify column names individually',
        'SELECT * fetches all columns from the users table. You can also use SELECT username, email FROM users to select specific columns.',
        1,
        'system',
        'system'
    ),
    (
        1,
        'Filter Users by Status',
        'Select all active users (enabled = true).',
        'EASY',
        'SELECT username, email FROM users WHERE enabled = true;',
        'Only active users',
        'Use WHERE clause with enabled = true condition',
        'WHERE clause filters rows based on conditions. enabled = true returns only enabled user accounts.',
        2,
        'system',
        'system'
    ),
    (
        1,
        'Order Users Alphabetically',
        'Select all users ordered by username (A to Z).',
        'EASY',
        'SELECT username, email FROM users ORDER BY username ASC;',
        'Users sorted by username ascending',
        'Use ORDER BY username ASC for ascending order',
        'ORDER BY sorts results. ASC (ascending) is default, DESC sorts descending.',
        3,
        'system',
        'system'
    );

-- ========== Insert Sample Problems for Stage 2: Joins & Aggregation ==========
INSERT INTO problems (stage_id, title, description, difficulty, sql_template, expected_output, hints, solution_explanation, display_order, created_by, updated_by)
VALUES
    (
        2,
        'Count Problems by Stage',
        'Count how many problems are in each stage.',
        'MEDIUM',
        'SELECT s.stage_name, COUNT(p.id) as problem_count FROM stages s LEFT JOIN problems p ON s.id = p.stage_id GROUP BY s.stage_name;',
        'Stage name with count of problems',
        'Use COUNT() with GROUP BY, LEFT JOIN to include stages with no problems',
        'GROUP BY groups rows by stage_name. COUNT(p.id) counts non-null problem IDs. LEFT JOIN includes all stages.',
        1,
        'system',
        'system'
    ),
    (
        2,
        'User Challenge Stats',
        'Get total challenges and completion count per user.',
        'MEDIUM',
        'SELECT u.username, COUNT(c.id) as total_attempts, COUNT(CASE WHEN c.status = ''COMPLETED'' THEN 1 END) as completed FROM users u LEFT JOIN challenges c ON u.id = c.user_id GROUP BY u.id, u.username;',
        'Username with attempt and completion counts',
        'Use CASE WHEN for conditional counting',
        'CASE WHEN allows conditional aggregation. This counts total challenges and completed ones separately per user.',
        2,
        'system',
        'system'
    );

-- ========== Insert Sample Problems for Stage 3: Subqueries ==========
INSERT INTO problems (stage_id, title, description, difficulty, sql_template, expected_output, hints, solution_explanation, display_order, created_by, updated_by)
VALUES
    (
        3,
        'Problems Harder Than Average',
        'Find all problems with difficulty level greater than the average.',
        'HARD',
        'WITH difficulty_order AS (SELECT ''EASY'' as level, 1 as value UNION SELECT ''MEDIUM'', 2 UNION SELECT ''HARD'', 3) SELECT p.title, p.difficulty FROM problems p CROSS JOIN (SELECT AVG(do.value) as avg_difficulty FROM difficulty_order do JOIN problems p2 ON p2.difficulty = do.level) stats WHERE (SELECT value FROM difficulty_order WHERE level = p.difficulty) > stats.avg_difficulty;',
        'Problem titles with difficulty above average',
        'Use subqueries or CTEs with aggregation',
        'This uses a CTE (Common Table Expression) with CROSS JOIN to compare each problem against the average difficulty.',
        1,
        'system',
        'system'
    );

-- ========== Insert Sample User Progress Records ==========
INSERT INTO user_progress (user_id, total_problems_completed, total_challenges_completed, current_stage_id, created_by, updated_by)
SELECT 
    u.id,
    0,
    0,
    1,
    'system',
    'system'
FROM users u WHERE u.username IN ('john_learner', 'sarah_student', 'michael_dev');

-- ========== Verify Data Insertion ==========
-- SELECT COUNT(*) as user_count FROM users;
-- SELECT COUNT(*) as stage_count FROM stages;
-- SELECT COUNT(*) as problem_count FROM problems;
-- SELECT COUNT(*) as progress_count FROM user_progress;
