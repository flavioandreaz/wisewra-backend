package com.wisevra.mapper;

import org.mapstruct.Mapper;
import com.wisevra.domain.Course;
import com.wisevra.response.CourseResponse;
import com.wisevra.dto.CourseDTO;

@Mapper
public interface CourseMapper {
    CourseResponse toResponse(Course course);
    Course toEntity(CourseDTO dto);
}
