-- SafeOps V12: Payment Transactions and Subscription Tier Foundation

CREATE TABLE IF NOT EXISTS public.payment_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES public.customers(id),
    organization_id UUID NOT NULL REFERENCES public.organizations(id),
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(8) NOT NULL DEFAULT 'INR',
    status VARCHAR(32) NOT NULL DEFAULT 'INITIATED',
    payment_method VARCHAR(32) NOT NULL DEFAULT 'RAZORPAY',
    subscription_plan VARCHAR(64) NOT NULL,
    transaction_reference VARCHAR(64) NOT NULL UNIQUE,
    gateway_order_id VARCHAR(128),
    gateway_payment_id VARCHAR(128),
    invoice_number VARCHAR(64),
    notes VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_payments_tx_ref ON public.payment_transactions(transaction_reference);
CREATE INDEX IF NOT EXISTS idx_payments_org_id ON public.payment_transactions(organization_id);
CREATE INDEX IF NOT EXISTS idx_payments_cust_id ON public.payment_transactions(customer_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON public.payment_transactions(status);

