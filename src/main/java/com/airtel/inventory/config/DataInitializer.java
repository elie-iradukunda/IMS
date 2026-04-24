package com.airtel.inventory.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.airtel.inventory.domain.Asset;
import com.airtel.inventory.domain.AssetCondition;
import com.airtel.inventory.domain.AssetMovement;
import com.airtel.inventory.domain.AssetStatus;
import com.airtel.inventory.domain.AssetType;
import com.airtel.inventory.domain.AuditAction;
import com.airtel.inventory.domain.Department;
import com.airtel.inventory.domain.Employee;
import com.airtel.inventory.domain.MovementType;
import com.airtel.inventory.domain.AppUser;
import com.airtel.inventory.repository.AssetMovementRepository;
import com.airtel.inventory.repository.AssetRepository;
import com.airtel.inventory.repository.DepartmentRepository;
import com.airtel.inventory.repository.EmployeeRepository;
import com.airtel.inventory.service.AccountService;
import com.airtel.inventory.service.AuditService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AssetRepository assetRepository;
    private final AssetMovementRepository assetMovementRepository;
    private final AuditService auditService;
    private final AccountService accountService;
    private final String defaultAdminFullName;
    private final String defaultAdminUsername;
    private final String defaultAdminEmail;
    private final String defaultAdminPassword;

    public DataInitializer(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository,
            AssetRepository assetRepository, AssetMovementRepository assetMovementRepository, AuditService auditService,
            AccountService accountService,
            @Value("${app.default-admin-full-name:SysAdmin}") String defaultAdminFullName,
            @Value("${app.default-admin-username:24RP04887}") String defaultAdminUsername,
            @Value("${app.default-admin-email:24rp04887@ims.local}") String defaultAdminEmail,
            @Value("${app.default-admin-password:24RP00463}") String defaultAdminPassword) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.assetRepository = assetRepository;
        this.assetMovementRepository = assetMovementRepository;
        this.auditService = auditService;
        this.accountService = accountService;
        this.defaultAdminFullName = defaultAdminFullName;
        this.defaultAdminUsername = defaultAdminUsername;
        this.defaultAdminEmail = defaultAdminEmail;
        this.defaultAdminPassword = defaultAdminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        AppUser adminUser = accountService.createAdminIfMissing(
                defaultAdminFullName,
                defaultAdminUsername,
                defaultAdminEmail,
                defaultAdminPassword);

        if (assetRepository.count() > 0) {
            return;
        }

        Map<String, Department> departments = departmentRepository.saveAll(List.of(
                new Department("IT-OPS", "IT Operations", "Kigali HQ", "Jean Irakoze"),
                new Department("FIN", "Finance", "Kigali HQ", "Aline Uwase"),
                new Department("HR", "Human Resources", "Regional Office", "Diane Mukansanga"),
                new Department("CS", "Customer Experience", "Call Centre", "Eric Nshimiyimana"))).stream()
                .collect(Collectors.toMap(Department::getCode, Function.identity()));

        Map<String, Employee> employees = employeeRepository.saveAll(List.of(
                new Employee("EMP-1001", "Clarisse Uwimana", "clarisse.uwimana@airtel.test", "+250700100001",
                        "IT Support Officer", departments.get("IT-OPS")),
                new Employee("EMP-1002", "David Ndayambaje", "david.ndayambaje@airtel.test", "+250700100002",
                        "Finance Analyst", departments.get("FIN")),
                new Employee("EMP-1003", "Alice Uwamahoro", "alice.uwamahoro@airtel.test", "+250700100003",
                        "HR Business Partner", departments.get("HR")),
                new Employee("EMP-1004", "Kevin Murenzi", "kevin.murenzi@airtel.test", "+250700100004",
                        "Customer Care Lead", departments.get("CS")))).stream()
                .collect(Collectors.toMap(Employee::getEmployeeCode, Function.identity()));

        LocalDateTime now = LocalDateTime.now();

        Asset laptopOne = buildAsset("ATL-LAP-001", "SN-LAP-001", AssetType.LAPTOP, "Dell", "Latitude 5440",
                "Windows 11 Pro", "Intel Core i7", 16, 512, LocalDate.now().minusMonths(8),
                LocalDate.now().plusMonths(28), departments.get("IT-OPS"), AssetStatus.ASSIGNED, AssetCondition.GOOD,
                "Primary support laptop for field operations.", now.minusDays(14));

        Asset laptopTwo = buildAsset("ATL-LAP-002", "SN-LAP-002", AssetType.LAPTOP, "HP", "EliteBook 840",
                "Windows 11 Pro", "Intel Core i5", 16, 256, LocalDate.now().minusMonths(4),
                LocalDate.now().plusMonths(32), departments.get("FIN"), AssetStatus.AVAILABLE, AssetCondition.EXCELLENT,
                "Prepared for new staff onboarding.", now.minusDays(2));

        Asset desktopOne = buildAsset("ATL-DESK-001", "SN-DESK-001", AssetType.DESKTOP, "Lenovo", "ThinkCentre M70s",
                "Windows 10 Pro", "Intel Core i5", 8, 512, LocalDate.now().minusYears(1),
                LocalDate.now().plusMonths(12), departments.get("CS"), AssetStatus.AVAILABLE, AssetCondition.GOOD,
                "Call centre workstation.", now.minusDays(6));

        Asset phoneOne = buildAsset("ATL-MOB-001", "SN-MOB-001", AssetType.MOBILE_PHONE, "Samsung", "Galaxy A55",
                "Android 14", "Exynos", 8, 128, LocalDate.now().minusMonths(6),
                LocalDate.now().plusMonths(24), departments.get("HR"), AssetStatus.IN_MAINTENANCE, AssetCondition.DAMAGED,
                "Returned with cracked screen and pending repair.", now.minusDays(1));

        Asset phoneTwo = buildAsset("ATL-MOB-002", "SN-MOB-002", AssetType.MOBILE_PHONE, "Apple", "iPhone 15",
                "iOS 18", "A16", 6, 256, LocalDate.now().minusMonths(3),
                LocalDate.now().plusMonths(33), departments.get("FIN"), AssetStatus.AVAILABLE, AssetCondition.GOOD,
                "Finance mobile backup device.", now.minusDays(5));

        Asset desktopTwo = buildAsset("ATL-DESK-002", "SN-DESK-002", AssetType.DESKTOP, "Dell", "OptiPlex 7010",
                "Windows 11 Pro", "Intel Core i7", 16, 512, LocalDate.now().minusMonths(10),
                LocalDate.now().plusMonths(20), departments.get("IT-OPS"), AssetStatus.AVAILABLE, AssetCondition.GOOD,
                "Engineering staging machine.", now.minusDays(7));

        Asset tabletOne = buildAsset("ATL-TAB-001", "SN-TAB-001", AssetType.TABLET, "Samsung", "Galaxy Tab S9",
                "Android 14", "Snapdragon", 8, 256, LocalDate.now().minusMonths(5),
                LocalDate.now().plusMonths(25), departments.get("CS"), AssetStatus.AVAILABLE, AssetCondition.GOOD,
                "Customer feedback kiosk tablet.", now.minusDays(4));

        assetRepository.saveAll(List.of(laptopOne, laptopTwo, desktopOne, phoneOne, phoneTwo, desktopTwo, tabletOne));

        AssetMovement activeIssue = new AssetMovement();
        activeIssue.setAsset(laptopOne);
        activeIssue.setMovementType(MovementType.ISSUE);
        activeIssue.setFromDepartment(departments.get("IT-OPS"));
        activeIssue.setToDepartment(departments.get("IT-OPS"));
        activeIssue.setEmployee(employees.get("EMP-1001"));
        activeIssue.setIssuedBy(adminUser.getFullName());
        activeIssue.setIssuedAt(now.minusDays(14));
        activeIssue.setConditionAtIssue(AssetCondition.GOOD);
        activeIssue.setIssueNotes("Issued to IT support desk for field support.");

        laptopOne.setAssignedDepartment(departments.get("IT-OPS"));
        laptopOne.setAssignedEmployee(employees.get("EMP-1001"));

        AssetMovement closedMovement = new AssetMovement();
        closedMovement.setAsset(phoneTwo);
        closedMovement.setMovementType(MovementType.RETURN);
        closedMovement.setFromDepartment(departments.get("FIN"));
        closedMovement.setToDepartment(departments.get("FIN"));
        closedMovement.setEmployee(employees.get("EMP-1002"));
        closedMovement.setIssuedBy(adminUser.getFullName());
        closedMovement.setIssuedAt(now.minusDays(16));
        closedMovement.setReturnedBy(adminUser.getFullName());
        closedMovement.setReturnedAt(now.minusDays(5));
        closedMovement.setConditionAtIssue(AssetCondition.GOOD);
        closedMovement.setConditionAtReturn(AssetCondition.GOOD);
        closedMovement.setIssueNotes("Temporary mobile assignment for regional travel.");
        closedMovement.setReturnNotes("Device checked in and restocked.");

        AssetMovement repairReturn = new AssetMovement();
        repairReturn.setAsset(phoneOne);
        repairReturn.setMovementType(MovementType.RETURN);
        repairReturn.setFromDepartment(departments.get("HR"));
        repairReturn.setToDepartment(departments.get("HR"));
        repairReturn.setEmployee(employees.get("EMP-1003"));
        repairReturn.setIssuedBy(adminUser.getFullName());
        repairReturn.setIssuedAt(now.minusDays(20));
        repairReturn.setReturnedBy("Workshop Technician");
        repairReturn.setReturnedAt(now.minusDays(1));
        repairReturn.setConditionAtIssue(AssetCondition.GOOD);
        repairReturn.setConditionAtReturn(AssetCondition.DAMAGED);
        repairReturn.setIssueNotes("Issued for field recruitment activities.");
        repairReturn.setReturnNotes("Returned damaged and moved to maintenance queue.");

        assetMovementRepository.saveAll(List.of(activeIssue, closedMovement, repairReturn));

        assetRepository.save(laptopOne);

        assetRepository.findAll().forEach(asset -> auditService.log(asset, AuditAction.ASSET_REGISTERED,
                adminUser.getFullName(), "Seeded asset record for " + asset.getAssetTag() + "."));
        auditService.log(laptopOne, AuditAction.ASSET_ISSUED, adminUser.getFullName(),
                "Seeded active assignment for " + employees.get("EMP-1001").getFullName() + ".");
        auditService.log(phoneTwo, AuditAction.ASSET_RETURNED, adminUser.getFullName(),
                "Seeded completed return record for finance mobile pool.");
        auditService.log(phoneOne, AuditAction.ASSET_RETURNED, adminUser.getFullName(),
                "Seeded damaged return record for maintenance follow-up.");
    }

    private Asset buildAsset(String assetTag, String serialNumber, AssetType assetType, String brand, String model,
            String operatingSystem, String processor, Integer ramGb, Integer storageGb, LocalDate purchaseDate,
            LocalDate warrantyExpiry, Department homeDepartment, AssetStatus status, AssetCondition condition,
            String notes, LocalDateTime lastMovementAt) {
        Asset asset = new Asset();
        asset.setAssetTag(assetTag);
        asset.setSerialNumber(serialNumber);
        asset.setAssetType(assetType);
        asset.setBrand(brand);
        asset.setModel(model);
        asset.setOperatingSystem(operatingSystem);
        asset.setProcessor(processor);
        asset.setRamGb(ramGb);
        asset.setStorageGb(storageGb);
        asset.setPurchaseDate(purchaseDate);
        asset.setWarrantyExpiry(warrantyExpiry);
        asset.setHomeDepartment(homeDepartment);
        asset.setStatus(status);
        asset.setCondition(condition);
        asset.setNotes(notes);
        asset.setLastMovementAt(lastMovementAt);
        return asset;
    }
}
