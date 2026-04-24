package com.airtel.inventory.domain;

public enum AssetType {
    LAPTOP("Laptop"),
    DESKTOP("Desktop"),
    MOBILE_PHONE("Mobile Phone"),
    TABLET("Tablet"),
    ACCESSORY("Accessory");

    private final String label;

    AssetType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
