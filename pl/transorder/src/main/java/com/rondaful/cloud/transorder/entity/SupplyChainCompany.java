package com.rondaful.cloud.transorder.entity;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-03-15 18:09
 * 包名: com.rondaful.cloud.order.entity
 * 描述: 接收用户服务返回的信息
 */
public class SupplyChainCompany {
    private String supplyId;
    private String supplyChainCompanyName;
    private String userId;

    public SupplyChainCompany() {
    }

    public SupplyChainCompany(String supplyId, String supplyChainCompanyName, String userId) {

        this.supplyId = supplyId;
        this.supplyChainCompanyName = supplyChainCompanyName;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SupplyChainCompany{" +
                "supplyId='" + supplyId + '\'' +
                ", supplyChainCompanyName='" + supplyChainCompanyName + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public String getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(String supplyId) {
        this.supplyId = supplyId;
    }

    public String getSupplyChainCompanyName() {
        return supplyChainCompanyName;
    }

    public void setSupplyChainCompanyName(String supplyChainCompanyName) {
        this.supplyChainCompanyName = supplyChainCompanyName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
