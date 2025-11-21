package com.wisevra.repository;

import com.wisevra.domain.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Mono<User> findByClerkUserId(String clerkUserId);
    Mono<Void> deleteByClerkUserId(String clerkUserId);
    
    @Query("INSERT INTO users (id, first_name, last_name, email, clerk_user_id, image_url, asaas_id, created_at, updated_at) " +
           "VALUES (:#{#user.id}, :#{#user.firstName}, :#{#user.lastName}, :#{#user.email}, :#{#user.clerkUserId}, :#{#user.imageUrl}, :#{#user.asaasId}, :#{#user.createdAt}, :#{#user.updatedAt}) " +
           "RETURNING *")
    Mono<User> insertUser(User user);
}
