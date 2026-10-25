package com.safeops.payment.dto;

import com.safeops.payment.PaymentMethod;
import com.safeops.payment.PaymentStatus;
import com.safeops.payment.PaymentTransaction;
import com.safeops.payment.SubscriptionPlan;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PaymentTransactionResponse {
    private UUID id;
    private UUID organizationId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private SubscriptionPlan subscriptionPlan;
    private String transactionReference;
    private String gatewayPaymentId;
    private String invoiceNumber;
    private Instant createdAt;

    public static PaymentTransactionResponse from(PaymentTransaction tx) {
        PaymentTransactionResponse res = new PaymentTransactionResponse();
        res.id = tx.getId();
        res.organizationId = tx.getOrganizationId();
        res.amount = tx.getAmount();
        res.currency = tx.getCurrency();
        res.status = tx.getStatus();
        res.paymentMethod = tx.getPaymentMethod();
        res.subscriptionPlan = tx.getSubscriptionPlan();
        res.transactionReference = tx.getTransactionReference();
        res.gatewayPaymentId = tx.getGatewayPaymentId();
        res.invoiceNumber = tx.getInvoiceNumber();
        res.createdAt = tx.getCreatedAt();
        return res;
    }

    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentStatus getStatus() { return status; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }
    public String getTransactionReference() { return transactionReference; }
    public String getGatewayPaymentId() { return gatewayPaymentId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public Instant getCreatedAt() { return createdAt; }
}

