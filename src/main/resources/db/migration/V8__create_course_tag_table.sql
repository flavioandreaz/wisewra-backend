-- V8__create_course_tag_table.sql
CREATE TABLE IF NOT EXISTS course_tag (
    course_id VARCHAR(30) NOT NULL REFERENCES course(id) ON DELETE CASCADE,
    tag_id VARCHAR(30) NOT NULL REFERENCES tag(id) ON DELETE CASCADE,
    PRIMARY KEY (course_id, tag_id)
);