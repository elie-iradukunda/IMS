package com.airtel.inventory.service;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.airtel.inventory.domain.Asset;
import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;
import com.airtel.inventory.service.dto.DashboardSummary;

@Service
public class DashboardService {

    private final AssetService assetService;
    private final AssignmentService assignmentService;
    private final AuditService auditService;

    public DashboardService(AssetService assetService, AssignmentService assignmentService, AuditService auditService) {
        this.assetService = assetService;
        this.assignmentService = assignmentService;
        this.auditService = auditService;
    }

    public DashboardSummary getSummary() {
        List<Asset> assets = assetService.getAllAssets();

        Map<AssetType, Long> byType = new EnumMap<>(AssetType.class);
        Arrays.stream(AssetType.values()).forEach(type -> byType.put(type, 0L));
        assets.forEach(asset -> byType.computeIfPresent(asset.getAssetType(), (type, count) -> count + 1));

        Map<AssetStatus, Long> byStatus = new EnumMap<>(AssetStatus.class);
        Arrays.stream(AssetStatus.values()).forEach(status -> byStatus.put(status, 0L));
        assets.forEach(asset -> byStatus.computeIfPresent(asset.getStatus(), (status, count) -> count + 1));

        long damagedAssets = assets.stream()
                .filter(asset -> asset.getCondition() == AssetCondition.DAMAGED)
                .count();

        return new DashboardSummary(
                assets.size(),
                byStatus.getOrDefault(AssetStatus.AVAILABLE, 0L),
                byStatus.getOrDefault(AssetStatus.ASSIGNED, 0L),
                byStatus.getOrDefault(AssetStatus.IN_MAINTENANCE, 0L),
                byStatus.getOrDefault(AssetStatus.RETIRED, 0L),
                damagedAssets,
                assignmentService.getOpenAssignments().size(),
                auditService.countEntries(),
                byType,
                byStatus);
    }
}
