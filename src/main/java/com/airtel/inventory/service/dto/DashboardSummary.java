package com.airtel.inventory.service.dto;

import java.util.Map;

import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;

public record DashboardSummary(
        long totalAssets,
        long availableAssets,
        long assignedAssets,
        long maintenanceAssets,
        long retiredAssets,
        long damagedAssets,
        long openAssignments,
        long totalAuditEntries,
        Map<AssetType, Long> assetsByType,
        Map<AssetStatus, Long> assetsByStatus) {
}
