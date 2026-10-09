
package com.example.iasf.repository;

import com.example.iasf.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    List<Employee> findByFullNameContainingIgnoreCaseOrEmployeeCodeContainingIgnoreCase(
            String fullName, String employeeCode);

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

    Optional<Employee> findByEmployeeCodeIgnoreCase(String employeeCode);
}