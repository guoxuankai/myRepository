package com.baidu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.baidu.dao.PersonDaoI;
import com.baidu.pojo.Person;

@Service("personService")
public class PersonServiceImpl implements PersonServiceI{
	
	@Autowired
	@Qualifier("personDao")
	private PersonDaoI personDao;

	@Override
	public List<Object[]> findAll() {
		return personDao.findAll();
	}

	@Override
	public void add(Person person) {		
		personDao.add(person);
	}

	@Override
	public void delete(Integer pid) {
		Person person = personDao.findPersonById(pid);//先根据id查询对象
		personDao.delete(person);//删除对象
	}

	@Override
	public Person findPersonById(Integer pid) {
		return personDao.findPersonById(pid);
	}

	@Override
	public void updatePerson(Person person) {
		personDao.updatePerson(person);
	}
	
}
