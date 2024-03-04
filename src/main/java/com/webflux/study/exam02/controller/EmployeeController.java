package com.webflux.study.exam02.controller;

import com.webflux.study.exam02.Employee;
import com.webflux.study.exam02.EmployeeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/{id}")
    public Mono<Employee> getEmployeeById(@PathVariable(value = "id") String id) {
        return employeeRepository.findEmployeeById(id);
    }
}
