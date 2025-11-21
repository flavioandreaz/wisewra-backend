package com.wisevra.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private String title;
    private String description;
    private String videoId;
    private Integer durationInMs;
    private Integer lessonOrder;
}