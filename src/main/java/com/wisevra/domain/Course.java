package com.wisevra.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Transient;
import com.wisevra.enums.CourseDifficultyEnum;
import com.wisevra.enums.CourseStatusEnum;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("course")
public class Course {
    @Id
    private String id;
    private CourseStatusEnum status;
    private String title;
    private String slug;
    private String description;
    private String shortDescription;
    private String thumbnail;
    private Double price;
    private Double discountPrice;
    private CourseDifficultyEnum difficulty;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Relacionamentos agregados (preenchidos manualmente)
    @Transient
    private List<Tag> tags;
    @Transient
    private List<Module> modules;
}
