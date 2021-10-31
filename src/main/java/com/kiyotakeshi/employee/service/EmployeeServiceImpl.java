package com.kiyotakeshi.employee.service;

import com.kiyotakeshi.employee.entity.Employee;
import com.kiyotakeshi.employee.entity.EmployeeRequest;
import com.kiyotakeshi.employee.exception.ResourceNotFoundException;
import com.kiyotakeshi.employee.repository.EmployeeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
    // @Cacheable(cacheNames = "employee")
    public List<Employee> findEmployees() {
        // System.out.println("fetching from DB");
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

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    @CachePut(value = "employee", key = "#id")
    public Employee update(int id, EmployeeRequest request) {
        var existingEmployee = this.findEmployeeById(id);
        System.out.println("updating employee with name:" + existingEmployee.getName());

        // update
        existingEmployee.setName(request.getName());
        existingEmployee.setDepartment(request.getDepartment());

        return this.save(existingEmployee);
    }

    @Override
    @CacheEvict(value = "employee",  key = "#id")
    public void delete(int id) {
        var existingEmployee = this.findEmployeeById(id);
        System.out.println("deleting employee with name:" + existingEmployee.getName());
        employeeRepository.delete(existingEmployee);
    }
}
