package com.example.demo.service;

import com.example.demo.entity.Department;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
public interface DepartmentService {

    void insert(Department department);

    List<Department> getAll();

}
