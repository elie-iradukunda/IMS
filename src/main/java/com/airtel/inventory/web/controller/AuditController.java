package com.airtel.inventory.web.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.airtel.inventory.domain.AuditAction;
import com.airtel.inventory.domain.AuditLogEntry;
import com.airtel.inventory.service.AuditService;

@Controller
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/audit")
    public String audit(@RequestParam(required = false) String q, @RequestParam(required = false) AuditAction action,
            Model model) {
        model.addAttribute("activePage", "audit");
        model.addAttribute("pageTitle", "Audit Trail");
        model.addAttribute("pageSubtitle",
                "Review complete asset accountability history including registrations, issues, returns, updates, and retirement actions.");
        model.addAttribute("auditEntries", filterAudit(q, action));
        model.addAttribute("auditActions", AuditAction.values());
        model.addAttribute("q", q);
        model.addAttribute("selectedAction", action);
        return "audit/index";
    }

    private List<AuditLogEntry> filterAudit(String q, AuditAction action) {
        String search = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        return auditService.getAuditEntries().stream()
                .filter(entry -> action == null || entry.getAction() == action)
                .filter(entry -> search.isBlank()
                        || contains(entry.getAssetTagSnapshot(), search)
                        || contains(entry.getActorName(), search)
                        || contains(entry.getDetails(), search)
                        || contains(entry.getAction().toString(), search))
                .toList();
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }
}
