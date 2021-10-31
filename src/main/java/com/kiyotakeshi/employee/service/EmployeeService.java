package com.kiyotakeshi.employee.service;

import com.kiyotakeshi.employee.entity.Employee;

import java.util.List;

public interface EmployeeService {

    List<Employee> findEmployees();

    Employee findEmployeeById(int id);

    Employee save(Employee employee);
}
