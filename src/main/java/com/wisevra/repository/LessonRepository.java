package com.wisevra.repository;

import com.wisevra.domain.Lesson;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface LessonRepository extends ReactiveCrudRepository<Lesson, String> {
    Flux<Lesson> findByModuleId(String moduleId);
}
