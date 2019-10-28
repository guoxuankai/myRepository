package com.rondaful.cloud.supplier.model.enums;

/**
 * @Author: xqq
 * @Date: 2019/6/11
 * @Description:
 */
public enum WarehouseFirmEnum {

    RONDAFUL("RONDAFUL","利郎达"),
    WMS("WMS","品连云仓"),
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

    public static String getByCode(String code){
        WarehouseFirmEnum[] enums=values();
        for (WarehouseFirmEnum firmEnum:enums) {
            if (firmEnum.getCode().equals(code)){
                return firmEnum.getName();
            }
        }
        return null;
    }

    public static String getByName(String name){
        WarehouseFirmEnum[] enums=values();
        for (WarehouseFirmEnum firmEnum:enums) {
            if (firmEnum.getName().equals(name)){
                return firmEnum.getCode();
            }
        }
        return null;
    }

}
