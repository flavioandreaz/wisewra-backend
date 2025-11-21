package com.wisevra.service;

import com.wisevra.repository.TagRepository;
import com.wisevra.response.TagResponse;
import com.wisevra.domain.Tag;
import com.wisevra.dto.TagDTO;
import com.wisevra.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public Flux<TagResponse> getAllTags() {
            return tagRepository.findAll()
            .map(tagMapper::toResponse);
    }

    public Tag toEntity(TagDTO dto) {
        return tagMapper.toEntity(dto);
    }
}
