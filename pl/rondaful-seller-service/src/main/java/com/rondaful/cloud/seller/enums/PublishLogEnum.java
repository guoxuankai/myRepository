package com.rondaful.cloud.seller.enums;
/**
 * 刊登操作日志类型
 * @author dsl
 *
 */
public enum PublishLogEnum {
	COPY("COPY","复制"),
	EDIT("EDIT","编辑"),
	DELETE("DELETE","删除"),
	INSERT("INSERT","添加"),
	PUBLISH("PUBLISH","刊登");
	/** 枚举码. */
    private final String code;

    /** 描述信息. */
    private final String desc;
	
    private PublishLogEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

}



