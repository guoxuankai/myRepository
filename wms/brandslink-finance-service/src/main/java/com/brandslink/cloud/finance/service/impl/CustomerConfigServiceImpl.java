package com.brandslink.cloud.finance.service.impl;

import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.mapper.CustomerConfigMapper;
import com.brandslink.cloud.finance.pojo.dto.CustomerConfig.*;
import com.brandslink.cloud.finance.pojo.entity.CustomerConfigEntity;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.AddCustomerConfigVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.EditorCustomerVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.EffectiveCstomerVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.QueryCustomerConfigVo;
import com.brandslink.cloud.finance.service.CustomerConfigService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zhangjinhua
 * @Date: 2019/8/20 11:44
 */
@Service
public class CustomerConfigServiceImpl extends BaseServiceImpl<CustomerConfigEntity> implements CustomerConfigService {
    @Autowired
    CustomerConfigMapper quoteMapper;
    @Override
    public int addCustomerConfig(AddCustomerConfigVo customerConfig) {
       return quoteMapper.addCustomerConfig(customerConfig);

    }

    @Override
    public List<SelectCustomerDto> selectCustomer() {
        return quoteMapper.selectCustomer();

    }

    @Override
    public PageInfo<QueryCustomerConfigDto> queryQuote(QueryCustomerConfigVo queryCustomer) {
       return new PageInfo<QueryCustomerConfigDto>(quoteMapper.queryQuote(queryCustomer));
    }

    @Override
    public int editorCustomerConfig(EditorCustomerVo editorCustomer) {
        return quoteMapper.editorCustomerConfig(editorCustomer);
    }

    @Override
    public int customerSubmit(Integer id) {
        return quoteMapper.customerSubmit(id);
    }

    @Override
    public Integer customerEffective(EffectiveCstomerVo effectiveCstomer) {
        Integer flag = quoteMapper.judgeEffective(effectiveCstomer);
        if (flag != null && flag > 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当前客户的生效日期已存在重复数据，不可保存！");
        }

       return quoteMapper.customerEffective(effectiveCstomer);

    }

    @Override
    public CustomerConfigEntity getCustomerInfo(String version) {

        return quoteMapper.getCustomerInfo(version);
    }
}
