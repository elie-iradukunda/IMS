package com.airtel.inventory.domain;

public enum MovementType {
    ISSUE("Issue"),
    RETURN("Return");

    private final String label;

    MovementType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
