package com.rondaful.cloud.seller.enums;

public class AmazonTemplateEnums {


    /**
     * 模板的默认状态
     */
    public enum DefaultTemplate {
        //是否默认模板 0 ：是 1：否  2:全局默认模板
        NOT_DEFAULT("1 代表不是默认模板", 1),
        DEFAULT("0 代表是默认模板", 0),
        GLOBAL_DEFAULT("2 代表是全局默认模板", 2);

        private String msg;
        private Integer type;

        DefaultTemplate(String msg, Integer type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
    }

    /**
     * 商品第一分类 生成规则
     */
    public enum CategoryFirst {  //classify

        CLASSIFY_MAP("检索商品分类映射表", "classifyMap");
        private String msg;
        private String type;

        CategoryFirst(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 商品第二分类 生成规则
     */
    public enum CategorySecond {  //classify
        NOT_SET("不设置商品第二分类", "notSet"),
        CLASSIFY_MAP("检索商品分类映射表", "classifyMap");
        private String msg;
        private String type;

        CategorySecond(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 刊登类型，1:单属性格式,2:多属性格式
     */
    public enum PublishType {
        SINGLE_ATTRIBUTE("单属性格式", 1),
        MULTIPLE_ATTRIBUTE("多属性格式", 2),
        AUTO_ATTRIBUTE("自动确定", 3);
        private String msg;
        private Integer type;

        PublishType(String msg, Integer type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
    }

    /**
     * 平台SKU 生成无规则号码 店铺名称 商品SPU 品连SKU 固定内容 自增序号
     */
    public enum PlatformSku {

        NOT_RULE_NO("生成无规则号码", "notRuleNo"),
        SHOP_NAME("店铺名称", "shopName"),
        GOODS_SPU("商品SPU", "goodsSPU"),
        PIN_LIN_SKU("品连SKU", "pinLinSKU"),
        FIXED_CONTENT("固定内容", "fixedContent"),
        INCR_NO("自增序号", "incrNo"),
        RANDOM_MEG("随机类容","randomMsg");
        private String msg;
        private String type;

        PlatformSku(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 品牌名    [{"默认值":"1111","no":"1"},{"取店铺名称":"取店铺名称","no":"4"},{"实际品牌名称":"实际品牌名称","no":"2"}，{"实际品牌名称若为空":"55555555","no":"2"}]
     */
    public enum BrandRule {  //Actual brand name

        DEFAULT("默认值", "default"),
        SHOP_NAME("取店铺名称", "shopName"),
        ACTUAL_BRAND("实际品牌名称", "actualBrand"),
        ACTUAL_BRAND_SET_DEFAULT("实际品牌名称若为空", "actualBrandSetDefault");
        private String msg;
        private String type;

        BrandRule(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 商品标题    [{"商品英文名":"商品英文名","no":"1"},{"品牌名称+商品英文名":"品牌名称+商品英文名","no":"4"}]
     */
    public enum productTitle {
        GOODS_EN_NAME("商品英文名", "goodsEnName"),
        BRAND_ADD_GOODS_EN_NAME("品牌名称+商品英文名", "brandAddGoodsEnName");
        private String msg;
        private String type;

        productTitle(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 商品编码    [{"用户自行填写":"用户自行填写","no":"1"},{"自动获取UPC":"自动获取UPC","no":"2"},{"自动获取EAN":"自动获取EAN","no":"3"}]
     */
    public enum productNo {
    	INPUTBox("用户自行填写", "inputBox"),
        UPC("自动获取UPC", "upc"),
        EAN("自动获取EAN", "ean");

        private String msg;
        private String type;

        productNo(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     *价格  [{"用户自行填写":"用户自行填写","no":"1"}]
     */
    public enum productPrice {

        INPUT_BOX("用户自行填写","inputBox");
        private String msg;
        private String type;

        productPrice(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 可售数  [{"默认值":"默认值","no":"1"}]
     */
    public enum quantity{
        DEFAULT("默认值","default");
        private String msg;
        private String type;

        quantity(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 制造商 [{"默认值":"1111","no":"1"},{"取店铺名称":"取店铺名称","no":"4"},{"实际品牌名称":"实际品牌名称","no":"2"}，{"实际品牌名称若为空":"55555555","no":"2"}]
     */
    public enum manufacturer{

        DEFAULT("默认值","default"),
        SHOP_NAME("取店铺名称","shopName"),
        BRAND("实际制造商","brand"),
        BRAND_NULL("实际制造商若为空","brandNull");
        private String msg;
        private String type;

        manufacturer(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * part_number [{"默认值":"1111","no":"1"},{"设置为平台SKU":"设置为平台SKU","no":"2"}]
     */
    public enum partNumber{
        DEFAULT("默认值","default"),
        PLATFORM_SKU("设置为平台SKU","platformSKU");
        private String msg;
        private String type;

        partNumber(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 商品描述  [{"随机欢迎语":["aaa","bbbbb","ccccc"],"no":"1"},{"商品标题":"商品标题","no":"2"},{"商品卖点":"商品卖点","no":"3"},
     * {"商品描述":"商品描述","no":"2"},{"包装清单":"包装清单","no":"2"},{"随机结束语":["1111","2222","33333"],"no":"2"}]  、、virtue
     */
    public enum description{
        RANDOM_WELCOME("随机欢迎语","randomWelcome"),
        GOODS_TITLE("商品标题","doodsTitle"),
        GOODS_VIRTUE("商品卖点","goodsVirtue"),
        GOODS_DESCRIP("商品描述","goodsDescrip"),
        PACK_LIST("包装清单","packList"),
        RANDOM_END("随机结束语","randomEnd");

        private String msg;
        private String type;

        description(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 父体图片 "主图":[{"从SPU图片中随机取一张","从SPU图片中随机取一张"},{"混合所有SKU的主图并从中随机取一张":"混合所有SKU的主图并从中随机取一张"},
     * {"若SPU图片为空或不足取":"混合所有SKU的主图并从中随机取一张"}]
     */
    public enum parentMainImage{
        FIND_TO_SPU("从SPU图片中随机取一张","findToSPU"),
        FIND_TO_SKU("混合所有SKU的主图并从中随机取一张","findToSKU"),
        SUPPLY_TO_SKU("若SPU图片为空或不足取","supplyToSKU");

        private String msg;
        private String type;

        parentMainImage(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 父体图片  "附图":[{"从SPU图片中随机取":{"min":1,"max":8}},{"混合所有SKU的附图并从中随机取随机取":{"min":1,"max":8}},{"若SPU图片为空或不足取":"混合所有SKU的附图并从中随机取随机取"}]
     */
    public enum parentAdditionImage{

        FIND_TO_SPU("从SPU图片中随机取","findToSPU"),
        FIND_TO_SKU("混合所有SKU的附图并从中随机取随机取","findToSKU"),
        SUPPLY_TO_SKU("若SPU图片为空或不足取","supplyToSKU");
        private String msg;
        private String type;

        parentAdditionImage(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 子体图片 "主图":[{"从SKU图片中随机取一张","从SKU图片中随机取一张"}]
     */
    public enum childMainImage{
        FIND_TO_SKU("混合所有SKU的主图并从中随机取一张","findToSKU");

        private String msg;
        private String type;

        childMainImage(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 子体图片 "附图":[{"从SKU图片中随机取":{"min":1,"max":8}}]
     */
    public enum childAdditionImage{
        FIND_TO_SKU("混合所有SKU的附图并从中随机取随机取","findToSKU");
        private String msg;
        private String type;

        childAdditionImage(String msg, String type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


    /**
     * 授权账号是否选择全部
     */
    public enum isAll {
    	YES("YES","是全部"),
    	NO("NO","不是全部");
    	
    	/** 枚举码. */
        private final String code;

        /** 描述信息. */
        private final String desc;
    	
        private isAll(String code, String desc) {
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
    
   
    public enum DataSource{
    	PL(1,"品连系统"),
    	AMAZON(2,"亚马逊"),
    	ERP(3,"ERP");
    	
    	/** 枚举码. */
        private final int code;

        /** 描述信息. */
        private final String desc;
    	
        private DataSource(int code, String desc) {
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
