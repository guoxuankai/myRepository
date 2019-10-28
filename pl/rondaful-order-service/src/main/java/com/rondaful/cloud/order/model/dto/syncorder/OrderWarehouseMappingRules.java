package com.rondaful.cloud.order.model.dto.syncorder;

import com.rondaful.cloud.order.entity.orderRule.OrderRuleWithBLOBs;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单仓库物流匹配规则
 *
 * @author Blade
 * @date 2019-07-17 16:46:23
 **/
public class OrderWarehouseMappingRules {
    // 卖家 仓库规则列表
    private List<OrderRuleWithBLOBs> sellerWarehouseRuleList;
    // 卖家 物流(邮寄方式)规则列表
    private List<OrderRuleWithBLOBs> sellerMailRuleList;
    // 管理后台 仓库规则列表
    private List<OrderRuleWithBLOBs> cmsWarehouseRuleList;
    // 管理后台 物流(邮寄方式)列表
    private List<OrderRuleWithBLOBs> cmsMailRuleList;

    public List<OrderRuleWithBLOBs> getSellerWarehouseRuleList() {
        return sellerWarehouseRuleList;
    }

    public void setSellerWarehouseRuleList(List<OrderRuleWithBLOBs> sellerWarehouseRuleList) {
        this.sellerWarehouseRuleList = sellerWarehouseRuleList;
    }

    public List<OrderRuleWithBLOBs> getSellerMailRuleList() {
        return sellerMailRuleList;
    }

    public void setSellerMailRuleList(List<OrderRuleWithBLOBs> sellerMailRuleList) {
        this.sellerMailRuleList = sellerMailRuleList;
    }

    public List<OrderRuleWithBLOBs> getCmsWarehouseRuleList() {
        return cmsWarehouseRuleList;
    }

    public void setCmsWarehouseRuleList(List<OrderRuleWithBLOBs> cmsWarehouseRuleList) {
        this.cmsWarehouseRuleList = cmsWarehouseRuleList;
    }

    public List<OrderRuleWithBLOBs> getCmsMailRuleList() {
        return cmsMailRuleList;
    }

    public void setCmsMailRuleList(List<OrderRuleWithBLOBs> cmsMailRuleList) {
        this.cmsMailRuleList = cmsMailRuleList;
    }
}
