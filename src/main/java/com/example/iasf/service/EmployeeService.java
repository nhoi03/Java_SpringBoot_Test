
package com.example.iasf.service;

import com.example.iasf.entity.Employee;
import com.example.iasf.repository.EmployeeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll(
                Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional(readOnly = true)
    public List<Employee> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        String value = keyword.trim();

        return employeeRepository
                .findByFullNameContainingIgnoreCaseOrEmployeeCodeContainingIgnoreCase(
                        value, value);
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found: " + id));
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteById(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Employee not found: " + id);
        }

        employeeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmployeeCode(String code) {
        return employeeRepository.existsByEmployeeCodeIgnoreCase(
                code.trim());
    }
}