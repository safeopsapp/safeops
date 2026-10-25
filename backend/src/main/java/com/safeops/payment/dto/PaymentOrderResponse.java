package com.safeops.payment.dto;

import com.safeops.payment.PaymentMethod;
import com.safeops.payment.SubscriptionPlan;
import java.math.BigDecimal;
import java.util.UUID;

public class PaymentOrderResponse {
    private UUID transactionId;
    private String transactionReference;
    private String gatewayOrderId;
    private BigDecimal amount;
    private String currency;
    private SubscriptionPlan plan;
    private PaymentMethod method;
    private String gatewayKeyId;

    public PaymentOrderResponse(UUID transactionId, String transactionReference, String gatewayOrderId,
                                BigDecimal amount, String currency, SubscriptionPlan plan,
                                PaymentMethod method, String gatewayKeyId) {
        this.transactionId = transactionId;
        this.transactionReference = transactionReference;
        this.gatewayOrderId = gatewayOrderId;
        this.amount = amount;
        this.currency = currency;
        this.plan = plan;
        this.method = method;
        this.gatewayKeyId = gatewayKeyId;
    }

    public UUID getTransactionId() { return transactionId; }
    public String getTransactionReference() { return transactionReference; }
    public String getGatewayOrderId() { return gatewayOrderId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public SubscriptionPlan getPlan() { return plan; }
    public PaymentMethod getMethod() { return method; }
    public String getGatewayKeyId() { return gatewayKeyId; }
}

