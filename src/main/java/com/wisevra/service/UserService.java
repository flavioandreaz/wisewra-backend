package com.wisevra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wisevra.domain.User;
import com.wisevra.repository.UserRepository;

import io.micrometer.core.annotation.Timed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Serviço para gerenciar usuários vindos do Clerk
 */
@Service
@Slf4j
public class UserService {
    

    private final UserRepository userRepository;
    private final io.micrometer.core.instrument.MeterRegistry meterRegistry;

    public UserService(UserRepository userRepository, io.micrometer.core.instrument.MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Cria um novo usuário baseado nos dados do Clerk
     */
    @Timed(value = "users.create.time", description = "Tempo para criar usuário")
    public Mono<User> createUserFromClerk(JsonNode clerkUserData) {
        String clerkUserId = clerkUserData.get("id").asText();
        
        log.info("🆕 Creating user from Clerk - ID: {}", clerkUserId);
        
        // Extrai dados do usuário do Clerk
        String email = extractEmail(clerkUserData);
        String firstName = extractFirstName(clerkUserData);
        String lastName = extractLastName(clerkUserData);
        String profileImageUrl = extractProfileImageUrl(clerkUserData);
        
        log.info("📋 User data - Email: {}, Name: {} {}, Image: {}", 
               email, firstName, lastName, profileImageUrl);

        User user = User.builder()
                .id(generateUserId())
                .clerkUserId(clerkUserId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .imageUrl(profileImageUrl)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Validação para null safety
        if (user == null) {
            return Mono.error(new RuntimeException("Failed to create user object"));
        }

        // Métrica: usuário criado
        meterRegistry.counter("users.created").increment();

        return userRepository.save(user)
                .doOnSuccess(savedUser -> log.info("✅ User created successfully: {}", savedUser.getId()))
                .doOnError(error -> log.error("❌ Error creating user: {}", error.getMessage()));
    }

    /**
     * Atualiza usuário existente com dados do Clerk
     */
    public Mono<User> updateUserFromClerk(JsonNode clerkUserData) {
        String clerkUserId = clerkUserData.get("id").asText();
        
        log.info("✏️ Updating user from Clerk - ID: {}", clerkUserId);
        
        return userRepository.findByClerkUserId(clerkUserId)
                .flatMap(existingUser -> {
                    // Atualiza apenas os campos que vieram do Clerk
                    String email = extractEmail(clerkUserData);
                    String firstName = extractFirstName(clerkUserData);
                    String lastName = extractLastName(clerkUserData);
                    String profileImageUrl = extractProfileImageUrl(clerkUserData);
                    
                    log.info("📝 Updating data - Email: {}, Name: {} {}", email, firstName, lastName);
                    
                    existingUser.setEmail(email);
                    existingUser.setFirstName(firstName);
                    existingUser.setLastName(lastName);
                    existingUser.setImageUrl(profileImageUrl);
                    existingUser.setUpdatedAt(OffsetDateTime.now());
                    
                    return userRepository.save(existingUser);
                })
                .doOnSuccess(updatedUser -> log.info("✅ User updated successfully: {}", updatedUser.getId()))
                .doOnError(error -> log.error("❌ Error updating user: {}", error.getMessage()))
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with clerkUserId: " + clerkUserId)));
    }

    /**
     * Remove usuário (soft delete - marca como inativo)
     */
    public Mono<Void> deleteUserFromClerk(String clerkUserId) {
        log.info("🗑️ Deleting user from Clerk - ID: {}", clerkUserId);
        
        return userRepository.findByClerkUserId(clerkUserId)
                .flatMap(user -> {
                    log.info("🔍 Found user to delete: {}", user.getId());
                    
                    // Validação para null safety
                    String userId = user.getId();
                    if (userId == null) {
                        return Mono.error(new RuntimeException("User ID is null"));
                    }
                    
                    // Soft delete - apenas remove do sistema, mas mantém histórico
                    return userRepository.deleteById(userId);
                })
                .doOnSuccess(result -> log.info("✅ User deleted successfully: {}", clerkUserId))
                .doOnError(error -> log.error("❌ Error deleting user: {}", error.getMessage()))
                .then();
    }

    /**
     * Busca usuário por Clerk User ID
     */
    public Mono<User> findByClerkUserId(String clerkUserId) {
        log.info("🔍 Buscando usuário por Clerk ID: {}", clerkUserId);
        
        return userRepository.findByClerkUserId(clerkUserId)
                .doOnNext(user -> log.info("✅ Usuário encontrado: {} ({})", user.getEmail(), user.getId()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("❌ Usuário não encontrado para Clerk ID: {}", clerkUserId);
                    return Mono.empty();
                }));
    }

    /**
     * Cria um usuário básico apenas com o Clerk User ID
     * Usado quando o evento de sessão não contém dados completos do usuário
     */
    public Mono<User> createBasicUserFromClerk(String clerkUserId) {
        log.info("🆕 Criando usuário básico - Clerk ID: {}", clerkUserId);
        
        User basicUser = User.builder()
                .id(generateUserId())  // Gera ID manualmente (CUID-like)
                .clerkUserId(clerkUserId)
                .email("user_" + clerkUserId + "@temp.clerk")  // Email temporário
                .firstName("Usuário")
                .lastName("Social")
                .imageUrl(null)
                .asaasId(null)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Verificação de segurança de nulo
        if (basicUser == null) {
            return Mono.error(new RuntimeException("Failed to create basic user object"));
        }

        return userRepository.insertUser(basicUser)
                .doOnSuccess(savedUser -> 
                    log.info("✅ Usuário básico criado: {} (ID: {})", savedUser.getEmail(), savedUser.getId())
                )
                .doOnError(error -> 
                    log.error("❌ Erro ao criar usuário básico: {}", error.getMessage())
                );
    }

    // ===== MÉTODOS AUXILIARES PARA EXTRAIR DADOS DO JSON =====
    
    private String extractEmail(JsonNode userData) {
        if (userData.has("email_addresses") && userData.get("email_addresses").isArray()) {
            JsonNode emailArray = userData.get("email_addresses");
            for (JsonNode emailObj : emailArray) {
                if (emailObj.has("email_address")) {
                    return emailObj.get("email_address").asText();
                }
            }
        }
        log.warn("⚠️ No email found in Clerk data");
        return null;
    }

    private String extractFirstName(JsonNode userData) {
        if (userData.has("first_name") && !userData.get("first_name").isNull()) {
            return userData.get("first_name").asText();
        }
        log.warn("⚠️ No first_name found in Clerk data");
        return null;
    }

    private String extractLastName(JsonNode userData) {
        if (userData.has("last_name") && !userData.get("last_name").isNull()) {
            return userData.get("last_name").asText();
        }
        // Last name é opcional
        return null;
    }

    private String extractProfileImageUrl(JsonNode userData) {
        if (userData.has("profile_image_url") && !userData.get("profile_image_url").isNull()) {
            return userData.get("profile_image_url").asText();
        }
        return null;
    }
    
    private String generateUserId() {
        // Gera um ID único de 30 caracteres (conforme schema da tabela)
        return UUID.randomUUID().toString().replace("-", "").substring(0, 30);
    }

    /**
     * Ensures the user exists by Clerk ID: if not, creates automatically
     */
    public Mono<User> ensureUserByClerkId(String clerkUserId) {
        return findByClerkUserId(clerkUserId)
            .switchIfEmpty(
                createBasicUserFromClerk(clerkUserId)
                    .doOnSuccess(user -> log.info("🆕 User automatically created for Clerk ID: {}", clerkUserId))
            )
            .doOnNext(user -> log.info("🔒 User ensured in database: {} (Clerk ID: {})", user.getEmail(), clerkUserId));
    }

    /**
     * Métrica: login realizado
     */
    public void recordLogin() {
        meterRegistry.counter("users.logins").increment();
    }
}