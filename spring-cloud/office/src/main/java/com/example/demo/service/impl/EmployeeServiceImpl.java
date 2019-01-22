package com.example.demo.service.impl;

import com.example.demo.dao.EmployeeRepository;
import com.example.demo.entity.Employee;
import com.example.demo.query.EmpQuery;
import com.example.demo.query.ResultData;
import com.example.demo.service.EmployeeService;
import com.example.demo.utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public void insert(Employee employee) {
        String id = IdUtil.randomUUID();
        employee.setId(id);
        employee.seteAdd(new Date());
        employeeRepository.save(employee);

    }

    @Override
    public ResultData<Employee> searchByQuery(EmpQuery empQuery) {
        ResultData<Employee> resultData = employeeRepository.searchByQuery(empQuery);
        return resultData;
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee get(String id) {
        Employee employee = employeeRepository.findById(id).get();
        return employee;
    }

    @Override
    public void update(Employee employee) {
        employeeRepository.saveAndFlush(employee);

    }

    @Override
    public void delete(String id) {
        employeeRepository.deleteById(id);
    }


}
