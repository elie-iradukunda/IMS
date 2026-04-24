package com.airtel.inventory.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;
import com.airtel.inventory.repository.AssetRepository;
import com.airtel.inventory.service.AccountService;
import com.airtel.inventory.service.AssetService;
import com.airtel.inventory.service.AssignmentService;
import com.airtel.inventory.service.ReferenceDataService;
import com.airtel.inventory.service.dto.AssetForm;

import java.time.LocalDate;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:navigation-db;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.export-directory=./target/test-exports"
})
class NavigationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private ReferenceDataService referenceDataService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldRenderPublicPagesAndProtectedNavigation() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("24RP04887 - Elie Iradukunda")))
                .andExpect(content().string(Matchers.containsString("24RP00463 - Ufiteyesu Esther")));

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Sign In")))
                .andExpect(content().string(Matchers.containsString("_csrf")));

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Create Account")))
                .andExpect(content().string(Matchers.containsString("_csrf")));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "Navigation Test User")
                        .param("username", "navtester")
                        .param("email", "navtester@example.com")
                        .param("password", "Password123")
                        .param("confirmPassword", "Password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "navtester")
                        .param("password", "Password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        var adminUser = accountService.findByUsername("24RP04887");

        mockMvc.perform(get("/dashboard").with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Inventory Dashboard")));

        mockMvc.perform(get("/assets").with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Asset Catalog")));

        mockMvc.perform(get("/assignments").with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Active Assignments")));

        mockMvc.perform(get("/reports").with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Asset Report")));

        mockMvc.perform(get("/audit").with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Audit Entries")));

        mockMvc.perform(post("/logout").with(user(adminUser)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void shouldSupportAssetCreateUpdateDeleteAndRecoverFromInvalidPost() throws Exception {
        var adminUser = accountService.findByUsername("24RP04887");
        var department = referenceDataService.getDepartments().get(0);
        LocalDate purchaseDate = LocalDate.now().minusDays(14);
        LocalDate warrantyExpiry = LocalDate.now().plusYears(2);

        mockMvc.perform(post("/assets/save")
                        .with(user(adminUser))
                        .with(csrf())
                        .param("assetTag", "WEB-CRUD-001")
                        .param("serialNumber", "WEB-CRUD-SN-001")
                        .param("assetType", "Laptop")
                        .param("brand", "Lenovo")
                        .param("model", "ThinkPad X1")
                        .param("operatingSystem", "Windows 11 Pro")
                        .param("processor", "Intel Core Ultra 7")
                        .param("ramGb", "16")
                        .param("storageGb", "512")
                        .param("purchaseDate", purchaseDate.toString())
                        .param("warrantyExpiry", warrantyExpiry.toString())
                        .param("homeDepartmentId", department.getId().toString())
                        .param("condition", "Good")
                        .param("status", "Available")
                        .param("notes", "Created from web form test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assets"));

        var createdAsset = assetRepository.findByAssetTagIgnoreCase("WEB-CRUD-001").orElseThrow();
        assertThat(createdAsset.getAssetType()).isEqualTo(AssetType.LAPTOP);
        assertThat(createdAsset.getCondition()).isEqualTo(AssetCondition.GOOD);

        mockMvc.perform(post("/assets/save")
                        .with(user(adminUser))
                        .with(csrf())
                        .param("id", createdAsset.getId().toString())
                        .param("assetTag", "WEB-CRUD-001")
                        .param("serialNumber", "WEB-CRUD-SN-001")
                        .param("assetType", "LAPTOP")
                        .param("brand", "Lenovo Updated")
                        .param("model", "ThinkPad X1 Carbon")
                        .param("operatingSystem", "Windows 11 Pro")
                        .param("processor", "Intel Core Ultra 7")
                        .param("ramGb", "32")
                        .param("storageGb", "1024")
                        .param("purchaseDate", purchaseDate.toString())
                        .param("warrantyExpiry", warrantyExpiry.toString())
                        .param("homeDepartmentId", department.getId().toString())
                        .param("condition", "EXCELLENT")
                        .param("status", "AVAILABLE")
                        .param("notes", "Updated from web form test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assets"));

        var updatedAsset = assetRepository.findById(createdAsset.getId()).orElseThrow();
        assertThat(updatedAsset.getBrand()).isEqualTo("Lenovo Updated");
        assertThat(updatedAsset.getCondition()).isEqualTo(AssetCondition.EXCELLENT);

        mockMvc.perform(post("/assets/save")
                        .with(user(adminUser))
                        .with(csrf())
                        .param("assetTag", "WEB-CRUD-002")
                        .param("serialNumber", "WEB-CRUD-SN-002")
                        .param("assetType", "Unknown Type")
                        .param("brand", "HP")
                        .param("model", "EliteBook")
                        .param("purchaseDate", purchaseDate.toString())
                        .param("homeDepartmentId", department.getId().toString())
                        .param("condition", "GOOD")
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Please review the asset type field")));

        mockMvc.perform(post("/assets/{assetId}/delete", createdAsset.getId())
                        .with(user(adminUser))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assets"));

        assertThat(assetRepository.findById(createdAsset.getId())).isEmpty();
    }

    @Test
    void shouldSupportIssueAndReturnFormsWithoutBadRequest() throws Exception {
        var adminUser = accountService.findByUsername("24RP04887");
        var department = referenceDataService.getDepartments().get(0);
        var employee = referenceDataService.getEmployeesForDepartment(department.getId()).get(0);

        var asset = assetService.createAsset(new AssetForm(
                "WEB-ISSUE-001",
                "WEB-ISSUE-SN-001",
                AssetType.LAPTOP,
                "Dell",
                "Latitude 7450",
                "Windows 11 Pro",
                "Intel Core Ultra 5",
                16,
                512,
                LocalDate.now().minusDays(3),
                LocalDate.now().plusYears(3),
                department.getId(),
                AssetCondition.GOOD,
                AssetStatus.AVAILABLE,
                "Web assignment flow asset"), adminUser.getFullName());

        mockMvc.perform(post("/assignments/issue")
                        .with(user(adminUser))
                        .with(csrf())
                        .param("assetId", asset.getId().toString())
                        .param("departmentId", department.getId().toString())
                        .param("employeeId", employee.getId().toString())
                        .param("issueDate", LocalDate.now().toString())
                        .param("conditionAtIssue", "Good")
                        .param("notes", "Issued from assignment form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assignments"));

        var issuedAsset = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(issuedAsset.getStatus()).isEqualTo(AssetStatus.ASSIGNED);

        var movement = assignmentService.getOpenAssignments().stream()
                .filter(item -> item.getAsset().getId().equals(asset.getId()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/assignments/return")
                        .with(user(adminUser))
                        .with(csrf())
                        .param("movementId", movement.getId().toString())
                        .param("returnDate", LocalDate.now().plusDays(1).toString())
                        .param("conditionAtReturn", "GOOD")
                        .param("notes", "Returned from assignment form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assignments"));

        var returnedAsset = assetRepository.findById(asset.getId()).orElseThrow();
        assertThat(returnedAsset.getStatus()).isEqualTo(AssetStatus.AVAILABLE);
    }
}
