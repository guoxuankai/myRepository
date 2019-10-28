package com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodCangAcceptVO {

        private String order_code	        ;//string(32)	Require	订单号	必填,唯一
        private String reference_no	        ;//string(32)	Option	参考号	客户的订单
        private Integer order_status	    ;//int	Require	订单状态	必填,对应枚举 0：未出库; 1：已出库
        private String tracking_number	    ;//string(50)	Require	跟踪号
        private String sm_code	            ;//string(64)	Require	物流产品Code
        private String add_time	            ;//Datetime	Require	创建时间	北京时间
        private Integer sc_id	            ;//Int	Require	渠道id
        private Integer warehouse_id	    ;//Int	Require	仓库id
        private String outStock_time	    ;//Datetime	Option	签出时间	北京时间
        private BigDecimal so_weight	    ;//decimal(10,3)	Require	预估重量
        private BigDecimal so_shipping_fee	;//decimal(10,2)	Require	运输费
        private String item	                ;//Object	Option	订单明细
        private String fee_details	        ;//Object	Option	费用明细
        private String orderBoxInfo	        ;//Object	Option	箱子明细	一票多箱


}
