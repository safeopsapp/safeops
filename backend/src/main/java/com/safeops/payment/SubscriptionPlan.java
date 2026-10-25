package com.safeops.payment;

public enum SubscriptionPlan {
    TRIAL("Free Compliance Trial", 0),
    STARTER_SITES("Starter Construction Sites (Up to 3 Sites)", 4999),
    SAFETY_PRO("Safety Pro Enterprise (Up to 15 Sites)", 19999),
    ENTERPRISE_EPC("Enterprise EPC Infrastructure Unlimited", 49999);

    private final String displayName;
    private final int monthlyRateInr;

    SubscriptionPlan(String displayName, int monthlyRateInr) {
        this.displayName = displayName;
        this.monthlyRateInr = monthlyRateInr;
    }

    public String getDisplayName() { return displayName; }
    public int getMonthlyRateInr() { return monthlyRateInr; }
}

