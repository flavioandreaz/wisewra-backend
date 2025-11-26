package com.wisevra.mapper;

import org.mapstruct.Mapper;
import com.wisevra.domain.Lesson;
import com.wisevra.response.LessonResponse;
import java.util.List;

@Mapper
public interface LessonMapper {
    LessonResponse toResponse(Lesson lesson);
    List<LessonResponse> toResponseList(List<Lesson> lessons);
}
