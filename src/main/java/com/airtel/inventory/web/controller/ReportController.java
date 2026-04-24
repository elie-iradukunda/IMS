package com.airtel.inventory.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;
import com.airtel.inventory.service.ReferenceDataService;
import com.airtel.inventory.service.ReportService;
import com.airtel.inventory.web.form.ReportFilterForm;

@Controller
public class ReportController {

    private final ReportService reportService;
    private final ReferenceDataService referenceDataService;

    public ReportController(ReportService reportService, ReferenceDataService referenceDataService) {
        this.reportService = reportService;
        this.referenceDataService = referenceDataService;
    }

    @GetMapping("/reports")
    public String reports(@ModelAttribute("reportFilter") ReportFilterForm reportFilter, Model model) {
        populateReportsPage(model, reportFilter);
        return "reports/index";
    }

    @GetMapping("/reports/assets/export")
    public ResponseEntity<ByteArrayResource> exportAssets(@ModelAttribute ReportFilterForm reportFilter) throws IOException {
        Path exportFile = reportService.exportAssetsReport(reportFilter.toReportFilter());
        return buildDownload(exportFile);
    }

    @GetMapping("/reports/movements/export")
    public ResponseEntity<ByteArrayResource> exportMovements(@ModelAttribute ReportFilterForm reportFilter)
            throws IOException {
        Path exportFile = reportService.exportMovementsReport(reportFilter.toReportFilter());
        return buildDownload(exportFile);
    }

    private void populateReportsPage(Model model, ReportFilterForm reportFilter) {
        model.addAttribute("activePage", "reports");
        model.addAttribute("pageTitle", "Reports and Exports");
        model.addAttribute("pageSubtitle",
                "Filter assets and movement history by date, department, type, and lifecycle status, then export CSV files.");
        model.addAttribute("reportFilter", reportFilter);
        model.addAttribute("departments", referenceDataService.getDepartments());
        model.addAttribute("assetTypes", AssetType.values());
        model.addAttribute("assetStatuses", AssetStatus.values());
        model.addAttribute("reportAssets", reportService.getFilteredAssets(reportFilter.toReportFilter()));
        model.addAttribute("reportMovements", reportService.getFilteredMovements(reportFilter.toReportFilter()));
    }

    private ResponseEntity<ByteArrayResource> buildDownload(Path exportFile) throws IOException {
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(exportFile));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + exportFile.getFileName() + "\"")
                .body(resource);
    }
}
