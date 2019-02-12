package com.baidu.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.baidu.pojo.Person;
import com.sun.org.apache.bcel.internal.generic.NEW;

@Repository("personDao")
public class PersonDaoImpl implements PersonDaoI{

	@Autowired
	@Qualifier("hibernateTemplate")
	private HibernateTemplate hibernateTemplate;
	@Override
	public List<Object[]> findAll() {
		//返回的是一个含有object数组的集合
		return hibernateTemplate.find("select p.pid,p.pname,p.gender,p.dept from Person p");
	}
	@Override
	public void add(Person person) {
		hibernateTemplate.saveOrUpdate(person);
	}
	@Override
	public void delete(Person person) {
		hibernateTemplate.delete(person);
	}
	@Override
	public Person findPersonById(Integer pid) {
		return hibernateTemplate.get(Person.class, pid);
	}
	@Override
	public void updatePerson(Person person) {
		hibernateTemplate.update(person);
	}

}
