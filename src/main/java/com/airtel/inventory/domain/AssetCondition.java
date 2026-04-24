package com.airtel.inventory.domain;

public enum AssetCondition {
    EXCELLENT("Excellent"),
    GOOD("Good"),
    FAIR("Fair"),
    DAMAGED("Damaged");

    private final String label;

    AssetCondition(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
