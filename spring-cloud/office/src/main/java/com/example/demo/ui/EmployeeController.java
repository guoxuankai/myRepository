package com.example.demo.ui;

import com.example.demo.entity.Employee;
import com.example.demo.enums.ErrorEnum;
import com.example.demo.query.EmpQuery;
import com.example.demo.query.ResultData;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emp")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = "/emp/{id}")
    public Employee getOne(@PathVariable("id") String id) {

        Employee employee = employeeService.get(id);

        return employee;
    }

    @DeleteMapping(value = "/emp/{id}")
    public ErrorEnum delete(@PathVariable("id") String id) {
        try {
            employeeService.delete(id);
            return ErrorEnum.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEnum.FAILED;
        }
    }

    @PostMapping(value = "/emp")
    public ErrorEnum insert(@RequestBody Employee employee) {
        try {
            employeeService.insert(employee);
            return ErrorEnum.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEnum.FAILED;
        }
    }

    @PutMapping(value = "/emp")
    public ErrorEnum update(@RequestBody Employee employee) {
        try {
            employeeService.update(employee);
            return ErrorEnum.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEnum.FAILED;
        }
    }

    @PostMapping(value = "/list")
    public ResultData<Employee> searchByQuery(@RequestBody EmpQuery empQuery) {

        ResultData<Employee> resultData = employeeService.searchByQuery(empQuery);

        return resultData;
    }

    @GetMapping(value = "/getAll")
    public List<Employee> getAll() {

        List<Employee> departments = employeeService.getAll();

        return departments;
    }


}
