package com.wisevra.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private DatabaseClient databaseClient;

    @Test
    void testDatabaseConnection() {
        Mono<Integer> result = databaseClient
            .sql("SELECT 1 as test")
            .map(row -> (Integer) row.get("test"))
            .one();

        StepVerifier.create(result)
            .expectNext(1)
            .verifyComplete();
            
        System.out.println("✅ Database connection test passed");
    }
    
    @Test
    void testDatabaseInfo() {
        Mono<String> versionQuery = databaseClient
            .sql("SELECT version() as db_version")
            .map(row -> (String) row.get("db_version"))
            .one();

        StepVerifier.create(versionQuery)
            .expectNextMatches(version -> {
                boolean isPostgres = version.contains("PostgreSQL");
                System.out.println("✅ Database Version: " + version.substring(0, Math.min(50, version.length())) + "...");
                return isPostgres;
            })
            .verifyComplete();
    }
    
    @Test
    void testDatabaseTimeQuery() {
        Mono<Object> timeQuery = databaseClient
            .sql("SELECT NOW() as current_time")
            .map(row -> row.get("current_time"))
            .one();

        StepVerifier.create(timeQuery)
            .expectNextMatches(time -> {
                System.out.println("✅ Database Current Time: " + time);
                return time != null;
            })
            .verifyComplete();
    }
    
    @Test
    void testConnectionPoolInfo() {
        Mono<Integer> connectionQuery = databaseClient
            .sql("SELECT count(*) as active_connections FROM pg_stat_activity WHERE state = 'active'")
            .map(row -> ((Number) row.get("active_connections")).intValue())
            .one();

        StepVerifier.create(connectionQuery)
            .expectNextMatches(count -> {
                System.out.println("✅ Active Database Connections: " + count);
                return count >= 0;
            })
            .verifyComplete();
    }
}