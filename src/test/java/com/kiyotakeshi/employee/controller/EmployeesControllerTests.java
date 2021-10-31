package com.kiyotakeshi.employee.controller;

import com.kiyotakeshi.employee.entity.Employee;
import com.kiyotakeshi.employee.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc
public class EmployeesControllerTests {

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private MockMvc mockMvc;

    private final String BASE_PATH = "/employees";

    @Test
    @DisplayName("Test employees found")
    void getEmployees() throws Exception {

        var employees = new ArrayList(Arrays.asList(
                new Employee(1, "taro", "president"),
                new Employee(2, "jiro", "sales")
        ));

        doReturn(employees).when(employeeService).findEmployees();
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("taro")))
                .andExpect(content().json("""
                        [{"id":1,"name":"taro","department":"president"},{"id":2,"name":"jiro","department":"sales"}]
                        """));
    }

    @Test
    @DisplayName("Test employee found")
    void getEmployee() throws Exception {
        var employee = new Employee(1, "taro", "president");
        doReturn(employee).when(employeeService).findEmployeeById(employee.getId());

        mockMvc.perform(get(BASE_PATH + "/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {"id":1,"name":"taro","department":"president"}
                        """));
    }
}
