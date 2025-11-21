package com.wisevra.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ActuatorEndpointsTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testHealthEndpoint() {
        webTestClient
            .get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP")
            .consumeWith(result -> 
                System.out.println("✅ Health Check: " + new String(result.getResponseBody()))
            );
    }
    
    @Test
    void testInfoEndpoint() {
        webTestClient
            .get()
            .uri("/actuator/info")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(result -> 
                System.out.println("✅ App Info: " + new String(result.getResponseBody()))
            );
    }
    
    @Test
    void testMetricsEndpoint() {
        webTestClient
            .get()
            .uri("/actuator/metrics")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.names").exists()
            .consumeWith(result -> 
                System.out.println("✅ Metrics Available")
            );
    }
}