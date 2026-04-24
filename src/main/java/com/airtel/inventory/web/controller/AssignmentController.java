package com.airtel.inventory.web.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.airtel.inventory.domain.AssetMovement;
import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.service.AccountService;
import com.airtel.inventory.service.AssetService;
import com.airtel.inventory.service.AssignmentService;
import com.airtel.inventory.service.ReferenceDataService;
import com.airtel.inventory.web.form.AssignmentWebForm;
import com.airtel.inventory.web.form.ReturnWebForm;

@Controller
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AssetService assetService;
    private final ReferenceDataService referenceDataService;
    private final AccountService accountService;

    public AssignmentController(AssignmentService assignmentService, AssetService assetService,
            ReferenceDataService referenceDataService, AccountService accountService) {
        this.assignmentService = assignmentService;
        this.assetService = assetService;
        this.referenceDataService = referenceDataService;
        this.accountService = accountService;
    }

    @GetMapping("/assignments")
    public String assignments(@RequestParam(required = false) Long returnId, Model model) {
        ReturnWebForm returnForm = new ReturnWebForm();
        AssetMovement selectedMovement = null;

        if (returnId != null) {
            selectedMovement = assignmentService.getOpenAssignments().stream()
                    .filter(movement -> movement.getId().equals(returnId))
                    .findFirst()
                    .orElse(null);
            if (selectedMovement != null) {
                returnForm.setMovementId(selectedMovement.getId());
                returnForm.setConditionAtReturn(selectedMovement.getConditionAtIssue());
            }
        }

        populateAssignmentsPage(model, new AssignmentWebForm(), returnForm, selectedMovement);
        return "assignments/index";
    }

    @PostMapping("/assignments/issue")
    public String issueAsset(@ModelAttribute("issueForm") AssignmentWebForm issueForm, BindingResult bindingResult,
            Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateAssignmentsPage(model, issueForm, new ReturnWebForm(), null);
            model.addAttribute("errorMessage", bindingErrorMessage(bindingResult));
            return "assignments/index";
        }
        try {
            assignmentService.issueAsset(issueForm.toAssignmentRequest(actorName(authentication)));
            redirectAttributes.addFlashAttribute("successMessage", "Asset issued successfully.");
            return "redirect:/assignments";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            populateAssignmentsPage(model, issueForm, new ReturnWebForm(), null);
            model.addAttribute("errorMessage", ex.getMessage());
            return "assignments/index";
        }
    }

    @PostMapping("/assignments/return")
    public String returnAsset(@ModelAttribute("returnForm") ReturnWebForm returnForm, BindingResult bindingResult,
            Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            AssetMovement selectedMovement = findMovement(returnForm.getMovementId());
            populateAssignmentsPage(model, new AssignmentWebForm(), returnForm, selectedMovement);
            model.addAttribute("errorMessage", bindingErrorMessage(bindingResult));
            return "assignments/index";
        }
        try {
            assignmentService.returnAsset(returnForm.toReturnRequest(actorName(authentication)));
            redirectAttributes.addFlashAttribute("successMessage", "Asset return completed successfully.");
            return "redirect:/assignments";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            AssetMovement selectedMovement = findMovement(returnForm.getMovementId());
            populateAssignmentsPage(model, new AssignmentWebForm(), returnForm, selectedMovement);
            model.addAttribute("errorMessage", ex.getMessage());
            return "assignments/index";
        }
    }

    private void populateAssignmentsPage(Model model, AssignmentWebForm issueForm, ReturnWebForm returnForm,
            AssetMovement selectedReturnMovement) {
        model.addAttribute("activePage", "assignments");
        model.addAttribute("pageTitle", "Assignments and Returns");
        model.addAttribute("pageSubtitle",
                "Issue equipment to staff, process returns, and follow complete movement history from one workspace.");
        model.addAttribute("issueForm", issueForm);
        model.addAttribute("returnForm", returnForm);
        model.addAttribute("selectedReturnMovement", selectedReturnMovement);
        model.addAttribute("availableAssets", assetService.getAssignableAssets());
        model.addAttribute("departments", referenceDataService.getDepartments());
        model.addAttribute("employees", referenceDataService.getEmployees());
        model.addAttribute("assetConditions", AssetCondition.values());
        model.addAttribute("openAssignments", assignmentService.getOpenAssignments());
        model.addAttribute("movementHistory", assignmentService.getMovementHistory());
    }

    private AssetMovement findMovement(Long movementId) {
        if (movementId == null) {
            return null;
        }
        List<AssetMovement> allMovements = assignmentService.getMovementHistory();
        return allMovements.stream()
                .filter(movement -> movement.getId().equals(movementId))
                .findFirst()
                .orElse(null);
    }

    private String actorName(Authentication authentication) {
        return accountService.displayNameForUsername(authentication.getName());
    }

    private String bindingErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .findFirst()
                .map(error -> "Please review the " + humanize(error.getField())
                        + " field. One of the submitted values is invalid.")
                .orElse("Please review the form values and try again.");
    }

    private String humanize(String fieldName) {
        return fieldName.replaceAll("([A-Z])", " $1").toLowerCase();
    }
}
