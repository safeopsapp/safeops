package com.safeops.payment;

import com.safeops.payment.dto.CreatePaymentOrderRequest;
import com.safeops.payment.dto.PaymentOrderResponse;
import com.safeops.payment.dto.PaymentTransactionResponse;
import com.safeops.payment.dto.PaymentVerificationRequest;
import com.safeops.security.SafeOpsPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentTransactionRepository paymentRepo;
    private final PaymentGatewayAdapter gatewayAdapter;

    public PaymentServiceImpl(PaymentTransactionRepository paymentRepo, PaymentGatewayAdapter gatewayAdapter) {
        this.paymentRepo = paymentRepo;
        this.gatewayAdapter = gatewayAdapter;
    }

    @Override
    public PaymentOrderResponse createPaymentOrder(SafeOpsPrincipal principal, CreatePaymentOrderRequest req) {
        UUID customerId = principal.getCustomerId();
        if (customerId == null) {
            throw new AccessDeniedException("User must belong to a verified customer organization to purchase subscriptions.");
        }

        BigDecimal amount = BigDecimal.valueOf(req.getPlan().getMonthlyRateInr());
        String txRef = "SO-" + Year.now().getValue() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PaymentTransaction tx = new PaymentTransaction(
            customerId,
            req.getOrganizationId(),
            amount,
            req.getPlan(),
            req.getMethod(),
            txRef
        );

        String gatewayOrderId = gatewayAdapter.createGatewayOrder(txRef, amount, "INR");
        tx.setGatewayOrderId(gatewayOrderId);

        PaymentTransaction saved = paymentRepo.save(tx);
        log.info("Initialized payment order id={} ref={} for org={}", saved.getId(), txRef, req.getOrganizationId());

        return new PaymentOrderResponse(
            saved.getId(),
            saved.getTransactionReference(),
            saved.getGatewayOrderId(),
            saved.getAmount(),
            saved.getCurrency(),
            saved.getSubscriptionPlan(),
            saved.getPaymentMethod(),
            gatewayAdapter.getGatewayKeyId()
        );
    }

    @Override
    public PaymentTransactionResponse verifyPayment(SafeOpsPrincipal principal, PaymentVerificationRequest req) {
        PaymentTransaction tx = paymentRepo.findById(req.getTransactionId())
            .orElseThrow(() -> new NoSuchElementException("Payment transaction not found: " + req.getTransactionId()));

        if (!tx.getCustomerId().equals(principal.getCustomerId())) {
            throw new AccessDeniedException("Tenant isolation violation: Transaction belongs to another customer.");
        }

        boolean isValid = gatewayAdapter.verifyPaymentSignature(
            req.getGatewayOrderId(),
            req.getGatewayPaymentId(),
            req.getGatewaySignature()
        );

        if (!isValid) {
            tx.setStatus(PaymentStatus.FAILED);
            paymentRepo.save(tx);
            log.warn("Payment signature verification failed for tx={}", tx.getId());
            throw new IllegalArgumentException("Invalid payment signature from gateway.");
        }

        tx.setStatus(PaymentStatus.SUCCESS);
        tx.setGatewayPaymentId(req.getGatewayPaymentId());
        tx.setInvoiceNumber("INV-" + Year.now().getValue() + "-" + tx.getTransactionReference().substring(3));
        PaymentTransaction updated = paymentRepo.save(tx);

        log.info("Payment confirmed successfully for tx={} invoice={}", updated.getId(), updated.getInvoiceNumber());
        return PaymentTransactionResponse.from(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentTransactionResponse getTransaction(SafeOpsPrincipal principal, UUID transactionId) {
        PaymentTransaction tx = paymentRepo.findById(transactionId)
            .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + transactionId));

        if (!tx.getCustomerId().equals(principal.getCustomerId())) {
            throw new AccessDeniedException("Tenant isolation violation");
        }
        return PaymentTransactionResponse.from(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponse> getOrganizationTransactions(SafeOpsPrincipal principal, UUID organizationId) {
        return paymentRepo.findByOrganizationIdOrderByCreatedAtDesc(organizationId)
            .stream()
            .map(PaymentTransactionResponse::from)
            .toList();
    }

    @Override
    public void handleWebhook(String payload, String signature) {
        if (!gatewayAdapter.verifyWebhookSignature(payload, signature)) {
            log.warn("Rejected unauthorized payment webhook: invalid signature");
            throw new AccessDeniedException("Invalid webhook HMAC signature");
        }
        log.info("Processed verified payment webhook event");
    }
}

