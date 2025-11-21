-- Migration V3: Create complete course platform schema
-- Based on Prisma schema for Wisevra Learning Platform

-- Drop existing person table (will be replaced by users)
DROP TABLE IF EXISTS person CASCADE;

-- Create ENUM types
DROP TYPE IF EXISTS course_status CASCADE;
CREATE TYPE course_status AS ENUM ('DRAFT', 'PUBLISHED');
DROP TYPE IF EXISTS course_difficulty CASCADE;
CREATE TYPE course_difficulty AS ENUM ('EASY', 'MEDIUM', 'HARD');

-- Users table (replaces person)
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(30) PRIMARY KEY, -- cuid
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    email VARCHAR(255) UNIQUE NOT NULL,
    clerk_user_id VARCHAR(255) UNIQUE NOT NULL,
    image_url TEXT,
    asaas_id VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Courses table
CREATE TABLE IF NOT EXISTS courses (
    id VARCHAR(30) PRIMARY KEY, -- cuid
    status course_status DEFAULT 'DRAFT',
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    description TEXT NOT NULL,
    short_description TEXT,
    thumbnail TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    discount_price DECIMAL(10,2),
    difficulty course_difficulty DEFAULT 'EASY',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Course Tags table
CREATE TABLE IF NOT EXISTS course_tags (
    id VARCHAR(30) PRIMARY KEY, -- cuid
    name VARCHAR(100) UNIQUE NOT NULL,
    course_id VARCHAR(30) REFERENCES courses(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Course Modules table
CREATE TABLE IF NOT EXISTS course_modules (
    id VARCHAR(30) PRIMARY KEY, -- cuid
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    course_id VARCHAR(30) NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    module_order INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Course Lessons table
CREATE TABLE IF NOT EXISTS course_lessons (
    id VARCHAR(30) PRIMARY KEY, -- cuid
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    video_id VARCHAR(255) NOT NULL,
    duration_in_ms INTEGER NOT NULL,
    lesson_order INTEGER NOT NULL,
    module_id VARCHAR(30) NOT NULL REFERENCES course_modules(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Completed Lessons table (user progress)
CREATE TABLE IF NOT EXISTS completed_lessons (
    id VARCHAR(30) PRIMARY KEY, -- cuid
    lesson_id VARCHAR(30) NOT NULL REFERENCES course_lessons(id) ON DELETE CASCADE,
    course_id VARCHAR(30) NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    user_id VARCHAR(30) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(lesson_id, user_id)
);

-- Lesson Comments table (with nested replies)
CREATE TABLE IF NOT EXISTS lesson_comments (
    id VARCHAR(30) PRIMARY KEY, -- cuid
    lesson_id VARCHAR(30) NOT NULL REFERENCES course_lessons(id) ON DELETE CASCADE,
    user_id VARCHAR(30) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    parent_id VARCHAR(30) REFERENCES lesson_comments(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Course Purchases table
CREATE TABLE IF NOT EXISTS course_purchases (
    id VARCHAR(30) PRIMARY KEY, -- cuid
    course_id VARCHAR(30) NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    user_id VARCHAR(30) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(course_id, user_id)
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id VARCHAR(30) PRIMARY KEY, -- cuid
    user_id VARCHAR(30) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    link TEXT,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_clerk_id ON users(clerk_user_id);
CREATE INDEX IF NOT EXISTS idx_courses_slug ON courses(slug);
CREATE INDEX IF NOT EXISTS idx_courses_status ON courses(status);
CREATE INDEX IF NOT EXISTS idx_courses_difficulty ON courses(difficulty);
CREATE INDEX IF NOT EXISTS idx_course_tags_name ON course_tags(name);
CREATE INDEX IF NOT EXISTS idx_course_modules_course_id ON course_modules(course_id);
CREATE INDEX IF NOT EXISTS idx_course_modules_order ON course_modules(course_id, module_order);
CREATE INDEX IF NOT EXISTS idx_course_lessons_module_id ON course_lessons(module_id);
CREATE INDEX IF NOT EXISTS idx_course_lessons_order ON course_lessons(module_id, lesson_order);
CREATE INDEX IF NOT EXISTS idx_completed_lessons_user_id ON completed_lessons(user_id);
CREATE INDEX IF NOT EXISTS idx_completed_lessons_course_id ON completed_lessons(course_id);
CREATE INDEX IF NOT EXISTS idx_lesson_comments_lesson_id ON lesson_comments(lesson_id);
CREATE INDEX IF NOT EXISTS idx_lesson_comments_user_id ON lesson_comments(user_id);
CREATE INDEX IF NOT EXISTS idx_lesson_comments_parent_id ON lesson_comments(parent_id);
CREATE INDEX IF NOT EXISTS idx_course_purchases_user_id ON course_purchases(user_id);
CREATE INDEX IF NOT EXISTS idx_course_purchases_course_id ON course_purchases(course_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_read_at ON notifications(user_id, read_at);