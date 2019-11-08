package com.brandslink.cloud.common.entity;

import java.io.Serializable;

/**
 * 客户详细信息
 *
 * @ClassName CustomerDetails
 * @Author tianye
 * @Date 2019/8/30 14:59
 * @Version 1.0
 */
public class CustomerDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户信息
     */
    private CustomerInfoEntity customerInfoEntity;

    /**
     * 账户信息
     */
    private CustomerUserDetailInfo customerUserDetailInfo;

    public CustomerInfoEntity getCustomerInfoEntity() {
        return customerInfoEntity;
    }

    public void setCustomerInfoEntity(CustomerInfoEntity customerInfoEntity) {
        this.customerInfoEntity = customerInfoEntity;
    }

    public CustomerUserDetailInfo getCustomerUserDetailInfo() {
        return customerUserDetailInfo;
    }

    public void setCustomerUserDetailInfo(CustomerUserDetailInfo customerUserDetailInfo) {
        this.customerUserDetailInfo = customerUserDetailInfo;
    }
}
