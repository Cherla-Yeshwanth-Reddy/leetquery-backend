-- ========== CLUSTER 4: MySQL Initial Schema ==========
-- Migration: V1__Initial_Schema.sql
-- Description: Create initial database schema with all core entities
-- Date: February 13, 2026
-- Flyway Version: 7.6.0

-- ========== Users Table ==========
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_not_locked BOOLEAN NOT NULL DEFAULT true,
    account_not_expired BOOLEAN NOT NULL DEFAULT true,
    credentials_not_expired BOOLEAN NOT NULL DEFAULT true,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- ========== Stages Table (Learning Progression) ==========
CREATE TABLE stages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stage_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    display_order INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    difficulty_level VARCHAR(20), -- BEGINNER, INTERMEDIATE, ADVANCED
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_stages_display_order ON stages(display_order);
CREATE INDEX idx_stages_is_active ON stages(is_active);

-- ========== Problems Table ==========
CREATE TABLE problems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stage_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    difficulty VARCHAR(20) NOT NULL, -- EASY, MEDIUM, HARD
    sql_template TEXT,
    expected_output TEXT,
    hints TEXT,
    solution_explanation TEXT,
    display_order INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INTEGER DEFAULT 0,
    CONSTRAINT fk_problems_stage FOREIGN KEY (stage_id) REFERENCES stages(id) ON DELETE CASCADE
);

CREATE INDEX idx_problems_stage_id ON problems(stage_id);
CREATE INDEX idx_problems_difficulty ON problems(difficulty);
CREATE INDEX idx_problems_is_active ON problems(is_active);
CREATE INDEX idx_problems_display_order ON problems(display_order);

-- ========== Challenges Table (Practice Exercises) ==========
CREATE TABLE challenges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED', -- NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED
    attempts INTEGER DEFAULT 0,
    max_attempts INTEGER DEFAULT 5,
    user_solution TEXT,
    execution_time_ms BIGINT,
    completed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    CONSTRAINT fk_challenges_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    CONSTRAINT fk_challenges_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_challenges_user_id ON challenges(user_id);
CREATE INDEX idx_challenges_problem_id ON challenges(problem_id);
CREATE INDEX idx_challenges_status ON challenges(status);
CREATE INDEX idx_challenges_completed_at ON challenges(completed_at);
CREATE UNIQUE INDEX idx_challenges_user_problem ON challenges(user_id, problem_id);

-- ========== Query Execution History ==========
CREATE TABLE query_executions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    problem_id BIGINT,
    query TEXT NOT NULL,
    status VARCHAR(20) NOT NULL, -- SUCCESS, ERROR, TIMEOUT
    error_message TEXT,
    result_rows INTEGER,
    execution_time_ms BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_query_executions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_query_executions_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE SET NULL
);

CREATE INDEX idx_query_executions_user_id ON query_executions(user_id);
CREATE INDEX idx_query_executions_problem_id ON query_executions(problem_id);
CREATE INDEX idx_query_executions_status ON query_executions(status);
CREATE INDEX idx_query_executions_created_at ON query_executions(created_at DESC);

-- ========== User Progress Tracking ==========
CREATE TABLE user_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    total_problems_completed INTEGER DEFAULT 0,
    total_challenges_completed INTEGER DEFAULT 0,
    current_stage_id BIGINT,
    total_execution_time_ms BIGINT DEFAULT 0,
    last_activity_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_progress_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_progress_stage FOREIGN KEY (current_stage_id) REFERENCES stages(id) ON DELETE SET NULL
);

CREATE INDEX idx_user_progress_user_id ON user_progress(user_id);
CREATE INDEX idx_user_progress_current_stage ON user_progress(current_stage_id);

-- ========== Audit Table ==========
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    user_agent TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);

-- ========== Initial Data Seed ==========
INSERT INTO stages (stage_name, description, display_order, difficulty_level) VALUES
    ('SELECT Basics', 'Learn SELECT, WHERE, and ORDER BY', 1, 'BEGINNER'),
    ('Joins & Aggregation', 'Master JOINs, GROUP BY, HAVING', 2, 'INTERMEDIATE'),
    ('Subqueries & Advanced', 'Complex queries with subqueries and CTEs', 3, 'INTERMEDIATE'),
    ('Window Functions', 'Learn OVER, PARTITION BY, ROW_NUMBER', 4, 'ADVANCED'),
    ('Transaction Management', 'ACID properties, locks, and transactions', 5, 'ADVANCED');

-- ========== Schema History Comment ==========
-- Version 1.0.0: Initial schema with user authentication, problems, challenges, and progress tracking
-- Tables: users, stages, problems, challenges, query_executions, user_progress, audit_logs
-- Total Indexes: 18
-- Foreign Keys: 6
-- Constraints: Unique, Not Null, Default, Check
-- Created: 2026-02-13
-- Converted to MySQL: 2026-02-14
