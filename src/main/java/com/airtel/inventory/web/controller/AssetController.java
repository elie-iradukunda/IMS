package com.airtel.inventory.web.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.airtel.inventory.domain.Asset;
import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;
import com.airtel.inventory.service.AccountService;
import com.airtel.inventory.service.AssetService;
import com.airtel.inventory.service.ReferenceDataService;
import com.airtel.inventory.web.form.AssetWebForm;

@Controller
public class AssetController {

    private final AssetService assetService;
    private final ReferenceDataService referenceDataService;
    private final AccountService accountService;

    public AssetController(AssetService assetService, ReferenceDataService referenceDataService,
            AccountService accountService) {
        this.assetService = assetService;
        this.referenceDataService = referenceDataService;
        this.accountService = accountService;
    }

    @GetMapping("/assets")
    public String assets(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) AssetType type,
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(required = false) AssetCondition condition,
            @RequestParam(required = false) Long edit,
            Model model) {
        AssetWebForm assetForm = edit != null ? AssetWebForm.fromAsset(assetService.getAsset(edit)) : AssetWebForm.empty();
        populateAssetsPage(model, assetForm, q, type, status, condition);
        return "assets/index";
    }

    @PostMapping("/assets/save")
    public String saveAsset(@ModelAttribute("assetForm") AssetWebForm assetForm, BindingResult bindingResult,
            Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateAssetsPage(model, assetForm, null, null, null, null);
            model.addAttribute("errorMessage", bindingErrorMessage(bindingResult));
            return "assets/index";
        }
        try {
            if (assetForm.getId() == null) {
                assetService.createAsset(assetForm.toAssetForm(), actorName(authentication));
                redirectAttributes.addFlashAttribute("successMessage", "Asset registered successfully.");
            } else {
                assetService.updateAsset(assetForm.getId(), assetForm.toAssetForm(), actorName(authentication));
                redirectAttributes.addFlashAttribute("successMessage", "Asset updated successfully.");
            }
            return "redirect:/assets";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            populateAssetsPage(model, assetForm, null, null, null, null);
            model.addAttribute("errorMessage", ex.getMessage());
            return "assets/index";
        }
    }

    @PostMapping("/assets/{assetId}/delete")
    public String deleteAsset(@PathVariable Long assetId, RedirectAttributes redirectAttributes) {
        try {
            assetService.deleteAsset(assetId);
            redirectAttributes.addFlashAttribute("successMessage", "Asset deleted successfully.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/assets";
    }

    @PostMapping("/assets/{assetId}/retire")
    public String retireAsset(@PathVariable Long assetId, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            assetService.retireAsset(assetId, actorName(authentication));
            redirectAttributes.addFlashAttribute("successMessage", "Asset retired successfully.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/assets";
    }

    private void populateAssetsPage(Model model, AssetWebForm assetForm, String q, AssetType type, AssetStatus status,
            AssetCondition condition) {
        model.addAttribute("activePage", "assets");
        model.addAttribute("pageTitle", "Asset Registry");
        model.addAttribute("pageSubtitle",
                "Create, update, search, and retire inventory records with full ownership and condition details.");
        model.addAttribute("assetForm", assetForm);
        model.addAttribute("assets", filterAssets(q, type, status, condition));
        model.addAttribute("departments", referenceDataService.getDepartments());
        model.addAttribute("assetTypes", AssetType.values());
        model.addAttribute("assetStatuses", AssetStatus.values());
        model.addAttribute("assetConditions", AssetCondition.values());
        model.addAttribute("q", q);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCondition", condition);
    }

    private List<Asset> filterAssets(String q, AssetType type, AssetStatus status, AssetCondition condition) {
        String search = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        return assetService.getAllAssets().stream()
                .filter(asset -> type == null || asset.getAssetType() == type)
                .filter(asset -> status == null || asset.getStatus() == status)
                .filter(asset -> condition == null || asset.getCondition() == condition)
                .filter(asset -> search.isBlank()
                        || contains(asset.getAssetTag(), search)
                        || contains(asset.getSerialNumber(), search)
                        || contains(asset.getBrand(), search)
                        || contains(asset.getModel(), search)
                        || contains(asset.getHomeDepartment() != null ? asset.getHomeDepartment().getName() : null,
                                search)
                        || contains(asset.getAssignedEmployee() != null ? asset.getAssignedEmployee().getFullName() : null,
                                search))
                .toList();
    }

    private String actorName(Authentication authentication) {
        return accountService.displayNameForUsername(authentication.getName());
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private String bindingErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .findFirst()
                .map(error -> "Please review the " + humanize(error.getField())
                        + " field. One of the values could not be understood.")
                .orElse("Please review the form values and try again.");
    }

    private String humanize(String fieldName) {
        return fieldName.replaceAll("([A-Z])", " $1").toLowerCase(Locale.ROOT);
    }
}
