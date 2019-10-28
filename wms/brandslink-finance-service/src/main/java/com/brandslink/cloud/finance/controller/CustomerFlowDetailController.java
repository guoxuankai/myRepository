package com.brandslink.cloud.finance.controller;

import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.finance.pojo.dto.CustomerFlowDetailDto;
import com.brandslink.cloud.finance.pojo.dto.CustomerFlowDto;
import com.brandslink.cloud.finance.pojo.vo.CustomerFlowVo;
import com.brandslink.cloud.finance.service.CustomerFlowDetailService;
import com.brandslink.cloud.finance.utils.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yangzefei
 * @Classname CustomerFlowDetailController
 * @Description 客户流水详情
 * @Date 2019/8/29 11:03
 */
@RestController
@RequestMapping("/customerFlowDetail")
public class CustomerFlowDetailController extends BaseController {

    private CustomerFlowDetailService customerFlowDetailService;

    @ApiOperation(value = "交易流水详情导出",notes = "交易流水详情导出")
    @GetMapping("/export")
    public void export(@RequestParam("customerFlowId") Integer customerFlowId){
        customerFlowDetailService.export(customerFlowId,response);
    }
}
