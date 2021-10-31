package com.kiyotakeshi.employee.contoller;

import com.kiyotakeshi.employee.entity.Employee;
import com.kiyotakeshi.employee.entity.EmployeeRequest;
import com.kiyotakeshi.employee.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeesController {

    private EmployeeService employeeService;

    public EmployeesController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getEmployees() {
        List<Employee> employeeList = employeeService.findEmployees();
        return ResponseEntity.ok().body(employeeList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable int id) {
        Employee employee = employeeService.findEmployeeById(id);
        return ResponseEntity.ok().body(employee);
    }

    @PostMapping
    public ResponseEntity<Employee> addEmployee(@RequestBody EmployeeRequest request) {
        var employee = new Employee(request.getName(), request.getDepartment());
        Employee saved = employeeService.save(employee);
        return ResponseEntity.ok().body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable int id, @RequestBody EmployeeRequest request) {
        Employee update = employeeService.update(id, request);
        try {
            return ResponseEntity
                    .created(new URI("/employees/" + update.getId()))
                    .body(update);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable int id) {
        employeeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
