package com.petcare.petcareapp.domain.model;

public enum SubscriptionFrequency {
    WEEKLY(7),
    BIWEEKLY(14),  // Every two weeks
    MONTHLY(30),   // Approx.
    BIMONTHLY(60); // Every two months, approx.

    private final int days;

    SubscriptionFrequency(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
