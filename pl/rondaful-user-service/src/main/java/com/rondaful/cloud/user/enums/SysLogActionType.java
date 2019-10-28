package com.rondaful.cloud.user.enums;

/** 接口操作类型  */
public class SysLogActionType {
	/** 操作类型:增加*/
	public static final String ADD = "ADD";
	
	/**操作类型: 修改 */
	public static final String UDPATE = "UDPATE";
	
	/**操作类型: 删除 */
	public static final String DELETE = "DELETE";
	
	/**操作类型: 查询 */
	public static final String QUERY = "QUERY";
	
	/*
	  ADD("增加", "1"), UDPATE("修改", "2"), DELETE("删除", "3"), QUERY("查询", "3");  
    // 成员变量  
    private String name;  
    private String index; 
    
    // 构造方法  
    private SysLogActionType(String name, String index) {  
        this.name = name;  
        this.index = index;  
    } 
    
    // 普通方法  
    public static String getName(int index) {  
        for (SysLogActionType c : SysLogActionType.values()) {  
            if (c.getIndex().equals(index)) {  
                return c.name;  
            }  
        }  
        return null;  
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	} 
    */
    
}
