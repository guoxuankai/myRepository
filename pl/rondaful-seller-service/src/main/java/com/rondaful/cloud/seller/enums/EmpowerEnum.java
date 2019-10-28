package com.rondaful.cloud.seller.enums;

/**
 * 授权枚举相关
 * @author dingshulin
 *
 */
public class EmpowerEnum {

    public enum EmpowerStatus {
    	UN_AUTHORIZATION(0,"未授权"),
    	NORMAL_AUTHORIZATION(1,"正常授权"),
    	AUTHORIZATION_EXPIRE(2,"授权过期"),
    	AUTHORIZATION_STOP(3,"停用");
    	
    	/** 枚举码. */
        private final int code;

        /** 描述信息. */
        private final String desc;
    	
        private EmpowerStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        
    	public int getCode() {
    		return code;
    	}

    	public String getDesc() {
    		return desc;
    	}
    }
    
    
    public enum EmpowerPlatform {
    	EBAY(1,"ebay"),
    	AMAZON(2,"amazon");
    	
    	/** 枚举码. */
        private final int code;

        /** 描述信息. */
        private final String desc;
    	
        private EmpowerPlatform(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        
    	public int getCode() {
    		return code;
    	}

    	public String getDesc() {
    		return desc;
    	}
    }
    
    
    
	
}
