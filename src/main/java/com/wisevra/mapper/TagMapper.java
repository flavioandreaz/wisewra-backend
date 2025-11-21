package com.wisevra.mapper;

import com.wisevra.domain.Tag;
import com.wisevra.dto.TagDTO;
import com.wisevra.response.TagResponse;
import org.mapstruct.Mapper;

@Mapper
public interface TagMapper {
    TagResponse toResponse(Tag tag);
    Tag toEntity(TagDTO dto);
}
