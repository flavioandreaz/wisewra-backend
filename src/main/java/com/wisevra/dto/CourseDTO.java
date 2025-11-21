package com.wisevra.dto;

import com.wisevra.enums.CourseStatusEnum;
import com.wisevra.enums.CourseDifficultyEnum;
import com.wisevra.domain.Tag;
import com.wisevra.domain.Module;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
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
    private List<Tag> tags;
    private List<Module> modules;
}