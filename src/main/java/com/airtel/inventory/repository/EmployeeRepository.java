package com.airtel.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airtel.inventory.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartmentIdOrderByFullName(Long departmentId);
}
