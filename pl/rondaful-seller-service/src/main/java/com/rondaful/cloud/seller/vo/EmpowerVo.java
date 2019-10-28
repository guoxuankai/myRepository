package com.rondaful.cloud.seller.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class EmpowerVo implements Serializable {
	@ApiModelProperty(value = "id")
    private Integer empowerId;
	
	@ApiModelProperty(value = "账号（自定义名称）,非amazon上的卖家账户，是用户自定义写的，与amazon账号无关")
    private String account;
	
	
	@ApiModelProperty(value = "平台 (1 ebay   2 amazon 3 aliexpress)")
    private Integer platform;
	
	@ApiModelProperty(value = "品连账号")
    private String pinlianAccount;


	private String company;//供应链公司
	private int type=0;//类型

	public Integer getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(Integer empowerId) {
		this.empowerId = empowerId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getPinlianAccount() {
		return pinlianAccount;
	}

	public void setPinlianAccount(String pinlianAccount) {
		this.pinlianAccount = pinlianAccount;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "EmpowerVo [empowerId=" + empowerId + ", platform=" + platform + ", pinlianAccount=" + pinlianAccount
				+ "]";
	}
	
    

	
}