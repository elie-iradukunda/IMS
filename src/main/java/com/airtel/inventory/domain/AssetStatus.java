package com.airtel.inventory.domain;

public enum AssetStatus {
    AVAILABLE("Available"),
    ASSIGNED("Assigned"),
    IN_MAINTENANCE("In Maintenance"),
    RETIRED("Retired");

    private final String label;

    AssetStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
