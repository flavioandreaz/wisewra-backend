package com.wisevra.domain;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("lesson")
public class Lesson {
    private String id;
    private String title;
    private String description;
    private String videoId;
    private Integer durationInMs;
    private Integer lessonOrder;
    private String moduleId;
    private java.time.OffsetDateTime createdAt;
    private java.time.OffsetDateTime updatedAt;
}
