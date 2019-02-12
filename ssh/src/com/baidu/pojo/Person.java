package com.baidu.pojo;

public class Person {
	private Integer pid;
	private String pname;
	private Integer gender;
	private Dept dept;
	public Person() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Person(Integer pid, String pname, Integer gender, Dept dept) {
		super();
		this.pid = pid;
		this.pname = pname;
		this.gender = gender;
		this.dept = dept;
	}
	
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Dept getDept() {
		return dept;
	}
	public void setDept(Dept dept) {
		this.dept = dept;
	}

	@Override
	public String toString() {
		return "Person [pid=" + pid + ", pname=" + pname + ", gender=" + gender
				+ ", dept=" + dept + "]";
	}
	
}
