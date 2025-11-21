package com.wisevra.mapper;

import org.mapstruct.Mapper;
import com.wisevra.domain.Course;
import com.wisevra.response.CourseResponse;

@Mapper
public interface CourseMapper {
    CourseResponse toResponse(Course course);
    com.wisevra.domain.Course toEntity(com.wisevra.dto.CourseDTO dto);
}
