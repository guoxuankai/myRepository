package com.baidu.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.baidu.pojo.Dept;
import com.baidu.pojo.Person;
import com.baidu.service.DeptServiceI;
import com.baidu.service.PersonServiceI;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.util.ValueStack;
@Controller("personAction")
@Scope("prototype")
public class PersonAction extends ActionSupport implements ModelDriven<Person>{

	@Autowired
	@Qualifier("personService")
	private PersonServiceI personService;
	
	@Autowired
	@Qualifier("deptService")
	private DeptServiceI deptService;
	
	private Person person = new Person();
	@Override
	public Person getModel() {
		// TODO Auto-generated method stub
		return person;
	}
	public String list() {
		List<Object[]> List = personService.findAll();
		List<Person> personList = new ArrayList<Person>();
		for (Object[] objects : List) {
			Person person = new Person((Integer)objects[0],(String)objects[1], (Integer)objects[2], (Dept)objects[3]);
			personList.add(person);
		}
		ValueStack valueStack = ActionContext.getContext().getValueStack();
		valueStack.set("personList", personList);
		return "list";
	}
	public String toAdd(){
		List<Dept> deptList = deptService.findDeptAll();
		ValueStack valueStack = ActionContext.getContext().getValueStack();
		valueStack.set("deptList", deptList);
		return "toadd";
	}
	public String add(){
		personService.add(person);
		return "add";
	}
	public String delete(){
		personService.delete(person.getPid());
		return "delete";
	}
	public String toUpdate(){
		Person persons = personService.findPersonById(person.getPid());
		List<Dept> deptList = deptService.findDeptAll();
		ValueStack valueStack = ActionContext.getContext().getValueStack();
		valueStack.set("persons", persons);
		valueStack.set("deptList", deptList);
		return "toupdate";
	}
	public String update(){
		personService.updatePerson(person);
		return "update";
	}
}
