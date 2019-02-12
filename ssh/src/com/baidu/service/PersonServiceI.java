package com.baidu.service;

import java.util.List;

import com.baidu.pojo.Person;

public interface PersonServiceI {

	List<Object[]> findAll();

	void add(Person person);

	void delete(Integer pid);

	Person findPersonById(Integer pid);

	void updatePerson(Person person);
}
