package com.airtel.inventory.domain;

public enum AuditAction {
    ASSET_REGISTERED("Asset Registered"),
    ASSET_UPDATED("Asset Updated"),
    ASSET_RETIRED("Asset Retired"),
    ASSET_ISSUED("Asset Issued"),
    ASSET_RETURNED("Asset Returned");

    private final String label;

    AuditAction(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
