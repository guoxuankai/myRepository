package com.example.demo.ui;

import com.example.demo.entity.Emp;
import com.example.demo.service.EmpService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmpController {

    @Autowired
    private EmpService empService;

    @GetMapping("/list/{page}/{pageSize}")
    public PageInfo<Emp> list(@PathVariable("page") int page, @PathVariable("pageSize") int pageSize) {
        PageInfo<Emp> pageInfo = empService.list(page, pageSize);
        return pageInfo;
    }


}
