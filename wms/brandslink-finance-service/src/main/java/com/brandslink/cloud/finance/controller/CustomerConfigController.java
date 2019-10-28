package com.brandslink.cloud.finance.controller;

import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.finance.pojo.dto.CustomerConfig.QueryCustomerConfigDto;
import com.brandslink.cloud.finance.pojo.dto.CustomerConfig.SelectCustomerDto;
import com.brandslink.cloud.finance.pojo.entity.CustomerConfigEntity;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.AddCustomerConfigVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.EditorCustomerVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.EffectiveCstomerVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerConfig.QueryCustomerConfigVo;
import com.brandslink.cloud.finance.service.CustomerConfigService;
import com.brandslink.cloud.finance.utils.VersionsCreateUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * .
 *
 * @title :客户报价
 * @Author: zhangjinhua
 * @Date: 2019/8/19 11:42
 */
@Slf4j
@RestController
@RequestMapping("/customerConfig")
@Api("客户报价")
public class CustomerConfigController extends BaseController {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    CustomerConfigService customerConfigService;
    @Autowired
    GetUserDetailInfoUtil getUserDetailInfoUtil;

    @ApiOperation(value = "新增客户报价", notes = "新增客户报价")
    @RequestMapping(value = "/addQuote", method = RequestMethod.POST)
    public String addQuote(@RequestBody AddCustomerConfigVo customerConfig) {
        String version = VersionsCreateUtil.orderProduceDate(redisTemplate, customerConfig.getCustomerName());
        customerConfig.setVersion(version);
        customerConfig.setCreateBy(getUserDetailInfoUtil.getUserDetailInfo().getName());
        customerConfigService.addCustomerConfig(customerConfig);
        return "增加成功";
    }

    @ApiOperation(value = "修改客户报价", notes = "修改客户报价")
    @RequestMapping(value = "/editorCustomerConfig", method = RequestMethod.POST)
    public String editorCustomerConfig(@RequestBody EditorCustomerVo editorCustomer) {
        int flag = customerConfigService.editorCustomerConfig(editorCustomer);
        if (flag == 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "状态不等于待提交，不可编辑");
        }
        return "修改成功";
    }

    @ApiOperation(value = "客户报价列表", notes = "客户报价列表")
    @RequestMapping(value = "/queryQuote", method = RequestMethod.POST)
    public PageInfo<QueryCustomerConfigDto> queryQuote(@RequestBody QueryCustomerConfigVo queryCustomer) {
        PageInfo<QueryCustomerConfigDto> configEntityPageInfo = customerConfigService.queryQuote(queryCustomer);
        return configEntityPageInfo;
    }

    @ApiOperation(value = "获取所有客户", notes = "获取所有客户")
    @RequestMapping(value = "/selectClient", method = RequestMethod.POST)
    public List<SelectCustomerDto> selectCustomer() {
        return customerConfigService.selectCustomer();
    }


    @ApiOperation(value = "生效", notes = "生效")
    @RequestMapping(value = "/customerEffective", method = RequestMethod.POST)
    public String customerEffective(EffectiveCstomerVo effectiveCstomer) {
        int flag = customerConfigService.customerSubmit(effectiveCstomer.getId());
        if (flag == 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "更新为提交状态失败！");
        }
        effectiveCstomer.setUpdateBy(getUserDetailInfoUtil.getUserDetailInfo().getName());
        customerConfigService.customerEffective(effectiveCstomer);

        return "提交保存成功!";
    }

    @ApiOperation(value = "客户报价详情", notes = "客户报价详情")
    @RequestMapping(value = "/getCustomerInfo", method = RequestMethod.POST)
    public CustomerConfigEntity getCustomerInfo(String version) {
        CustomerConfigEntity configEntityPageInfo = customerConfigService.getCustomerInfo(version);
        return configEntityPageInfo;
    }

}
