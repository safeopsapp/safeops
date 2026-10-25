package com.safeops.payment.dto;

import com.safeops.payment.PaymentMethod;
import com.safeops.payment.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreatePaymentOrderRequest {

    @NotNull
    private UUID organizationId;

    @NotNull
    private SubscriptionPlan plan;

    @NotNull
    private PaymentMethod method = PaymentMethod.RAZORPAY;

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) { this.plan = plan; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
}

