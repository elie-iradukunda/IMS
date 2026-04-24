package com.airtel.inventory.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.airtel.inventory.domain.Asset;
import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.domain.AssetMovement;
import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AuditAction;
import com.airtel.inventory.domain.Department;
import com.airtel.inventory.domain.Employee;
import com.airtel.inventory.domain.MovementType;
import com.airtel.inventory.repository.AssetMovementRepository;
import com.airtel.inventory.repository.AssetRepository;
import com.airtel.inventory.service.dto.AssignmentRequest;
import com.airtel.inventory.service.dto.ReturnRequest;

@Service
public class AssignmentService {

    private final AssetRepository assetRepository;
    private final AssetMovementRepository assetMovementRepository;
    private final AssetService assetService;
    private final ReferenceDataService referenceDataService;
    private final AuditService auditService;

    public AssignmentService(AssetRepository assetRepository, AssetMovementRepository assetMovementRepository,
            AssetService assetService, ReferenceDataService referenceDataService, AuditService auditService) {
        this.assetRepository = assetRepository;
        this.assetMovementRepository = assetMovementRepository;
        this.assetService = assetService;
        this.referenceDataService = referenceDataService;
        this.auditService = auditService;
    }

    public List<AssetMovement> getOpenAssignments() {
        return assetMovementRepository.findByReturnedAtIsNullOrderByIssuedAtDesc();
    }

    public List<AssetMovement> getMovementHistory() {
        return assetMovementRepository.findAll(Sort.by(Sort.Direction.DESC, "issuedAt"));
    }

    public List<AssetMovement> getRecentActivity() {
        return assetMovementRepository.findTop20ByOrderByIssuedAtDesc();
    }

    @Transactional
    public AssetMovement issueAsset(AssignmentRequest request) {
        if (request.assetId() == null) {
            throw new IllegalArgumentException("Please choose an asset to issue.");
        }
        if (request.departmentId() == null) {
            throw new IllegalArgumentException("Please choose the receiving department.");
        }
        if (request.employeeId() == null) {
            throw new IllegalArgumentException("Please choose the receiving employee.");
        }

        Asset asset = assetService.getAsset(request.assetId());
        if (asset.getStatus() == AssetStatus.RETIRED) {
            throw new IllegalStateException("Retired assets cannot be assigned.");
        }
        if (asset.getAssignedEmployee() != null
                || assetMovementRepository.findFirstByAssetIdAndReturnedAtIsNull(asset.getId()).isPresent()) {
            throw new IllegalStateException("This asset is already assigned.");
        }

        Department department = referenceDataService.getDepartment(request.departmentId());
        Employee employee = referenceDataService.getEmployee(request.employeeId());
        if (!employee.getDepartment().getId().equals(department.getId())) {
            throw new IllegalArgumentException("The selected employee does not belong to the selected department.");
        }

        LocalDateTime issuedAt = combineDate(request.issueDate());
        AssetCondition conditionAtIssue = request.conditionAtIssue() != null ? request.conditionAtIssue()
                : asset.getCondition();

        AssetMovement movement = new AssetMovement();
        movement.setAsset(asset);
        movement.setMovementType(MovementType.ISSUE);
        movement.setFromDepartment(asset.getAssignedDepartment() != null ? asset.getAssignedDepartment()
                : asset.getHomeDepartment());
        movement.setToDepartment(department);
        movement.setEmployee(employee);
        movement.setIssuedBy(resolveActor(request.issuedBy()));
        movement.setIssuedAt(issuedAt);
        movement.setConditionAtIssue(conditionAtIssue);
        movement.setIssueNotes(normalize(request.notes()));

        AssetMovement savedMovement = assetMovementRepository.save(movement);

        asset.setAssignedDepartment(department);
        asset.setAssignedEmployee(employee);
        asset.setStatus(AssetStatus.ASSIGNED);
        asset.setCondition(conditionAtIssue);
        asset.setLastMovementAt(issuedAt);
        assetRepository.save(asset);

        auditService.log(asset, AuditAction.ASSET_ISSUED, request.issuedBy(),
                "Issued " + asset.getAssetTag() + " to " + employee.getFullName() + " (" + department.getName()
                        + ").");
        return savedMovement;
    }

    @Transactional
    public AssetMovement returnAsset(ReturnRequest request) {
        if (request.movementId() == null) {
            throw new IllegalArgumentException("Please choose an active assignment to return.");
        }

        AssetMovement movement = assetMovementRepository.findById(request.movementId())
                .orElseThrow(() -> new IllegalArgumentException("The selected assignment could not be found."));
        if (movement.getReturnedAt() != null) {
            throw new IllegalStateException("This assignment has already been returned.");
        }

        Asset asset = movement.getAsset();
        AssetCondition returnedCondition = request.conditionAtReturn() != null ? request.conditionAtReturn()
                : movement.getConditionAtIssue();
        LocalDateTime returnedAt = combineDate(request.returnDate());

        movement.setReturnedAt(returnedAt);
        movement.setReturnedBy(resolveActor(request.returnedBy()));
        movement.setConditionAtReturn(returnedCondition);
        movement.setReturnNotes(normalize(request.notes()));
        movement.setMovementType(MovementType.RETURN);

        AssetMovement savedMovement = assetMovementRepository.save(movement);

        asset.setAssignedDepartment(null);
        asset.setAssignedEmployee(null);
        asset.setCondition(returnedCondition);
        asset.setStatus(returnedCondition == AssetCondition.DAMAGED ? AssetStatus.IN_MAINTENANCE : AssetStatus.AVAILABLE);
        asset.setLastMovementAt(returnedAt);
        assetRepository.save(asset);

        auditService.log(asset, AuditAction.ASSET_RETURNED, request.returnedBy(),
                "Returned " + asset.getAssetTag() + " from "
                        + (movement.getEmployee() != null ? movement.getEmployee().getFullName() : "unassigned holder")
                        + ".");
        return savedMovement;
    }

    private LocalDateTime combineDate(LocalDate date) {
        LocalDate resolvedDate = date != null ? date : LocalDate.now();
        return LocalDateTime.of(resolvedDate, LocalTime.of(9, 0));
    }

    private String resolveActor(String actor) {
        if (StringUtils.hasText(actor)) {
            return actor.trim();
        }
        return System.getProperty("user.name", "system-operator");
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
