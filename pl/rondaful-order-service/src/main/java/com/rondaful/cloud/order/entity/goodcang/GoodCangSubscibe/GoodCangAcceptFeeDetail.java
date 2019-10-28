package com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe;

import lombok.Data;

@Data
public class GoodCangAcceptFeeDetail {
    private String totalFee	        ;//float	Require	总费用
    private String SHIPPING	        ;//float	Require	运输费
    private String OPF	            ;//float	Require	操作费用
    private String FSC	            ;//float	Require	燃油附加费
    private String DT	            ;//float	Require	关税
    private String  RSF	            ;//float	Require	挂号
    private String OTF	            ;//float	Require	其它费用
    private String currency_code	;//string	Require	订单费用币种
}
