package com.baidu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.baidu.dao.DeptDaoI;
import com.baidu.pojo.Dept;

@Service("deptService")
public class DeptServiceImpl implements DeptServiceI{
	@Autowired
	@Qualifier("deptDao")
	private DeptDaoI deptDao;
	@Override
	public List<Dept> findDeptAll() {
		// TODO Auto-generated method stub
		return deptDao.findDeptAll();
	}

}
