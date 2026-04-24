package com.airtel.inventory.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "assets", uniqueConstraints = {
        @UniqueConstraint(name = "uk_asset_tag", columnNames = "asset_tag"),
        @UniqueConstraint(name = "uk_asset_serial_number", columnNames = "serial_number")
})
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_tag", nullable = false, length = 50)
    private String assetTag;

    @Column(name = "serial_number", nullable = false, length = 80)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false, length = 40)
    private AssetType assetType;

    @Column(nullable = false, length = 80)
    private String brand;

    @Column(nullable = false, length = 120)
    private String model;

    @Column(name = "operating_system", length = 120)
    private String operatingSystem;

    @Column(length = 120)
    private String processor;

    @Column(name = "ram_gb")
    private Integer ramGb;

    @Column(name = "storage_gb")
    private Integer storageGb;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "warranty_expiry")
    private LocalDate warrantyExpiry;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "home_department_id", nullable = false)
    private Department homeDepartment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_department_id")
    private Department assignedDepartment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_status", nullable = false, length = 40)
    private AssetStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_condition", nullable = false, length = 40)
    private AssetCondition condition;

    @Column(length = 1500)
    private String notes;

    @Column(name = "last_movement_at")
    private LocalDateTime lastMovementAt;

    public Long getId() {
        return id;
    }

    public String getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public Integer getRamGb() {
        return ramGb;
    }

    public void setRamGb(Integer ramGb) {
        this.ramGb = ramGb;
    }

    public Integer getStorageGb() {
        return storageGb;
    }

    public void setStorageGb(Integer storageGb) {
        this.storageGb = storageGb;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getWarrantyExpiry() {
        return warrantyExpiry;
    }

    public void setWarrantyExpiry(LocalDate warrantyExpiry) {
        this.warrantyExpiry = warrantyExpiry;
    }

    public Department getHomeDepartment() {
        return homeDepartment;
    }

    public void setHomeDepartment(Department homeDepartment) {
        this.homeDepartment = homeDepartment;
    }

    public Department getAssignedDepartment() {
        return assignedDepartment;
    }

    public void setAssignedDepartment(Department assignedDepartment) {
        this.assignedDepartment = assignedDepartment;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }

    public AssetCondition getCondition() {
        return condition;
    }

    public void setCondition(AssetCondition condition) {
        this.condition = condition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getLastMovementAt() {
        return lastMovementAt;
    }

    public void setLastMovementAt(LocalDateTime lastMovementAt) {
        this.lastMovementAt = lastMovementAt;
    }

    public String getDisplayName() {
        return assetTag + " - " + brand + " " + model;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
