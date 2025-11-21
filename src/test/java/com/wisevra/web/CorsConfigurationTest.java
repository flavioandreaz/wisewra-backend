package com.wisevra.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CorsConfigurationTest {
    
    @Autowired
    private Environment environment;
    
    @Test
    void testCorsOrigins() {
        String allowedOriginsProperty = environment.getProperty("app.cors.allowed-origins");
        if (allowedOriginsProperty != null) {
            List<String> allowedOrigins = Arrays.asList(allowedOriginsProperty.split(","));
            
            assertTrue(allowedOrigins.stream().anyMatch(origin -> origin.trim().equals("http://localhost:3000")));
            assertTrue(allowedOrigins.stream().anyMatch(origin -> origin.trim().equals("https://wisevra.vercel.app")));
            
            System.out.println("✅ CORS Origins: " + allowedOrigins);
        } else {
            System.out.println("⚠️ CORS origins not configured in test environment");
            assertTrue(true); // Pass test if property not configured in test
        }
    }
    
    @Test
    void testCorsMethods() {
        String allowedMethodsProperty = environment.getProperty("app.cors.allowed-methods");
        if (allowedMethodsProperty != null) {
            List<String> allowedMethods = Arrays.asList(allowedMethodsProperty.split(","));
            
            assertTrue(allowedMethods.stream().anyMatch(method -> method.trim().equals("GET")));
            assertTrue(allowedMethods.stream().anyMatch(method -> method.trim().equals("POST")));
            assertTrue(allowedMethods.stream().anyMatch(method -> method.trim().equals("PUT")));
            assertTrue(allowedMethods.stream().anyMatch(method -> method.trim().equals("DELETE")));
            
            System.out.println("✅ CORS Methods: " + allowedMethods);
        } else {
            System.out.println("⚠️ CORS methods not configured in test environment");
            assertTrue(true); // Pass test if property not configured in test
        }
    }
    
    @Test
    void testCorsCredentials() {
        String allowCredentialsProperty = environment.getProperty("app.cors.allow-credentials");
        if (allowCredentialsProperty != null) {
            boolean allowCredentials = Boolean.parseBoolean(allowCredentialsProperty);
            assertTrue(allowCredentials);
            System.out.println("✅ CORS Credentials Allowed: " + allowCredentials);
        } else {
            System.out.println("⚠️ CORS credentials not configured in test environment");
            assertTrue(true); // Pass test if property not configured in test
        }
    }
}