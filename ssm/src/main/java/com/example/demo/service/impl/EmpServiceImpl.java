package com.example.demo.service.impl;

import com.example.demo.dao.EmpDao;
import com.example.demo.entity.Emp;
import com.example.demo.service.EmpService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpDao empDao;


    public PageInfo<Emp> list(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Emp> all = empDao.getAll();
        PageInfo<Emp> pageInfo = new PageInfo<Emp>(all);
        return pageInfo;
    }


}
