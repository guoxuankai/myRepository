package com.baidu.dao;

import java.util.List;

import com.baidu.pojo.Person;

public interface PersonDaoI {

	List<Object[]> findAll();

	void add(Person person);

	void delete(Person person);

	Person findPersonById(Integer pid);

	void updatePerson(Person person);

}
