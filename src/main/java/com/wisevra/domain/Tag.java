package com.wisevra.domain;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tag")
public class Tag {
    @Id
    private String id;

    @NotBlank(message = "Tag não pode ser vazia")
    @Pattern(regexp = ".*[A-Za-z0-9].*", message = "Tag deve conter pelo menos uma letra ou número")
    @Size(max = 100, message = "Tag deve ter no máximo 100 caracteres")
    private String name;
}
