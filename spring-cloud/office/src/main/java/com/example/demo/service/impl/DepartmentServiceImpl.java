package com.example.demo.service.impl;

import com.example.demo.dao.DepartmentRepository;
import com.example.demo.entity.Department;
import com.example.demo.service.DepartmentService;
import com.example.demo.utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void insert(Department department) {
        String id = IdUtil.randomUUID();
        department.setId(id);
        departmentRepository.save(department);

    }

    @Override
    public List<Department> getAll() {
        return departmentRepository.findAll();
    }


}
