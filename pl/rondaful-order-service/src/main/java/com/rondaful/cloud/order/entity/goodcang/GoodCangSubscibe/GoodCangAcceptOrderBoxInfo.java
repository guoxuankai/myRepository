package com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe;

import lombok.Data;

@Data
public class GoodCangAcceptOrderBoxInfo {

    private String box_no	        ;//string(32)	Require	箱号
    private Integer ob_qty	        ;//Int	Require	数量
    private String ob_length	    ;//decimal(10,2)	Require	长
    private String ob_width	        ;//decimal(10,2)	Require	宽
    private String ob_height	    ;//decimal(10,2)	Require	高
    private String ob_weight	    ;//decimal(10,2)	Require	重量
    private String tracking_number	;//string(64)	Require	跟踪号
}
