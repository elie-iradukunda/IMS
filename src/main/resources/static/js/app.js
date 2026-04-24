document.addEventListener("DOMContentLoaded", () => {
    const departmentSelect = document.querySelector("[data-assignment-department]");
    const employeeSelect = document.querySelector("[data-assignment-employee]");

    if (!departmentSelect || !employeeSelect) {
        return;
    }

    const employeeOptions = Array.from(employeeSelect.querySelectorAll("option"));

    const syncEmployees = () => {
        const selectedDepartmentId = departmentSelect.value;

        employeeOptions.forEach((option, index) => {
            if (index === 0) {
                option.hidden = false;
                return;
            }

            const optionDepartmentId = option.getAttribute("data-department-id");
            const shouldShow = !selectedDepartmentId || optionDepartmentId === selectedDepartmentId;
            option.hidden = !shouldShow;
        });

        const selectedOption = employeeSelect.options[employeeSelect.selectedIndex];
        if (selectedOption && selectedOption.hidden) {
            employeeSelect.value = "";
        }
    };

    departmentSelect.addEventListener("change", syncEmployees);
    syncEmployees();
});
