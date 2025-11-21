package com.wisevra.repository;

import com.wisevra.domain.Tag;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;

public interface TagRepository extends ReactiveCrudRepository<Tag, String> {
	@Query("SELECT t.* FROM tag t JOIN course_tag ct ON t.id = ct.tag_id WHERE ct.course_id = :courseId")
	Flux<Tag> findTagsByCourseId(String courseId);
}
