package org.lpl.demo.bean;

import java.util.List;
import java.util.Map;

public class User {

	private Long id;
	private String username;
	private String name;
	private Integer age;
	private Integer sex;
	private String phone;
	private String idCrad;
	private String email;
	private String studentId;
	private String classname;
	private String school;
	private String address;
	private String city;
	private String title;
	private String content;
	private String orderId;
	private String avatar;
	private String term;
	private String course;
	private String subject;
	private String topic;
	private List<Map<String,Object>> stringList;
	

	public User(Long id, String username, String name, Integer age, Integer sex, String phone, String idCrad,
			String email, String studentId, String classname, String school, String address, String city, String title,
			String content, String orderId, String avatar, String term, String course, String subject, String topic,
			List<Map<String, Object>> stringList) {
		super();
		this.id = id;
		this.username = username;
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.phone = phone;
		this.idCrad = idCrad;
		this.email = email;
		this.studentId = studentId;
		this.classname = classname;
		this.school = school;
		this.address = address;
		this.city = city;
		this.title = title;
		this.content = content;
		this.orderId = orderId;
		this.avatar = avatar;
		this.term = term;
		this.course = course;
		this.subject = subject;
		this.topic = topic;
		this.stringList = stringList;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getIdCrad() {
		return idCrad;
	}
	public void setIdCrad(String idCrad) {
		this.idCrad = idCrad;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public List<Map<String, Object>> getStringList() {
		return stringList;
	}
	public void setStringList(List<Map<String, Object>> stringList) {
		this.stringList = stringList;
	}
	
	
	
}
