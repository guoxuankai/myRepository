package com.rondaful.cloud.seller.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "订单规则优先级交换对象")
public class OrderRuleSort implements Serializable {

    @ApiModelProperty(value = "(将优先级 置顶或者置尾 / 上移或下移 时使用)订单规则id")
    private Long id;

    @ApiModelProperty(value = "(将优先级 置顶或者置尾 / 上移或下移 时使用)操作方法[top:置顶/上移  tail:置尾/下移 ]")
    private String way;

    @ApiModelProperty(value = "(交换两个优先级时使用)用来交换的第一个规则的id")
    private Long id1;

    @ApiModelProperty(value = "(交换两个优先级时使用)用来交换的第二个规则的id")
    private Long id2;

    public Long getId1() {
        return id1;
    }

    public void setId1(Long id1) {
        this.id1 = id1;
    }

    public Long getId2() {
        return id2;
    }

    public void setId2(Long id2) {
        this.id2 = id2;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }
}
