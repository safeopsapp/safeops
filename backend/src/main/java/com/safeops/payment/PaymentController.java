package com.safeops.payment;

import com.safeops.payment.dto.CreatePaymentOrderRequest;
import com.safeops.payment.dto.PaymentOrderResponse;
import com.safeops.payment.dto.PaymentTransactionResponse;
import com.safeops.payment.dto.PaymentVerificationRequest;
import com.safeops.security.SafeOpsPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments & Subscription Billing", description = "Endpoints for managing license orders, gateway verification, and billing receipts.")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/order")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create checkout subscription order")
    public ResponseEntity<PaymentOrderResponse> createOrder(
        @AuthenticationPrincipal SafeOpsPrincipal principal,
        @Valid @RequestBody CreatePaymentOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(paymentService.createPaymentOrder(principal, request));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify gateway payment confirmation signature")
    public ResponseEntity<PaymentTransactionResponse> verifyPayment(
        @AuthenticationPrincipal SafeOpsPrincipal principal,
        @Valid @RequestBody PaymentVerificationRequest request
    ) {
        return ResponseEntity.ok(paymentService.verifyPayment(principal, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction details by ID")
    public ResponseEntity<PaymentTransactionResponse> getTransaction(
        @AuthenticationPrincipal SafeOpsPrincipal principal,
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(paymentService.getTransaction(principal, id));
    }

    @GetMapping("/organization/{orgId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get billing history for an organization")
    public ResponseEntity<List<PaymentTransactionResponse>> getOrganizationHistory(
        @AuthenticationPrincipal SafeOpsPrincipal principal,
        @PathVariable UUID orgId
    ) {
        return ResponseEntity.ok(paymentService.getOrganizationTransactions(principal, orgId));
    }

    @PostMapping("/webhook")
    @Operation(summary = "Gateway webhook handler")
    public ResponseEntity<Map<String, String>> handleWebhook(
        @RequestBody String payload,
        @RequestHeader(value = "X-Razorpay-Signature", required = false) String rzpSignature,
        @RequestHeader(value = "Stripe-Signature", required = false) String stripeSignature
    ) {
        String signature = rzpSignature != null ? rzpSignature : stripeSignature;
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok(Map.of("status", "received"));
    }
}

