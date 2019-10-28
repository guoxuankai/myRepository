package com.rondaful.cloud.order.model.vo.sysorder;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class EmpowerRequestVo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer empowerId;
	
	private String account;
	
	private Integer platform;
	
	private String dataType;

	private List<Integer> bindCode;

	private Integer page;

	private Integer row;
}
