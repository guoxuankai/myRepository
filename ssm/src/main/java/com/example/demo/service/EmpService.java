package com.example.demo.service;

import com.example.demo.entity.Emp;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface EmpService {

    PageInfo<Emp> list(int page, int pageSize);

}
