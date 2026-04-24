package com.airtel.inventory.service.dto;

import java.time.LocalDate;

import com.airtel.inventory.domain.AssetCondition;

public record ReturnRequest(
        Long movementId,
        String returnedBy,
        LocalDate returnDate,
        AssetCondition conditionAtReturn,
        String notes) {
}
