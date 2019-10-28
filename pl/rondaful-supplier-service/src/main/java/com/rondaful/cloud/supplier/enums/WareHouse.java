package com.rondaful.cloud.supplier.enums;

/**
 * 仓库
 * @author songjie
 *
 */
public class WareHouse {

	public enum WareHouseTypeEnum {

		HouseType_0(0L, "自营"), HouseType_1(1L, "品莲"), HouseType_2(2L, "第三方");

		private Long code;
		private String msg;

		private WareHouseTypeEnum(Long code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public Long getCode() {
			return code;
		}

		public String getStrCode() {
			return String.valueOf(code);
		}

		public String getMsg() {
			return msg;
		}
	}
	
	public enum WareHouseProviderEnum {

		HouseProviderType_0(0L, "利朗达"), HouseProviderType_1(1L, "易可达");

		private Long code;
		private String msg;

		private WareHouseProviderEnum(Long code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public Long getCode() {
			return code;
		}

		public String getStrCode() {
			return String.valueOf(code);
		}

		public String getMsg() {
			return msg;
		}
	}

}
