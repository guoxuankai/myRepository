package com.baidu.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.baidu.pojo.Dept;

@Repository("deptDao")
public class DeptDaoImpl implements DeptDaoI{

	@Autowired
	@Qualifier("hibernateTemplate")
	private HibernateTemplate hibernateTemplate;
	@Override
	public List<Dept> findDeptAll() {
		// TODO Auto-generated method stub
		return hibernateTemplate.find("from Dept");
	}

}
