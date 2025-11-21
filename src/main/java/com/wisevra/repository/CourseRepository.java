package com.wisevra.repository;

import com.wisevra.domain.Course;
import com.wisevra.enums.CourseStatusEnum;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CourseRepository extends ReactiveCrudRepository<Course, String> {
    Flux<Course> findByStatus(CourseStatusEnum status);
    Flux<Course> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
    // Outros métodos derivados podem ser adicionados conforme necessidade
}
