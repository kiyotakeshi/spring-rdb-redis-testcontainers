package com.kiyotakeshi.employee.entity;

import javax.validation.constraints.NotNull;

public class EmployeeRequest {

    @NotNull
    private String name;

    @NotNull
    private String department;

    public EmployeeRequest(String name, String department) {
        this.name = name;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }
}
