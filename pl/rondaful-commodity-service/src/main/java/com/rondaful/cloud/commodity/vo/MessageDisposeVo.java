package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;

/**
 *  @Description: 待办消息
 */
public class MessageDisposeVo implements Serializable{
	private static final long serialVersionUID = 1L;

	//商品审核 COMMODITY_CHECK、商品品牌审核 COMMODITY_BRAND_CHECK
    private String identify;
    
    //所属平台 0供应商 1卖家 2后台
    private Integer belongSys;
    
    //数量
    private Integer num;



    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }


    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

	public Integer getBelongSys() {
		return belongSys;
	}

	public void setBelongSys(Integer belongSys) {
		this.belongSys = belongSys;
	}

}
