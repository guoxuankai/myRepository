package com.brandslink.cloud.finance.controller;

import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.finance.pojo.dto.CustomerDto;
import com.brandslink.cloud.finance.pojo.vo.CustomerAddVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerRechargeVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerVo;
import com.brandslink.cloud.finance.service.CustomerService;
import com.brandslink.cloud.finance.utils.FinanceCommonUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangzefei
 * @Classname CustomerController
 * @Description 客户帐号控制层
 * @Date 2019/8/29 17:37
 */
@RestController
@RequestMapping("/customer")
public class CustomerController extends BaseController {

    @Resource
    private CustomerService customerService;

    @Resource
    private FinanceCommonUtil financeCommonUtil;

    @GetMapping("/getList")
    @ApiOperation(value = "获取客户列表", notes = "客户列表")
    public Page<CustomerDto> getList(CustomerVo param){
        Page.builder(param.getPageNum(),param.getPageSize());
        List<CustomerDto> list=customerService.getList(param);
        return new Page(new PageInfo(list));
    }

    /**
     * 获取客户信息
     * @param customerCode 客户编码
     * @return
     */
    @GetMapping("/get")
    @ApiOperation(value = "获取单个客户信息", notes = "单个客户信息")
    public CustomerDto get(@ApiParam(value = "客户编码",required = true)@RequestParam("customerCode") String customerCode){
        return customerService.get(customerCode);
    }

    /**
     * 客户获取自己的信息
     * @return
     */
    @GetMapping("/getSelf")
    @ApiOperation(value = "客户获取自己的信息", notes = "客户获取自己的信息")
    public CustomerDto getSelf(){
        String customerCode= financeCommonUtil.getCustomerCode();
        return customerService.get(customerCode);
    }

    /**
     * 新增客户
     */
    @PostMapping("/add")
    @ApiOperation(value = "新增客户", notes = "新增客户")
    public void add(CustomerAddVo param){
        customerService.add(param);
    }

    /**
     * 客户充值
     */
    @PostMapping("/recharge")
    @ApiOperation(value = "客户充值", notes = "客户充值")
    public void recharge(@RequestBody CustomerRechargeVo param){
       customerService.recharge(param);
    }
}
