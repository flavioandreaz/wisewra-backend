package com.wisevra.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleResponse {    
    private String title;
    private String description;
    private String courseId;
    private Integer moduleOrder;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<LessonResponse> lessons;
}