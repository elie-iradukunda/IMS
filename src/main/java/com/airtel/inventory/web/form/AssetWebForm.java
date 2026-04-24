package com.airtel.inventory.web.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.airtel.inventory.domain.Asset;
import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;
import com.airtel.inventory.service.dto.AssetForm;

public class AssetWebForm {

    private Long id;
    private String assetTag;
    private String serialNumber;
    private AssetType assetType;
    private String brand;
    private String model;
    private String operatingSystem;
    private String processor;
    private Integer ramGb;
    private Integer storageGb;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate purchaseDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate warrantyExpiry;
    private Long homeDepartmentId;
    private AssetCondition condition;
    private AssetStatus status;
    private String notes;

    public static AssetWebForm empty() {
        AssetWebForm form = new AssetWebForm();
        form.setPurchaseDate(LocalDate.now());
        form.setRamGb(8);
        form.setStorageGb(256);
        form.setCondition(AssetCondition.GOOD);
        form.setStatus(AssetStatus.AVAILABLE);
        return form;
    }

    public static AssetWebForm fromAsset(Asset asset) {
        AssetWebForm form = new AssetWebForm();
        form.setId(asset.getId());
        form.setAssetTag(asset.getAssetTag());
        form.setSerialNumber(asset.getSerialNumber());
        form.setAssetType(asset.getAssetType());
        form.setBrand(asset.getBrand());
        form.setModel(asset.getModel());
        form.setOperatingSystem(asset.getOperatingSystem());
        form.setProcessor(asset.getProcessor());
        form.setRamGb(asset.getRamGb());
        form.setStorageGb(asset.getStorageGb());
        form.setPurchaseDate(asset.getPurchaseDate());
        form.setWarrantyExpiry(asset.getWarrantyExpiry());
        form.setHomeDepartmentId(asset.getHomeDepartment() != null ? asset.getHomeDepartment().getId() : null);
        form.setCondition(asset.getCondition());
        form.setStatus(asset.getStatus());
        form.setNotes(asset.getNotes());
        return form;
    }

    public AssetForm toAssetForm() {
        return new AssetForm(
                assetTag,
                serialNumber,
                assetType,
                brand,
                model,
                operatingSystem,
                processor,
                ramGb,
                storageGb,
                purchaseDate,
                warrantyExpiry,
                homeDepartmentId,
                condition,
                status,
                notes);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getHomeDepartmentId() {
        return homeDepartmentId;
    }

    public void setHomeDepartmentId(Long homeDepartmentId) {
        this.homeDepartmentId = homeDepartmentId;
    }

    public AssetCondition getCondition() {
        return condition;
    }

    public void setCondition(AssetCondition condition) {
        this.condition = condition;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
