package com.wisevra.controller;

import com.wisevra.response.CourseResponse;
import com.wisevra.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public Flux<CourseResponse> listCourses(
        @org.springframework.web.bind.annotation.RequestParam(required = false) String status,
        @org.springframework.web.bind.annotation.RequestParam(required = false) java.util.List<String> tags,
        @org.springframework.web.bind.annotation.RequestParam(required = false) String query
    ) {
        return courseService.listCourses(status, tags, query); 
    }

    @GetMapping("/{slug}")
    public Mono<CourseResponse> getCourseBySlug(@PathVariable String slug) {
        return courseService.getCourseBySlug(slug).next();
    }

    @GetMapping("/id/{id}")
    public Mono<CourseResponse> getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id).next();
    }

}
