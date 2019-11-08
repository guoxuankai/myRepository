package com.brandslink.cloud.common.constant;

public class SystemConstants {

	public enum nameType {
		/** 入库服务 **/
		SYS_INBOUND("入库服务"),
		/** 出库服务 **/
		SYS_OUTBOUND("出库服务"),
		/** 中心服务 **/
		SYS_CENTER("中心服务"),
		/** 物流服务 **/
		SYS_LOGISTICS("物流服务"),
		/** 库内服务 **/
		SYS_WAREHOUSE("库内服务"),
		/** 用户服务 **/
		SYS_USER("用户服务"),
		/** 报表服务 **/
		SYS_REPORT("报表服务"),
		/** 财务服务 **/
		SYS_FINANCE("财务服务");

		private String description;

		private nameType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

	}
	
}
