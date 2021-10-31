package com.kiyotakeshi.employee.service;

import com.kiyotakeshi.employee.entity.Employee;
import com.kiyotakeshi.employee.exception.ResourceNotFoundException;
import com.kiyotakeshi.employee.repository.EmployeeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> findEmployees() {
        System.out.println("fetching from DB");
        return employeeRepository.findAll();
    }

    @Override
    @Cacheable(cacheNames = "employee", key = "#id", unless = "#result == null")
    public Employee findEmployeeById(int id) {
        System.out.println("fetching from DB: " + id);
        return employeeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("employee not found:" + id)
        );
    }
}
