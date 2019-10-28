package com.rondaful.cloud.commodity.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/11
 * @Description:
 */
public enum WarehouseFirmEnum {

	WMS("WMS","品连云仓"),
    RONDAFUL("RONDAFUL","利郎达"),
    GOODCANG("GOODCANG","易可达");

    private String code;

    private String name;

    WarehouseFirmEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public static String getNameByCode(String code) {
		for (WarehouseFirmEnum firm : WarehouseFirmEnum.values()) {
			if (code.equals(firm.getCode())) {
				return firm.getName();
			}
		}
		return null;
	}
    
    public static List<String> getNames() {
    	List<String> names=new ArrayList<String>();
		for (WarehouseFirmEnum firm : WarehouseFirmEnum.values()) {
			names.add(firm.getName());
		}
		return names;
	}

}
