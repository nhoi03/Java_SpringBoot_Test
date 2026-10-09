
package com.example.iasf.controller;

import com.example.iasf.entity.Employee;
import com.example.iasf.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false, defaultValue = "")
            String keyword,
            Model model) {

        model.addAttribute("employees",
                employeeService.search(keyword));
        model.addAttribute("keyword", keyword);

        return "employees/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("pageTitle", "Add Employee");
        model.addAttribute("formAction", "/employees");
        return "employees/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!result.hasErrors()
                && employeeService.existsByEmployeeCode(
                employee.getEmployeeCode())) {
            result.rejectValue("employeeCode",
                    "duplicate", "Employee code already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Add Employee");
            model.addAttribute("formAction", "/employees");
            return "employees/form";
        }

        try {
            employeeService.save(employee);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Employee created successfully");
            return "redirect:/employees";
        } catch (DataIntegrityViolationException ex) {
            result.rejectValue("employeeCode",
                    "duplicate", "Employee code already exists");
            model.addAttribute("pageTitle", "Add Employee");
            model.addAttribute("formAction", "/employees");
            return "employees/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("employee",
                employeeService.findById(id));
        return "employees/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("employee",
                employeeService.findById(id));
        model.addAttribute("pageTitle", "Edit Employee");
        model.addAttribute("formAction",
                "/employees/" + id + "/edit");

        return "employees/form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        Employee existing = employeeService.findById(id);

        employee.setId(id);

        if (!result.hasErrors()) {
            boolean duplicate =
                    employeeService.existsByEmployeeCode(
                            employee.getEmployeeCode());

            boolean changedCode =
                    !existing.getEmployeeCode().equalsIgnoreCase(
                            employee.getEmployeeCode().trim());

            if (duplicate && changedCode) {
                result.rejectValue("employeeCode",
                        "duplicate", "Employee code already exists");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Employee");
            model.addAttribute("formAction",
                    "/employees/" + id + "/edit");
            return "employees/form";
        }

        try {
            employeeService.save(employee);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Employee updated successfully");
            return "redirect:/employees";
        } catch (DataIntegrityViolationException ex) {
            result.rejectValue("employeeCode",
                    "duplicate", "Employee code already exists");
            model.addAttribute("pageTitle", "Edit Employee");
            model.addAttribute("formAction",
                    "/employees/" + id + "/edit");
            return "employees/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        employeeService.deleteById(id);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Employee deleted successfully");

        return "redirect:/employees";
    }
}