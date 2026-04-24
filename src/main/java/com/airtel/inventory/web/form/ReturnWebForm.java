package com.airtel.inventory.web.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.service.dto.ReturnRequest;

public class ReturnWebForm {

    private Long movementId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate returnDate = LocalDate.now();
    private AssetCondition conditionAtReturn = AssetCondition.GOOD;
    private String notes;

    public ReturnRequest toReturnRequest(String actorName) {
        return new ReturnRequest(movementId, actorName, returnDate, conditionAtReturn, notes);
    }

    public Long getMovementId() {
        return movementId;
    }

    public void setMovementId(Long movementId) {
        this.movementId = movementId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public AssetCondition getConditionAtReturn() {
        return conditionAtReturn;
    }

    public void setConditionAtReturn(AssetCondition conditionAtReturn) {
        this.conditionAtReturn = conditionAtReturn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
