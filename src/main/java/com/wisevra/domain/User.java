package com.wisevra.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;

@Data                    // Gera getters, setters, toString, equals, hashCode
@Builder                 // Gera builder pattern
@NoArgsConstructor      // Construtor vazio (necessário para Spring Data)
@AllArgsConstructor     // Construtor com todos os campos (para builder)
@Table("users")
public class User implements Persistable<String> {
    
    @Id
    @Column("id") 
    private String id;

    @Transient
    @Builder.Default
    private boolean isNew = true;
    
    @Column("first_name")
    private String firstName;
    
    @Column("last_name")
    private String lastName;
    
    @Column("email")
    private String email;
    
    @Column("clerk_user_id")
    private String clerkUserId;
    
    @Column("image_url")
    private String imageUrl;
    
    @Column("asaas_id")
    private String asaasId;
    
    @Column("created_at")
    private OffsetDateTime createdAt;
    
    @Column("updated_at")
    private OffsetDateTime updatedAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public User markNotNew() {
        this.isNew = false;
        return this;
    }
}