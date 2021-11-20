package com.kiyotakeshi.employee.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeTests {

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("Test find employee from initialized data in data-h2.sql")
    void find() {
        var employee = em.find(Employee.class, 1);
        var employee2 = em.find(Employee.class, 2);
        assertThat(employee.getDepartment()).isEqualTo("sales");
        assertThat(employee2.getName()).isEqualTo("jiro");
    }

    @Test
    @DisplayName("Test save(insert) new employee data")
    void save() {
        var newEmployee = new Employee("ichiro", "account");
        em.persistAndFlush(newEmployee);

        var employee = em.find(Employee.class, 3);
        assertThat(employee.getName()).isEqualTo("ichiro");
    }

    @Test
    @DisplayName("Test update employee data")
    void update() {
        var employee = em.find(Employee.class, 1);
        employee.setDepartment("president");

        // update 文が発行される
        em.persistAndFlush(employee);

        // session を使い回すため？同じオブジェクトを参照することになるため
        // 意味のある assertion にはならない
        // var updatedEmployee = em.find(Employee.class, 1);
        // assertThat(updatedEmployee.getDepartment()).isEqualTo("president");
        // System.out.println(employee.equals(updatedEmployee)); // true
    }
}
