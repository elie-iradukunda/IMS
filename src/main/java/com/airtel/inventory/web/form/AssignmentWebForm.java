package com.airtel.inventory.web.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.service.dto.AssignmentRequest;

public class AssignmentWebForm {

    private Long assetId;
    private Long departmentId;
    private Long employeeId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate issueDate = LocalDate.now();
    private AssetCondition conditionAtIssue = AssetCondition.GOOD;
    private String notes;

    public AssignmentRequest toAssignmentRequest(String actorName) {
        return new AssignmentRequest(assetId, departmentId, employeeId, actorName, issueDate, conditionAtIssue, notes);
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public AssetCondition getConditionAtIssue() {
        return conditionAtIssue;
    }

    public void setConditionAtIssue(AssetCondition conditionAtIssue) {
        this.conditionAtIssue = conditionAtIssue;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
