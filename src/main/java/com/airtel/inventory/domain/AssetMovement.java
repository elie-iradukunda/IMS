package com.airtel.inventory.domain;

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

@Entity
@Table(name = "asset_movements")
public class AssetMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private MovementType movementType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_department_id")
    private Department fromDepartment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_department_id")
    private Department toDepartment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "issued_by", nullable = false, length = 120)
    private String issuedBy;

    @Column(name = "returned_by", length = 120)
    private String returnedBy;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_at_issue", nullable = false, length = 40)
    private AssetCondition conditionAtIssue;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_at_return", length = 40)
    private AssetCondition conditionAtReturn;

    @Column(name = "issue_notes", length = 1500)
    private String issueNotes;

    @Column(name = "return_notes", length = 1500)
    private String returnNotes;

    public Long getId() {
        return id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Department getFromDepartment() {
        return fromDepartment;
    }

    public void setFromDepartment(Department fromDepartment) {
        this.fromDepartment = fromDepartment;
    }

    public Department getToDepartment() {
        return toDepartment;
    }

    public void setToDepartment(Department toDepartment) {
        this.toDepartment = toDepartment;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getReturnedBy() {
        return returnedBy;
    }

    public void setReturnedBy(String returnedBy) {
        this.returnedBy = returnedBy;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }

    public AssetCondition getConditionAtIssue() {
        return conditionAtIssue;
    }

    public void setConditionAtIssue(AssetCondition conditionAtIssue) {
        this.conditionAtIssue = conditionAtIssue;
    }

    public AssetCondition getConditionAtReturn() {
        return conditionAtReturn;
    }

    public void setConditionAtReturn(AssetCondition conditionAtReturn) {
        this.conditionAtReturn = conditionAtReturn;
    }

    public String getIssueNotes() {
        return issueNotes;
    }

    public void setIssueNotes(String issueNotes) {
        this.issueNotes = issueNotes;
    }

    public String getReturnNotes() {
        return returnNotes;
    }

    public void setReturnNotes(String returnNotes) {
        this.returnNotes = returnNotes;
    }

    public String getLifecycleStatus() {
        return returnedAt == null ? "Open" : "Closed";
    }
}
