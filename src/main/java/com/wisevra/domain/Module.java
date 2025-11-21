package com.wisevra.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.annotation.Transient;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("module")
public class Module {
    @Id
    private String id;
    private String title;
    private String description;
    private String courseId;
    private Integer moduleOrder;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    @Transient
    private List<Lesson> lessons;
}
