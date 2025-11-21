package com.wisevra.domain;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    private String id;
    private String title;
    private String description;
    private String videoId;
    private Integer durationInMs;
    private Integer lessonOrder;
}
