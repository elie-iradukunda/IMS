package com.airtel.inventory.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.airtel.inventory.domain.Department;
import com.airtel.inventory.domain.Employee;
import com.airtel.inventory.repository.DepartmentRepository;
import com.airtel.inventory.repository.EmployeeRepository;

@Service
public class ReferenceDataService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public ReferenceDataService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Department> getDepartments() {
        return departmentRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public List<Employee> getEmployees() {
        return employeeRepository.findAll(Sort.by(Sort.Direction.ASC, "fullName"));
    }

    public List<Employee> getEmployeesForDepartment(Long departmentId) {
        if (departmentId == null) {
            return getEmployees();
        }
        return employeeRepository.findByDepartmentIdOrderByFullName(departmentId);
    }

    public Department getDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Please choose a valid department."));
    }

    public Employee getEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Please choose a valid employee."));
    }
}
