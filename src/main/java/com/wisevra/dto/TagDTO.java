package com.wisevra.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class TagDTO {
    private String id;
    private String name;
}
