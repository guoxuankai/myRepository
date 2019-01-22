package com.example.demo.dao;

import com.example.demo.entity.Employee;
import com.example.demo.query.EmpQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String>, BaseRepository<Employee, EmpQuery> {
}

