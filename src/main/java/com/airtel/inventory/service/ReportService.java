package com.airtel.inventory.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.airtel.inventory.domain.Asset;
import com.airtel.inventory.domain.AssetMovement;
import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.service.dto.ReportFilter;

@Service
public class ReportService {

    private static final DateTimeFormatter EXPORT_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final AssetService assetService;
    private final AssignmentService assignmentService;
    private final Path exportDirectory;

    public ReportService(AssetService assetService, AssignmentService assignmentService,
            @Value("${app.export-directory:./exports}") String exportDirectory) {
        this.assetService = assetService;
        this.assignmentService = assignmentService;
        this.exportDirectory = Paths.get(exportDirectory);
    }

    public List<Asset> getFilteredAssets(ReportFilter filter) {
        return assetService.getAllAssets().stream()
                .filter(asset -> matchesAsset(asset, filter))
                .toList();
    }

    public List<AssetMovement> getFilteredMovements(ReportFilter filter) {
        return assignmentService.getMovementHistory().stream()
                .filter(movement -> matchesMovement(movement, filter))
                .toList();
    }

    public Path exportAssetsReport(ReportFilter filter) {
        List<String> lines = new ArrayList<>();
        lines.add("Asset Tag,Type,Brand,Model,Department,Assigned Employee,Status,Condition,Purchase Date,Last Movement");
        for (Asset asset : getFilteredAssets(filter)) {
            lines.add(String.join(",",
                    csv(asset.getAssetTag()),
                    csv(Objects.toString(asset.getAssetType(), "")),
                    csv(asset.getBrand()),
                    csv(asset.getModel()),
                    csv(asset.getAssignedDepartment() != null ? asset.getAssignedDepartment().getName()
                            : asset.getHomeDepartment().getName()),
                    csv(asset.getAssignedEmployee() != null ? asset.getAssignedEmployee().getFullName() : ""),
                    csv(Objects.toString(asset.getStatus(), "")),
                    csv(Objects.toString(asset.getCondition(), "")),
                    csv(Objects.toString(asset.getPurchaseDate(), "")),
                    csv(formatDate(asset.getLastMovementAt()))));
        }
        return writeExport("assets-report", lines);
    }

    public Path exportMovementsReport(ReportFilter filter) {
        List<String> lines = new ArrayList<>();
        lines.add("Asset Tag,Movement Type,Department,Employee,Issued By,Issued At,Returned By,Returned At,Condition At Issue,Condition At Return,Status");
        for (AssetMovement movement : getFilteredMovements(filter)) {
            lines.add(String.join(",",
                    csv(movement.getAsset().getAssetTag()),
                    csv(Objects.toString(movement.getMovementType(), "")),
                    csv(movement.getToDepartment() != null ? movement.getToDepartment().getName() : ""),
                    csv(movement.getEmployee() != null ? movement.getEmployee().getFullName() : ""),
                    csv(movement.getIssuedBy()),
                    csv(formatDate(movement.getIssuedAt())),
                    csv(movement.getReturnedBy()),
                    csv(formatDate(movement.getReturnedAt())),
                    csv(Objects.toString(movement.getConditionAtIssue(), "")),
                    csv(Objects.toString(movement.getConditionAtReturn(), "")),
                    csv(movement.getLifecycleStatus())));
        }
        return writeExport("movement-report", lines);
    }

    private Path writeExport(String prefix, List<String> lines) {
        try {
            Files.createDirectories(exportDirectory);
            Path targetFile = exportDirectory.resolve(prefix + "-" + EXPORT_TIMESTAMP.format(LocalDateTime.now()) + ".csv");
            Files.write(targetFile, lines, StandardCharsets.UTF_8);
            return targetFile.toAbsolutePath();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write the report file.", ex);
        }
    }

    private boolean matchesAsset(Asset asset, ReportFilter filter) {
        if (filter == null) {
            return true;
        }
        if (filter.departmentId() != null) {
            Long departmentId = asset.getAssignedDepartment() != null ? asset.getAssignedDepartment().getId()
                    : asset.getHomeDepartment().getId();
            if (!filter.departmentId().equals(departmentId)) {
                return false;
            }
        }
        if (filter.assetType() != null && filter.assetType() != asset.getAssetType()) {
            return false;
        }
        if (filter.assetStatus() != null && filter.assetStatus() != asset.getStatus()) {
            return false;
        }
        LocalDate referenceDate = asset.getLastMovementAt() != null ? asset.getLastMovementAt().toLocalDate()
                : asset.getPurchaseDate();
        return matchesDateRange(referenceDate, filter);
    }

    private boolean matchesMovement(AssetMovement movement, ReportFilter filter) {
        if (filter == null) {
            return true;
        }
        if (filter.departmentId() != null) {
            if (movement.getToDepartment() == null || !filter.departmentId().equals(movement.getToDepartment().getId())) {
                return false;
            }
        }
        if (filter.assetType() != null && filter.assetType() != movement.getAsset().getAssetType()) {
            return false;
        }
        if (filter.assetStatus() != null) {
            if (filter.assetStatus() == AssetStatus.ASSIGNED && movement.getReturnedAt() != null) {
                return false;
            }
            if (filter.assetStatus() == AssetStatus.AVAILABLE && movement.getReturnedAt() == null) {
                return false;
            }
        }
        return matchesDateRange(movement.getIssuedAt().toLocalDate(), filter);
    }

    private boolean matchesDateRange(LocalDate referenceDate, ReportFilter filter) {
        if (referenceDate == null) {
            return filter.fromDate() == null && filter.toDate() == null;
        }
        if (filter.fromDate() != null && referenceDate.isBefore(filter.fromDate())) {
            return false;
        }
        if (filter.toDate() != null && referenceDate.isAfter(filter.toDate())) {
            return false;
        }
        return true;
    }

    private String formatDate(LocalDateTime value) {
        return value != null ? value.toString() : "";
    }

    private String csv(String value) {
        String safeValue = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + safeValue + "\"";
    }
}
