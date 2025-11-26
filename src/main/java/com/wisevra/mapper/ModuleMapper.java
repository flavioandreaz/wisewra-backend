package com.wisevra.mapper;


import org.mapstruct.Mapper;

import com.wisevra.domain.Module;
import com.wisevra.response.ModuleResponse;
import java.util.List;

@Mapper(uses = {LessonMapper.class})
public interface ModuleMapper {
    ModuleResponse toResponse(Module module);
    List<ModuleResponse> toResponseList(List<Module> modules);
}



