package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.query.EmpQuery;
import com.example.demo.query.ResultData;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
public interface EmployeeService {

    void insert(Employee employee);

    ResultData<Employee> searchByQuery(EmpQuery empQuery);

    List<Employee> getAll();

    Employee get(String id);

    void update(Employee employee);

    void delete(String id);


}
