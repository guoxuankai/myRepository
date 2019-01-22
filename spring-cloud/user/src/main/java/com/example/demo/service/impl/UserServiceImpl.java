package com.example.demo.service.impl;

import com.example.demo.dao.EmployeeRepository;
import com.example.demo.entity.Employee;
import com.example.demo.result.LoginResultData;
import com.example.demo.service.UserService;
import com.example.demo.utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public LoginResultData validateUser(Employee employee) {
        List<Employee> byName = employeeRepository.findByName(employee.getName());
        if (!byName.isEmpty()) {
            Employee user = byName.get(0);
            if (user.getPwd().equals(employee.getPwd())) {
                LoginResultData LoginResultData = new LoginResultData();
                LoginResultData.setId(user.getId());
                LoginResultData.setName(user.getName());
                LoginResultData.setUname(user.getUname());
                LoginResultData.setStatus("1");
                LoginResultData.setMsg("登陆成功");
                return LoginResultData;
            } else {
                LoginResultData LoginResultData = new LoginResultData();
                LoginResultData.setStatus("0");
                LoginResultData.setMsg("用户名或密码错误");
                return LoginResultData;
            }
        } else {
            LoginResultData LoginResultData = new LoginResultData();
            LoginResultData.setStatus("0");
            LoginResultData.setMsg("用户名或密码错误");
            return LoginResultData;
        }


    }

    @Override
    public void register(Employee employee) {
        String id = IdUtil.randomUUID();
        employee.setId(id);
        employee.seteAdd(new Date());
        employeeRepository.save(employee);

    }


}
