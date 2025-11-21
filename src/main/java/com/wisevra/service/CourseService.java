package com.wisevra.service;

import com.wisevra.repository.CourseRepository;
import com.wisevra.repository.ModuleRepository;
import com.wisevra.repository.TagRepository;
import com.wisevra.response.CourseResponse;
import com.wisevra.domain.Course;
import com.wisevra.dto.CourseDTO;
import com.wisevra.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final TagRepository tagRepository;
    private final CourseMapper courseMapper;


    public Flux<CourseResponse> enrichCoursesWithCollections(Flux<Course> courses) {
        return courses.flatMap(course ->
            Mono.zip(
                moduleRepository.findByCourseId(course.getId()).collectList(),
                tagRepository.findTagsByCourseId(course.getId()).collectList(),
                (modules, tags) -> {
                    course.setModules(modules);
                    course.setTags(tags);
                    return courseMapper.toResponse(course);
                }
            )
        );
    }


    public Flux<CourseResponse> getAllCourses() {
        return enrichCoursesWithCollections(courseRepository.findAll());
    }

    public Flux<CourseResponse> listCourses(String status, List<String> tags, String query) {
        Flux<Course> courses = courseRepository.findAll();
        return enrichCoursesWithCollections(courses)
            .filter(courseResponse -> {
                boolean matches = true;
                if (status != null && courseResponse.getStatus() != null) {
                    matches = matches && status.equalsIgnoreCase(courseResponse.getStatus().toString());
                }
                if (tags != null && !tags.isEmpty()) {
                    matches = matches && courseResponse.getTags() != null &&
                        courseResponse.getTags().stream().anyMatch(tag -> tags.contains(tag.getName()));
                }
                if (query != null && !query.isBlank()) {
                    matches = matches && courseResponse.getTitle() != null &&
                        courseResponse.getTitle().toLowerCase().contains(query.toLowerCase());
                }
                return matches;
            });
    }
   
    public Course toEntity(CourseDTO dto) {
        return courseMapper.toEntity(dto);
    }
}
