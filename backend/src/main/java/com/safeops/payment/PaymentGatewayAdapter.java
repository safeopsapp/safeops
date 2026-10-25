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

public interface PaymentGatewayAdapter {
    String createGatewayOrder(String transactionRef, BigDecimal amount, String currency);
    boolean verifyWebhookSignature(String payload, String signature);
    boolean verifyPaymentSignature(String orderId, String paymentId, String signature);
    String getGatewayKeyId();
}

