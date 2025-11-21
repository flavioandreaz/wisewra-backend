package com.wisevra.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisevra.config.ClerkProperties;
import com.wisevra.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

/**
 * Clerk Webhook Controller focado em eventos de sessão
 * Para tracking de login/logout com autenticação social
 */
@RestController
@RequestMapping("/api/webhooks/clerk")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "https://wisevra.vercel.app"})
@Slf4j
@RequiredArgsConstructor
public class ClerkWebhookController {

    private static final String ALGORITHM = "HmacSHA256";
    
    private final ClerkProperties clerkProperties;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<Object>> handleClerkWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "svix-id", required = false) String svixId,
            @RequestHeader(value = "svix-timestamp", required = false) String svixTimestamp,
            @RequestHeader(value = "svix-signature", required = false) String svixSignature) {

        log.info("📨 Webhook recebido do Clerk - ID: {}", svixId);
        
        String webhookSecret = clerkProperties.getWebhook().getSecret();
        
        // Equivalente ao: if (!SIGNING_SECRET) throw new Error(...)
        if (webhookSecret == null || webhookSecret.isEmpty()) {
            log.error("❌ Error: Clerk webhook secret not set");
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error: Clerk webhook secret not set")));
        }

        // Equivalente ao: if (!svix_id || !svix_timestamp || !svix_signature)
        if (svixId == null || svixTimestamp == null || svixSignature == null) {
            log.warn("⚠️ Error: Missing Svix headers");
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error: Missing Svix headers")));
        }

        return verifyAndProcessWebhook(payload, svixId, svixTimestamp, svixSignature, webhookSecret)
                .onErrorResume(error -> {
                    log.error("💥 Erro no webhook:", error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", error.getMessage())));
                });
    }

    /**
     * Verifica e processa webhook usando validação HMAC manual (equivalente ao Svix)
     */
    private Mono<ResponseEntity<Object>> verifyAndProcessWebhook(String payload, String svixId, 
                                                               String svixTimestamp, String svixSignature, 
                                                               String webhookSecret) {
        return Mono.fromCallable(() -> {
            // Validar assinatura usando o algoritmo Svix
            if (!validateSvixSignature(payload, svixId, svixTimestamp, svixSignature, webhookSecret)) {
                log.error("🔒 Error: Could not verify webhook");
                throw new RuntimeException("Error: Verification error");
            }
            
            try {
                return objectMapper.readTree(payload);
            } catch (Exception e) {
                log.error("🔍 Error parsing webhook payload", e);
                throw new RuntimeException("Error: Invalid payload format");
            }
        })
        .flatMap(this::processWebhookEvent);
    }

    /**
     * Valida assinatura Svix exatamente como o Next.js faz
     * Algoritmo: HMAC-SHA256 de svix_id + "." + svix_timestamp + "." + payload
     */
    private boolean validateSvixSignature(String payload, String svixId, String svixTimestamp, 
                                         String svixSignature, String secret) {
        try {
            log.info("🔍 Debug - Secret usado: {}", secret.length() > 10 ? secret.substring(0, 10) + "..." : secret);
            log.info("🔍 Debug - Headers recebidos: ID={}, Timestamp={}", svixId, svixTimestamp);
            log.info("🔍 Debug - Signature recebida: {}", svixSignature);
            
            // Formato Svix: id.timestamp.payload
            String signedPayload = svixId + "." + svixTimestamp + "." + payload;
            log.info("🔍 Debug - Signed payload length: {}", signedPayload.length());
            
            // Remover prefixo do secret se existir
            String cleanSecret = secret.startsWith("whsec_") ? secret.substring(6) : secret;
            byte[] secretBytes = Base64.getDecoder().decode(cleanSecret);
            
            // HMAC SHA256
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(secretBytes, ALGORITHM);
            mac.init(secretKey);
            
            byte[] computedHash = mac.doFinal(signedPayload.getBytes(StandardCharsets.UTF_8));
            String computedSignature = Base64.getEncoder().encodeToString(computedHash);
            log.info("🔍 Debug - Computed signature: {}", computedSignature);
            
            // Clerk envia assinaturas no formato: v1,assinatura_base64
            String[] signatures = svixSignature.split(",");
            log.info("🔍 Debug - Total signatures to check: {}", signatures.length);
            
            for (String sig : signatures) {
                String trimmedSig = sig.trim();
                log.info("🔍 Debug - Checking signature part: {}", trimmedSig);
                
                // Pula a versão (v1) e compara apenas as assinaturas base64
                if (!trimmedSig.startsWith("v") && trimmedSig.length() > 10) {
                    log.info("🔍 Debug - Comparing computed: {} vs provided: {}", computedSignature, trimmedSig);
                    if (computedSignature.equals(trimmedSig)) {
                        log.info("✅ Signature validated successfully!");
                        return true;
                    }
                }
            }
            
            log.warn("❌ Signature validation failed - no matching signature found");
            return false;
            
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalArgumentException e) {
            log.error("🔐 Error validating Svix signature", e);
            return false;
        }
    }

    /**
     * Processa evento verificado - focado em eventos de sessão
     */
    private Mono<ResponseEntity<Object>> processWebhookEvent(JsonNode webhookData) {
        String eventType = webhookData.get("type").asText();
        JsonNode sessionData = webhookData.get("data");
        
        // Para eventos de sessão, pega o session ID
        String sessionId = sessionData.get("id").asText();
        String userId = sessionData.has("user_id") ? sessionData.get("user_id").asText() : "N/A";

        if (sessionId == null || sessionId.isEmpty()) {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No session ID provided")));
        }

        log.info("🔄 Processando evento: {} para sessão: {} (usuário: {})", eventType, sessionId, userId);

        return switch (eventType) {
            case "session.created" -> handleSessionCreated(sessionData);
            case "session.pending" -> handleSessionPending(sessionData);
            case "session.removed" -> handleSessionRemoved(sessionData);  
            case "session.ended" -> handleSessionEnded(sessionData);
            default -> {
                log.info("ℹ️ Evento não tratado: {}", eventType);
                yield Mono.just(ResponseEntity.ok(Map.of("message", "Event type not handled")));
            }
        };
    }

    /**
     * Handlers de eventos de sessão
     */
    private Mono<ResponseEntity<Object>> handleSessionCreated(JsonNode sessionData) {
        String sessionId = sessionData.get("id").asText();
        String userId = sessionData.get("user_id").asText();
        String clientId = sessionData.has("client_id") ? sessionData.get("client_id").asText() : null;
        
        log.info("🔑 Login realizado - Sessão: {} | Usuário: {} | Cliente: {}", sessionId, userId, clientId);
        
        // Buscar dados completos do usuário no evento (se disponíveis)
        // Nota: Para session.created, precisamos buscar os dados do usuário via API do Clerk
        // ou aguardar que o webhook envie os dados do usuário junto
        
        // Por enquanto, vamos criar um usuário básico com o clerk_user_id
        // Em produção, você deveria fazer uma chamada para a API do Clerk para pegar os dados completos
    return userService.ensureUserByClerkId(userId)
        .map(user -> {
            log.info("✅ User ensured in database: {} (Clerk ID: {})", user.getEmail(), user.getClerkUserId());
            userService.recordLogin();
            Map<String, Object> response = Map.of(
                "status", "success",
                "action", "session.created",
                "session", Map.of(
                    "sessionId", sessionId,
                    "userId", userId,
                    "loginAt", System.currentTimeMillis()
                ),
                "user", Map.of(
                    "id", user.getId(),
                    "clerkUserId", user.getClerkUserId(),
                    "email", user.getEmail() != null ? user.getEmail() : "unknown"
                )
            );
            return ResponseEntity.ok((Object) response);
        })
        .onErrorResume(error -> {
            log.error("❌ Error ensuring user in database during login", error);
            Map<String, Object> errorResponse = Map.of(
                "error", "Failed to ensure user during login", 
                "message", error.getMessage()
            );
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body((Object) errorResponse));
        });
    }

    private Mono<ResponseEntity<Object>> handleSessionPending(JsonNode sessionData) {
        String sessionId = sessionData.get("id").asText();
        String userId = sessionData.get("user_id").asText();
        
        log.info("⏳ Login pendente (2FA?) - Sessão: {} | Usuário: {}", sessionId, userId);
        
        Map<String, Object> response = Map.of(
                "status", "success",
                "action", "session.pending",
                "session", Map.of(
                        "sessionId", sessionId,
                        "userId", userId,
                        "pendingAt", System.currentTimeMillis()
                )
        );
        return Mono.just(ResponseEntity.ok((Object) response));
    }

    private Mono<ResponseEntity<Object>> handleSessionRemoved(JsonNode sessionData) {
        String sessionId = sessionData.get("id").asText();
        String userId = sessionData.get("user_id").asText();
        
        log.info("🚪 Logout manual - Sessão: {} | Usuário: {}", sessionId, userId);
        
        // TODO: Registrar logout no banco, limpar cache, etc.
        
        Map<String, Object> response = Map.of(
                "status", "success",
                "action", "session.removed",
                "session", Map.of(
                        "sessionId", sessionId,
                        "userId", userId,
                        "logoutAt", System.currentTimeMillis()
                )
        );
        return Mono.just(ResponseEntity.ok((Object) response));
    }

    private Mono<ResponseEntity<Object>> handleSessionEnded(JsonNode sessionData) {
        String sessionId = sessionData.get("id").asText();
        String userId = sessionData.get("user_id").asText();
        
        log.info("⏰ Sessão expirou - Sessão: {} | Usuário: {}", sessionId, userId);
        
        // TODO: Cleanup de sessão expirada, notificações, etc.
        
        Map<String, Object> response = Map.of(
                "status", "success",
                "action", "session.ended",
                "session", Map.of(
                        "sessionId", sessionId,
                        "userId", userId,
                        "expiredAt", System.currentTimeMillis()
                )
        );
        return Mono.just(ResponseEntity.ok((Object) response));
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<String>> healthCheck() {
        boolean secretConfigured = !clerkProperties.getWebhook().getSecret().isEmpty();
        String message = String.format("🚀 Clerk webhook endpoint is healthy | Secret: %s", 
                                      secretConfigured ? "✅ CONFIGURED" : "❌ NOT SET");
        return Mono.just(ResponseEntity.ok(message));
    }
}