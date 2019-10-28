package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.dto.CustomerConfig.*;
import com.brandslink.cloud.finance.pojo.entity.CustomerConfigEntity;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.AddCustomerConfigVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.EditorCustomerVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.EffectiveCstomerVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.QueryCustomerConfigVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author: zhangjinhua
 * @Date: 2019/8/20 11:40
 */
public interface CustomerConfigService extends BaseService<CustomerConfigEntity> {

    /**
     * 增加
     * @param customerConfig
     * @return
     */
    int addCustomerConfig(AddCustomerConfigVo customerConfig);

    /**
     * 获取客户列表
     * @return
     */
    List<SelectCustomerDto> selectCustomer();

    /**
     * 分页查询报价列表
     * @param queryCustomer
     * @return
     */
    PageInfo<QueryCustomerConfigDto> queryQuote(QueryCustomerConfigVo queryCustomer);

    int editorCustomerConfig(EditorCustomerVo editorCustomer);

    int customerSubmit(Integer id);

    Integer customerEffective(EffectiveCstomerVo effectiveCstomer);

    /**
     * 根据版本号获取报价详情
     * @param version
     * @return
     */
    CustomerConfigEntity getCustomerInfo(String version);
}
