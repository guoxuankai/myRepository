package com.example.demo.dao;

import com.example.demo.entity.Emp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmpDao {

    List<Emp> getAll();

}
