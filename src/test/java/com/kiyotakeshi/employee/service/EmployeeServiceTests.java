package com.kiyotakeshi.employee.service;

import com.kiyotakeshi.employee.entity.Employee;
import com.kiyotakeshi.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    // これを参考にした
    // https://medium.com/backend-habit/integrate-junit-and-mockito-unit-testing-for-service-layer-a0a5a811c58a
    @Mock
    private EmployeeRepository employeeRepository;

    // creates the mock implementation
    // additionally injects the dependent mocks that are marked with the annotations @Mock into it.
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void getEmployees() {
        var employees = new ArrayList(Arrays.asList(
                new Employee(1, "taro", "president"),
                new Employee(2, "jiro", "sales")
        ));
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> expected = employeeService.findEmployees();
        assertEquals(expected, employees);
    }

    @Test
    void getEmployee() {
        var employee = new Employee(1,"ichiro", "account");
        when(employeeRepository.findById(1)).thenReturn(Optional.ofNullable(employee));

        var expected = employeeService.findEmployeeById(1);
        assertThat(expected).isNotNull();
    }
}
