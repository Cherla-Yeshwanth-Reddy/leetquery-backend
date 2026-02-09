-- ============================================
-- LeetQuery Tutorial Database Schema
-- ============================================
-- This schema creates a university database for SQL learning
-- Tables: departments, instructors, students, courses, enrollments
-- ============================================

-- Drop tables if they exist (for clean restart)
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS problems;
DROP TABLE IF EXISTS stages;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS instructors;
DROP TABLE IF EXISTS departments;

-- ============================================
-- Table: departments
-- ============================================
CREATE TABLE IF NOT EXISTS departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    building VARCHAR(100),
    budget DECIMAL(12, 2)
);

-- ============================================
-- Table: instructors
-- ============================================
CREATE TABLE IF NOT EXISTS instructors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department_id INT,
    salary DECIMAL(10, 2),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- ============================================
-- Table: students
-- ============================================
CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department_id INT,
    gpa DECIMAL(3, 2),
    enrollment_year INT,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- ============================================
-- Table: courses
-- ============================================
CREATE TABLE IF NOT EXISTS courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department_id INT,
    credits INT,
    instructor_id INT,
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (instructor_id) REFERENCES instructors(id)
);

-- ============================================
-- Table: enrollments
-- ============================================
CREATE TABLE IF NOT EXISTS enrollments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    semester VARCHAR(20),
    year INT,
    grade VARCHAR(2),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- ============================================
-- Sample Data: departments
-- ============================================
INSERT INTO departments (name, building, budget) VALUES
('Computer Science', 'Taylor Hall', 500000.00),
('Mathematics', 'Watson Hall', 350000.00),
('Physics', 'Science Center', 450000.00),
('English', 'Humanities Building', 250000.00),
('Business', 'Commerce Hall', 600000.00);

-- ============================================
-- Sample Data: instructors
-- ============================================
INSERT INTO instructors (name, department_id, salary) VALUES
('Dr. Alan Turing', 1, 95000.00),
('Dr. Ada Lovelace', 1, 92000.00),
('Dr. Isaac Newton', 3, 98000.00),
('Dr. Emmy Noether', 2, 89000.00),
('Dr. Marie Curie', 3, 96000.00),
('Dr. Jane Austen', 4, 78000.00),
('Dr. Warren Buffett', 5, 105000.00);

-- ============================================
-- Sample Data: students
-- ============================================
INSERT INTO students (name, department_id, gpa, enrollment_year) VALUES
('Alice Johnson', 1, 3.85, 2022),
('Bob Smith', 1, 3.42, 2021),
('Charlie Davis', 2, 3.91, 2023),
('Diana Prince', 3, 3.67, 2022),
('Eve Martinez', 1, 3.55, 2023),
('Frank Zhang', 2, 3.78, 2021),
('Grace Hopper', 1, 3.95, 2022),
('Henry Ford', 5, 3.33, 2023),
('Iris West', 4, 3.72, 2022),
('Jack Ryan', 3, 3.48, 2021),
('Kate Bishop', 1, 3.88, 2023),
('Leo Valdez', 2, 3.61, 2022),
('Maya Lopez', 5, 3.44, 2021),
('Noah Centineo', 4, 3.56, 2023),
('Olivia Rodrigo', 3, 3.82, 2022);

-- ============================================
-- Sample Data: courses
-- ============================================
INSERT INTO courses (name, department_id, credits, instructor_id) VALUES
('Introduction to Programming', 1, 4, 1),
('Data Structures', 1, 4, 2),
('Database Systems', 1, 3, 1),
('Calculus I', 2, 4, 4),
('Linear Algebra', 2, 3, 4),
('Physics I', 3, 4, 3),
('Quantum Mechanics', 3, 4, 5),
('English Literature', 4, 3, 6),
('Business Management', 5, 3, 7),
('Algorithms', 1, 4, 2);

-- ============================================
-- Sample Data: enrollments
-- ============================================
INSERT INTO enrollments (student_id, course_id, semester, year, grade) VALUES
(1, 1, 'Fall', 2022, 'A'),
(1, 2, 'Spring', 2023, 'A-'),
(2, 1, 'Fall', 2021, 'B+'),
(2, 3, 'Spring', 2022, 'B'),
(3, 4, 'Fall', 2023, 'A'),
(3, 5, 'Spring', 2024, 'A-'),
(4, 6, 'Fall', 2022, 'B+'),
(5, 1, 'Fall', 2023, 'A-'),
(5, 2, 'Spring', 2024, 'B+'),
(6, 4, 'Fall', 2021, 'A'),
(7, 1, 'Fall', 2022, 'A'),
(7, 10, 'Spring', 2023, 'A'),
(8, 9, 'Fall', 2023, 'B'),
(9, 8, 'Fall', 2022, 'A-'),
(10, 6, 'Fall', 2021, 'B+'),
(11, 2, 'Fall', 2023, 'A'),
(12, 5, 'Fall', 2022, 'A-'),
(13, 9, 'Fall', 2021, 'B'),
(14, 8, 'Fall', 2023, 'B+'),
(15, 7, 'Fall', 2022, 'A-');

-- ============================================
-- Table: stages (SQL Learning Stages)
-- ============================================
CREATE TABLE IF NOT EXISTS stages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    order_no INT NOT NULL
);

-- ============================================
-- Table: problems (SQL Learning Problems)
-- ============================================
CREATE TABLE IF NOT EXISTS problems (
    id INT PRIMARY KEY AUTO_INCREMENT,
    stage_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    expected_query TEXT NOT NULL,
    FOREIGN KEY (stage_id) REFERENCES stages(id)
);

-- ============================================
-- Sample Data: stages
-- ============================================
INSERT INTO stages (title, description, order_no) VALUES
('SELECT Basics', 'Learn how to retrieve data using SELECT', 1),
('WHERE Filtering', 'Filter rows using WHERE clause', 2),
('JOIN Operations', 'Combine tables using JOIN', 3);

-- ============================================
-- Sample Data: problems
-- ============================================
INSERT INTO problems (stage_id, title, description, expected_query) VALUES
(1, 'List all students', 'Write a query to display all students from the students table.', 'SELECT * FROM students;'),
(2, 'Students with GPA > 3.5', 'Display students whose GPA is greater than 3.5', 'SELECT * FROM students WHERE gpa > 3.5;'),
(3, 'Courses with instructors', 'Show course name and instructor name', 'SELECT c.name, i.name FROM courses c JOIN instructors i ON c.instructor_id = i.id;');

-- ============================================
-- Table: user_roles (Admin Authentication)
-- ============================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(255) PRIMARY KEY,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Table: challenges (Tutorial Mode Challenges)
-- ============================================
CREATE TABLE IF NOT EXISTS challenges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    level_id INT NOT NULL DEFAULT 0,
    stage_number INT NOT NULL,
    stage_title VARCHAR(255) NOT NULL,
    difficulty VARCHAR(20) NOT NULL CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    description TEXT NOT NULL,
    expected_query TEXT NOT NULL,
    hint TEXT,
    relational_algebra_hint TEXT,
    success_message TEXT NOT NULL,
    challenge_type VARCHAR(10) DEFAULT 'DQL',
    UNIQUE(level_id, stage_number, difficulty)
);

-- ============================================
-- Table: tutorial_schema (Schema Information)
-- ============================================
CREATE TABLE IF NOT EXISTS tutorial_schema (
    id INT PRIMARY KEY AUTO_INCREMENT,
    level_id INT NOT NULL DEFAULT 0,
    schema_info TEXT NOT NULL
);

-- ============================================
-- Sample Data: user_roles
-- ============================================
-- Add your Supabase user ID here with ADMIN role
-- Example: INSERT INTO user_roles (user_id, role) VALUES ('your-supabase-user-id', 'ADMIN');
