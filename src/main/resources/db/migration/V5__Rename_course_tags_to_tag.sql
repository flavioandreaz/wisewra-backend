-- Migration V5: Renomear tabela course_tags para tag
ALTER TABLE course_tags RENAME TO tag;

-- Opcional: renomear índices se existirem
ALTER INDEX IF EXISTS idx_course_tags_name RENAME TO idx_tag_name;
