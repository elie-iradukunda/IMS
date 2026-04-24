package com.airtel.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.airtel.inventory.domain.Asset;
import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.domain.AssetMovement;
import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;
import com.airtel.inventory.domain.Department;
import com.airtel.inventory.domain.Employee;
import com.airtel.inventory.service.dto.AssetForm;
import com.airtel.inventory.service.dto.AssignmentRequest;
import com.airtel.inventory.service.dto.ReportFilter;
import com.airtel.inventory.service.dto.ReturnRequest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:workflow-db;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.export-directory=./target/test-exports"
})
class InventoryWorkflowIntegrationTest {

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private ReportService reportService;

    @Test
    void shouldCreateIssueReturnAndExportAssetReport() throws Exception {
        Department department = referenceDataService.getDepartments().get(0);
        List<Employee> employees = referenceDataService.getEmployeesForDepartment(department.getId());
        Employee employee = employees.get(0);

        Asset asset = assetService.createAsset(new AssetForm(
                "TEST-LAP-100",
                "TEST-SERIAL-100",
                AssetType.LAPTOP,
                "Dell",
                "Latitude 7450",
                "Windows 11 Pro",
                "Intel Core Ultra 7",
                16,
                512,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusYears(3),
                department.getId(),
                AssetCondition.GOOD,
                AssetStatus.AVAILABLE,
                "Integration workflow asset"),
                "integration-test");

        AssetMovement issuedMovement = assignmentService.issueAsset(new AssignmentRequest(
                asset.getId(),
                department.getId(),
                employee.getId(),
                "integration-test",
                LocalDate.now(),
                AssetCondition.GOOD,
                "Issued for integration verification"));

        Asset assignedAsset = assetService.getAsset(asset.getId());
        assertThat(assignedAsset.getStatus()).isEqualTo(AssetStatus.ASSIGNED);
        assertThat(assignedAsset.getAssignedEmployee().getId()).isEqualTo(employee.getId());
        assertThat(issuedMovement.getReturnedAt()).isNull();

        assignmentService.returnAsset(new ReturnRequest(
                issuedMovement.getId(),
                "integration-test",
                LocalDate.now(),
                AssetCondition.GOOD,
                "Returned after verification"));

        Asset returnedAsset = assetService.getAsset(asset.getId());
        assertThat(returnedAsset.getStatus()).isEqualTo(AssetStatus.AVAILABLE);
        assertThat(returnedAsset.getAssignedEmployee()).isNull();

        List<Asset> reportAssets = reportService.getFilteredAssets(
                new ReportFilter(null, null, department.getId(), AssetType.LAPTOP, null));
        assertThat(reportAssets)
                .extracting(Asset::getAssetTag)
                .contains("TEST-LAP-100");

        Path exportPath = reportService.exportAssetsReport(
                new ReportFilter(null, null, department.getId(), AssetType.LAPTOP, null));
        assertThat(Files.exists(exportPath)).isTrue();
    }
}
