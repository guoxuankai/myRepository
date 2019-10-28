package com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe;

import lombok.Data;

@Data
public class GoodCangBackOrderVo {

    private String order_code;        //订单号
    private String reference_no;      //参考号
    private String tracking_number;   //跟踪号
    private String sm_code;           //物流产品Code
    private String error_code;        //错误代码
    private String error_message;     //错误信息
    private String error_time;         //错误发生时间
}
