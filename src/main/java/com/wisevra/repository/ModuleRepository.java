package com.wisevra.repository;

import com.wisevra.domain.Module;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ModuleRepository extends ReactiveCrudRepository<Module, String> {
    Flux<Module> findByCourseId(String courseId);
}
