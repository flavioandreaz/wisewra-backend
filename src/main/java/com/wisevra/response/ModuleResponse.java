package com.wisevra.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleResponse {
    private String name;
    private List<LessonResponse> lessons;
}