package com.wisevra.response;

import com.wisevra.enums.CourseStatusEnum;
import com.wisevra.enums.CourseDifficultyEnum;
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
public class CourseResponse {
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
    private List<TagResponse> tags;
    private List<ModuleResponse> modules;
}