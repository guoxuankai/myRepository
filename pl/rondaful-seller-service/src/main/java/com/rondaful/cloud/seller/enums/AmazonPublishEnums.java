package com.rondaful.cloud.seller.enums;

/**
 * 亚马逊刊登枚举
 * @author 9527
 *
 */
public class AmazonPublishEnums {

	/**
	 * 是否有必填项
	 * @author dingshulin
	 *
	 */
	public enum HasRequired {
    	YES(1,"存在必填项"),
    	NO(0,"没有必填项");
    	
    	/** 枚举码. */
        private final Integer code;

        /** 描述信息. */
        private final String desc;
    	
        private HasRequired(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        
    	public Integer getCode() {
    		return code;
    	}

    	public String getDesc() {
    		return desc;
    	}
    }



	/**
	 * 是否有必填项
	 * @author dingshulin
	 *
	 */
	public enum SupplyStatus {

		NORMAL(0,"正常"),
		DOWN(1,"下架"),
		STOCK(2,"缺货"),
		LESS(3,"少货"),
		OTHER(4,"未知"),
		TORT(5,"侵权");

		/** 枚举码. */
		private final Integer code;

		/** 描述信息. */
		private final String desc;

		private SupplyStatus(Integer code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public Integer getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 品连刊登sku上下架状态
	 */
	public enum PLSKUStatus {
		UP(3,"上架"),
		DOWN(1,"下架"),
		OTHER(4,"未知"),
		TORT(5,"侵权");
		/** 枚举码. */
		private final Integer code;

		/** 描述信息. */
		private final String desc;

		private PLSKUStatus(Integer code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public Integer getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 同步时报告本地是否已存在（0：存在 1：不存在）
	 */
	public enum isExist {
		EXIST(0,"存在"),
		NOT_EXIST(1,"不存在"),
		ERROR(2,"异常");

		/** 枚举码. */
		private final Integer code;

		/** 描述信息. */
		private final String desc;

		private isExist(Integer code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public Integer getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}



	/**
	 * 报告数据状态
	 */
	public enum RoportStatus {
		active("active","活跃"),
		inactive("inactive","不活跃"),
		incomplete("incomplete","不完整");

		/** 枚举码. */
		private final String code;

		/** 描述信息. */
		private final String desc;

		private RoportStatus(String code, String desc) {
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

	/**
	 * 返回数据关系的标识 Parent
	 */
	public enum getForIdRelationShips {
		CHILD("VariationParent","是子属性"),
		PARENT("VariationChild","是父体数据");

		/** 枚举码. */
		private final String code;

		/** 描述信息. */
		private final String desc;

		private getForIdRelationShips(String code, String desc) {
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






}
