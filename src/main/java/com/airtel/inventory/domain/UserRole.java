package com.airtel.inventory.domain;

public enum UserRole {
    ADMIN("Administrator"),
    INVENTORY_OFFICER("Inventory Officer");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
