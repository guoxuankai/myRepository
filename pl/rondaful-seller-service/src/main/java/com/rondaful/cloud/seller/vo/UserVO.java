package com.rondaful.cloud.seller.vo;


/**
 * 用户服务返回的相关属性返给前端
 * @author (#^.^#)
 *
 */
public class UserVO {

	private Integer platformType;
	/**
	 * 注意!!!这里的username、是账号管理里面的姓名字段。。。
	 */
	private String userName;
	private Integer userId;
	//private List<Integer> childs;
	public Integer getPlatformType() {
		return platformType;
	}
	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	/*public List<Integer> getChilds() {
		return childs;
	}
	public void setChilds(List<Integer> childs) {
		this.childs = childs;
	}*/
	
	
}
