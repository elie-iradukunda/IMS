package com.airtel.inventory.service.dto;

import java.time.LocalDate;

import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;

public record ReportFilter(
        LocalDate fromDate,
        LocalDate toDate,
        Long departmentId,
        AssetType assetType,
        AssetStatus assetStatus) {
}
