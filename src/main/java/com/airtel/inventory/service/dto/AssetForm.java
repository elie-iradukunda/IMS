package com.airtel.inventory.service.dto;

import java.time.LocalDate;

import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;

public record AssetForm(
        String assetTag,
        String serialNumber,
        AssetType assetType,
        String brand,
        String model,
        String operatingSystem,
        String processor,
        Integer ramGb,
        Integer storageGb,
        LocalDate purchaseDate,
        LocalDate warrantyExpiry,
        Long homeDepartmentId,
        AssetCondition condition,
        AssetStatus status,
        String notes) {
}
