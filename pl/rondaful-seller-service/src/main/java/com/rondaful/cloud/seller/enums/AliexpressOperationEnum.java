package com.rondaful.cloud.seller.enums;


public enum AliexpressOperationEnum {

	DRAFT("1","发布为草稿"),
    PUBLISH("2","发布并刊登"),
    RELIST("3","重刊登"),
    EDIT_REMARK("4","编辑备注"),
    EDIT("5","刊登编辑"),
    COPY("6","复制"),
    END("7","下架"),
    DRAFT_RELIST("8","草稿刊登"),
	FAIL_RELIST("9","刊登失败重刊登"),
    DELETE_PUBLISH("10","删除刊登"),
    UPDATE_PRICE("11","修改商品价格"),
    UPDATE_INVENTORY("12","修改商品库存"),
    PUTAWAY("13","上架"),
    SYNC("14","同步listing");
    private String code;
    private String msg;
    private AliexpressOperationEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

}
