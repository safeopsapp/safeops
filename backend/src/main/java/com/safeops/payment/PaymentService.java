package com.safeops.payment;

import com.safeops.payment.dto.CreatePaymentOrderRequest;
import com.safeops.payment.dto.PaymentOrderResponse;
import com.safeops.payment.dto.PaymentTransactionResponse;
import com.safeops.payment.dto.PaymentVerificationRequest;
import com.safeops.security.SafeOpsPrincipal;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentOrderResponse createPaymentOrder(SafeOpsPrincipal principal, CreatePaymentOrderRequest request);
    PaymentTransactionResponse verifyPayment(SafeOpsPrincipal principal, PaymentVerificationRequest request);
    PaymentTransactionResponse getTransaction(SafeOpsPrincipal principal, UUID transactionId);
    List<PaymentTransactionResponse> getOrganizationTransactions(SafeOpsPrincipal principal, UUID organizationId);
    void handleWebhook(String payload, String signature);
}

