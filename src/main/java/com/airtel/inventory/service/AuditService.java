package com.airtel.inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.airtel.inventory.domain.Asset;
import com.airtel.inventory.domain.AuditAction;
import com.airtel.inventory.domain.AuditLogEntry;
import com.airtel.inventory.repository.AuditLogRepository;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(Asset asset, AuditAction action, String actorName, String details) {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setAsset(asset);
        entry.setAssetTagSnapshot(asset != null ? asset.getAssetTag() : "N/A");
        entry.setAction(action);
        entry.setActorName(resolveActor(actorName));
        entry.setDetails(details);
        entry.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(entry);
    }

    public List<AuditLogEntry> getAuditEntries() {
        return auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public long countEntries() {
        return auditLogRepository.count();
    }

    private String resolveActor(String actorName) {
        if (StringUtils.hasText(actorName)) {
            return actorName.trim();
        }
        return System.getProperty("user.name", "system-operator");
    }
}
