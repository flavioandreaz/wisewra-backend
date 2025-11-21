package com.wisevra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Clerk authentication service integration
 * Documenta as propriedades customizadas 'clerk.*' no application.yaml
 */
@Configuration
@ConfigurationProperties(prefix = "clerk")
public class ClerkProperties {

    private Webhook webhook = new Webhook();

    public Webhook getWebhook() {
        return webhook;
    }

    public void setWebhook(Webhook webhook) {
        this.webhook = webhook;
    }

    /**
     * Webhook configuration for Clerk user events
     */
    public static class Webhook {
        
        /**
         * Secret key for validating Clerk webhook signatures.
         * This should match the webhook secret configured in your Clerk Dashboard.
         * Format: whsec_abc123def456...
         */
        private String secret = "";

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}