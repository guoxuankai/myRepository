package com.rondaful.cloud.commodity.enums;

/**
* @Description:国家代号-中文名枚举
* @author:范津 
* @date:2019年3月13日 上午11:29:55
 */
public enum CountryCodeEnum {
	//BR("BR", "巴西"), 
	CA("CA", "加拿大"),
	MX("MX", "墨西哥"), 
	US("US", "美国"), 
	DE("DE", "德国"),
	ES("ES", "西班牙"), 
	FR("FR", "法国"),
	GB("GB", "英国"),
	//IN("IN", "印度"),
	IT("IT", "意大利"),
	AU("AU", "澳大利亚"),
	//TR("TR", "土耳其"),
	JP("JP", "日本"),
	//CN("CN", "中国"),
	
	Canada("Canada", "加拿大"),
	CanadaFrench("CanadaFrench", "加拿大"),
	UK("UK", "英国"),
	Germany("Germany", "德国"),
	Australia("Australia", "澳大利亚"),
	France("France", "法国"),
	eBayMotors("eBayMotors", "Ebay汽车(美国)"),
	Italy("Italy", "意大利"),
	Netherlands("Netherlands", "荷兰"),
	Spain("Spain", "西班牙"), 
	HongKong("HongKong", "香港"),
	Singapore("Singapore", "新加坡"),
	Poland("Poland", "波兰"),
	Belgium_Dutch("Belgium_Dutch", "比利时(荷兰语)"),
	Belgium_French("Belgium_French", "比利时(法语)"),
	Austria("Austria", "奥地利"),
	Switzerland("Switzerland", "瑞土"),
	Ireland("Ireland", "爱尔兰土");
	
	private String nameEn;
	private String nameCn;

	
	private CountryCodeEnum(String nameEn, String nameCn) {
		this.nameEn = nameEn;
		this.nameCn = nameCn;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameCn() {
		return nameCn;
	}

	public void setNameCn(String nameCn) {
		this.nameCn = nameCn;
	}
	
	public static String getNameCnByNameEn(String nameEn) {
		for (CountryCodeEnum country : CountryCodeEnum.values()) {
			if (nameEn.equals(country.getNameEn())) {
				return country.getNameCn();
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(CountryCodeEnum.getNameCnByNameEn("Ireland"));
	}
}
