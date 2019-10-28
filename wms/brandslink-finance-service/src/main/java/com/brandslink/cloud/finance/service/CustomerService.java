package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.dto.CustomerDto;
import com.brandslink.cloud.finance.pojo.dto.CustomerFlowDto;
import com.brandslink.cloud.finance.pojo.dto.CustomerSelfFlowDto;
import com.brandslink.cloud.finance.pojo.entity.Customer;
import com.brandslink.cloud.finance.pojo.entity.CustomerFlow;
import com.brandslink.cloud.finance.pojo.vo.CustomerAddVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerFlowVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerRechargeVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerVo;

import java.util.List;

/**
 * @author yangzefei
 * @Classname CustomerService
 * @Description 客户帐号信息
 * @Date 2019/8/26 10:07
 */
public interface CustomerService extends BaseService<Customer> {


    CustomerDto get(String customerCode);

    List<CustomerDto> getList(CustomerVo param);

    void add(CustomerAddVo param);

    void recharge(CustomerRechargeVo param);
}
