package com.airtel.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airtel.inventory.domain.AuditLogEntry;

public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Long> {

    void deleteByAssetId(Long assetId);

    List<AuditLogEntry> findTop200ByOrderByCreatedAtDesc();
}
