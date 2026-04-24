package com.airtel.inventory.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.airtel.inventory.domain.Asset;
import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AuditAction;
import com.airtel.inventory.domain.Department;
import com.airtel.inventory.repository.AssetMovementRepository;
import com.airtel.inventory.repository.AssetRepository;
import com.airtel.inventory.repository.AuditLogRepository;
import com.airtel.inventory.service.dto.AssetForm;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMovementRepository assetMovementRepository;
    private final AuditLogRepository auditLogRepository;
    private final ReferenceDataService referenceDataService;
    private final AuditService auditService;

    public AssetService(AssetRepository assetRepository, AssetMovementRepository assetMovementRepository,
            AuditLogRepository auditLogRepository, ReferenceDataService referenceDataService, AuditService auditService) {
        this.assetRepository = assetRepository;
        this.assetMovementRepository = assetMovementRepository;
        this.auditLogRepository = auditLogRepository;
        this.referenceDataService = referenceDataService;
        this.auditService = auditService;
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll(Sort.by(Sort.Direction.ASC, "assetTag"));
    }

    public List<Asset> getAssignableAssets() {
        return assetRepository.findByStatusOrderByAssetTag(AssetStatus.AVAILABLE);
    }

    public Asset getAsset(Long assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("The selected asset could not be found."));
    }

    @Transactional
    public Asset createAsset(AssetForm form, String actorName) {
        validateForm(form, null);
        Department department = referenceDataService.getDepartment(form.homeDepartmentId());

        Asset asset = new Asset();
        mapForm(asset, form, department);
        asset.setStatus(resolveInitialStatus(form.status()));
        asset.setCondition(form.condition() != null ? form.condition() : AssetCondition.GOOD);
        asset.setLastMovementAt(LocalDateTime.now());

        Asset savedAsset = assetRepository.save(asset);
        auditService.log(savedAsset, AuditAction.ASSET_REGISTERED, actorName,
                "Registered asset " + savedAsset.getAssetTag() + " for " + department.getName() + ".");
        return savedAsset;
    }

    @Transactional
    public Asset updateAsset(Long assetId, AssetForm form, String actorName) {
        Asset asset = getAsset(assetId);
        validateForm(form, assetId);
        Department department = referenceDataService.getDepartment(form.homeDepartmentId());

        mapForm(asset, form, department);
        if (asset.getAssignedEmployee() != null) {
            asset.setStatus(AssetStatus.ASSIGNED);
        } else if (form.status() == null || form.status() == AssetStatus.ASSIGNED) {
            asset.setStatus(AssetStatus.AVAILABLE);
        } else {
            asset.setStatus(form.status());
        }
        asset.setCondition(form.condition() != null ? form.condition() : asset.getCondition());
        asset.setLastMovementAt(LocalDateTime.now());

        Asset savedAsset = assetRepository.save(asset);
        auditService.log(savedAsset, AuditAction.ASSET_UPDATED, actorName,
                "Updated asset " + savedAsset.getAssetTag() + ".");
        return savedAsset;
    }

    @Transactional
    public Asset retireAsset(Long assetId, String actorName) {
        Asset asset = getAsset(assetId);
        if (asset.getAssignedEmployee() != null) {
            throw new IllegalStateException("Please return the asset before retiring it.");
        }

        asset.setStatus(AssetStatus.RETIRED);
        asset.setLastMovementAt(LocalDateTime.now());

        Asset savedAsset = assetRepository.save(asset);
        auditService.log(savedAsset, AuditAction.ASSET_RETIRED, actorName,
                "Retired asset " + savedAsset.getAssetTag() + ".");
        return savedAsset;
    }

    @Transactional
    public void deleteAsset(Long assetId) {
        Asset asset = getAsset(assetId);
        if (asset.getAssignedEmployee() != null) {
            throw new IllegalStateException("Please return the asset before deleting it.");
        }
        if (assetMovementRepository.existsByAssetId(assetId)) {
            throw new IllegalStateException(
                    "This asset already has issue or return history. Retire it instead of deleting it.");
        }

        auditLogRepository.deleteByAssetId(assetId);
        assetRepository.delete(asset);
    }

    private void validateForm(AssetForm form, Long existingAssetId) {
        if (!StringUtils.hasText(form.assetTag())) {
            throw new IllegalArgumentException("Asset tag is required.");
        }
        if (!StringUtils.hasText(form.serialNumber())) {
            throw new IllegalArgumentException("Serial number is required.");
        }
        if (form.assetType() == null) {
            throw new IllegalArgumentException("Device type is required.");
        }
        if (!StringUtils.hasText(form.brand())) {
            throw new IllegalArgumentException("Brand is required.");
        }
        if (!StringUtils.hasText(form.model())) {
            throw new IllegalArgumentException("Model is required.");
        }
        if (form.homeDepartmentId() == null) {
            throw new IllegalArgumentException("Home department is required.");
        }

        assetRepository.findByAssetTagIgnoreCase(form.assetTag().trim())
                .filter(asset -> !asset.getId().equals(existingAssetId))
                .ifPresent(asset -> {
                    throw new IllegalArgumentException("Asset tag already exists.");
                });

        assetRepository.findBySerialNumberIgnoreCase(form.serialNumber().trim())
                .filter(asset -> !asset.getId().equals(existingAssetId))
                .ifPresent(asset -> {
                    throw new IllegalArgumentException("Serial number already exists.");
                });
    }

    private void mapForm(Asset asset, AssetForm form, Department department) {
        asset.setAssetTag(form.assetTag().trim().toUpperCase());
        asset.setSerialNumber(form.serialNumber().trim().toUpperCase());
        asset.setAssetType(form.assetType());
        asset.setBrand(form.brand().trim());
        asset.setModel(form.model().trim());
        asset.setOperatingSystem(normalize(form.operatingSystem()));
        asset.setProcessor(normalize(form.processor()));
        asset.setRamGb(form.ramGb() != null ? Math.max(form.ramGb(), 0) : null);
        asset.setStorageGb(form.storageGb() != null ? Math.max(form.storageGb(), 0) : null);
        asset.setPurchaseDate(form.purchaseDate() != null ? form.purchaseDate() : LocalDate.now());
        asset.setWarrantyExpiry(form.warrantyExpiry());
        asset.setHomeDepartment(department);
        asset.setNotes(normalize(form.notes()));
    }

    private AssetStatus resolveInitialStatus(AssetStatus requestedStatus) {
        if (requestedStatus == null || requestedStatus == AssetStatus.ASSIGNED) {
            return AssetStatus.AVAILABLE;
        }
        return requestedStatus;
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
