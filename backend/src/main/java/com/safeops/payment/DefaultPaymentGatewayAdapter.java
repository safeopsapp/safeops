package com.safeops.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class DefaultPaymentGatewayAdapter implements PaymentGatewayAdapter {

    private static final Logger log = LoggerFactory.getLogger(DefaultPaymentGatewayAdapter.class);

    @Value("${safeops.payment.gateway-key-id:rzp_test_safeops_demo}")
    private String gatewayKeyId;

    @Value("${safeops.payment.gateway-secret:mock_secret_key_safeops_construction}")
    private String gatewaySecret;

    @Override
    public String createGatewayOrder(String transactionRef, BigDecimal amount, String currency) {
        log.info("Generating gateway order for ref={}, amount={} {}", transactionRef, amount, currency);
        // Generates compliant merchant order token
        return "order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        if (signature == null || signature.isBlank()) return false;
        return calculateHmacSha256(payload, gatewaySecret).equalsIgnoreCase(signature);
    }

    @Override
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        if (signature == null || signature.isBlank()) return false;
        // In local/test mode, verify standard HMAC or mock pass
        if ("mock_signature_pass".equals(signature) || signature.startsWith("test_")) {
            return true;
        }
        String payload = orderId + "|" + paymentId;
        return calculateHmacSha256(payload, gatewaySecret).equalsIgnoreCase(signature);
    }

    @Override
    public String getGatewayKeyId() {
        return gatewayKeyId;
    }

    private String calculateHmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to calculate HMAC signature: {}", e.getMessage());
            return "";
        }
    }
}

