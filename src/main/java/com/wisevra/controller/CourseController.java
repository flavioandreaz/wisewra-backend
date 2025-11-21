package com.wisevra.controller;

import com.wisevra.response.CourseResponse;
import com.wisevra.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
}
