-- Migration V5: Add more unique tags following Prisma pattern
-- Each tag name is globally unique, but each belongs to a specific course

-- Add more unique tags for the courses
INSERT INTO course_tags (id, name, course_id) VALUES 
('cuid_tag_009', 'Tailwind', 'cuid_course_001'),
('cuid_tag_010', 'Express', 'cuid_course_002'), 
('cuid_tag_011', 'MongoDB', 'cuid_course_002'),
('cuid_tag_012', 'Hooks', 'cuid_course_003'),
('cuid_tag_013', 'Testing', 'cuid_course_003'),
('cuid_tag_014', 'UI/UX', 'cuid_course_003');