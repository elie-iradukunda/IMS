package com.airtel.inventory.web.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;
import com.airtel.inventory.service.AssignmentService;
import com.airtel.inventory.service.AuditService;
import com.airtel.inventory.service.DashboardService;
import com.airtel.inventory.service.dto.DashboardSummary;
import com.airtel.inventory.web.view.MetricBar;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final AssignmentService assignmentService;
    private final AuditService auditService;

    public DashboardController(DashboardService dashboardService, AssignmentService assignmentService,
            AuditService auditService) {
        this.dashboardService = dashboardService;
        this.assignmentService = assignmentService;
        this.auditService = auditService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardSummary summary = dashboardService.getSummary();

        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Inventory Dashboard");
        model.addAttribute("pageSubtitle",
                "Monitor current device stock, assignments, maintenance items, and recent inventory activity.");
        model.addAttribute("summary", summary);
        model.addAttribute("statusBars", buildStatusBars(summary.assetsByStatus()));
        model.addAttribute("typeBars", buildTypeBars(summary.assetsByType()));
        model.addAttribute("recentMovements", assignmentService.getRecentActivity());
        model.addAttribute("openAssignments", assignmentService.getOpenAssignments().stream().limit(8).toList());
        model.addAttribute("recentAuditEntries", auditService.getAuditEntries().stream().limit(8).toList());
        return "dashboard";
    }

    private List<MetricBar> buildStatusBars(Map<AssetStatus, Long> values) {
        long max = Arrays.stream(AssetStatus.values())
                .mapToLong(status -> values.getOrDefault(status, 0L))
                .max()
                .orElse(1L);

        return Arrays.stream(AssetStatus.values())
                .map(status -> new MetricBar(
                        status.toString(),
                        values.getOrDefault(status, 0L),
                        percentage(values.getOrDefault(status, 0L), max),
                        switch (status) {
                            case AVAILABLE -> "tone-green";
                            case ASSIGNED -> "tone-blue";
                            case IN_MAINTENANCE -> "tone-amber";
                            case RETIRED -> "tone-slate";
                        }))
                .toList();
    }

    private List<MetricBar> buildTypeBars(Map<AssetType, Long> values) {
        long max = Arrays.stream(AssetType.values())
                .mapToLong(type -> values.getOrDefault(type, 0L))
                .max()
                .orElse(1L);

        return Arrays.stream(AssetType.values())
                .map(type -> new MetricBar(
                        type.toString(),
                        values.getOrDefault(type, 0L),
                        percentage(values.getOrDefault(type, 0L), max),
                        "tone-orange"))
                .toList();
    }

    private int percentage(long value, long max) {
        if (max <= 0) {
            return 0;
        }
        return (int) Math.max(8, Math.round((double) value * 100 / max));
    }
}
