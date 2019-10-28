package com.brandslink.cloud.logistics.controller.logistics;

import com.brandslink.cloud.logistics.thirdLogistics.RemoteSunYouLogisticsService;
import com.brandslink.cloud.logistics.thirdLogistics.bean.SunYou.SunYouCommonBean;
import com.brandslink.cloud.logistics.thirdLogistics.bean.SunYou.SunYouPackage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("顺友物流接口")
@RestController
@RequestMapping("/sunyou")
public class LogisticsSunYouController {

    @Autowired
    private RemoteSunYouLogisticsService sunYouService;

    @PostMapping("/createAndConfirmPackages")
    @ApiOperation(value = "批量创建并预报包裹")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "list", value = "批量创建顺友物流并预报包裹", required = true, allowMultiple = true, dataType = "SunYouPackage")})
    public String createAndConfirmPackages(@RequestBody List<SunYouPackage> list) throws Exception {
        return sunYouService.createAndConfirmPackages(list);
    }

    @PostMapping("/getPackagesDetails")
    @ApiOperation(value = "批量获取包裹详情")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "sunYouCommonBean", value = "查询包裹详情请求参数对象", required = true, dataType = "SunYouCommonBean")})
    public String getPackagesDetails(@RequestBody SunYouCommonBean sunYouCommonBean) throws Exception {
        return sunYouService.getPackagesDetails(sunYouCommonBean.getSyOrderNoList(), sunYouCommonBean.getCustomerNoList());
    }

    @PostMapping("/getPackagesStatus")
    @ApiOperation(value = "批量获取包裹状态")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "sunYouCommonBean", value = "查询包裹状态请求参数对象", required = true, dataType = "SunYouCommonBean")})
    public String getPackagesStatus(@RequestBody SunYouCommonBean sunYouCommonBean) throws Exception {
        return sunYouService.getPackagesStatus(sunYouCommonBean.getSyOrderNoList(), sunYouCommonBean.getCustomerNoList());
    }

    @PostMapping("/getPackagesTrackingNumber")
    @ApiOperation(value = "批量获取包裹状态")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "sunYouCommonBean", value = "查询包裹跟踪号请求参数对象", required = true, dataType = "SunYouCommonBean")})
    public String getPackagesTrackingNumber(@RequestBody SunYouCommonBean sunYouCommonBean) throws Exception {
        return sunYouService.getPackagesTrackingNumber(sunYouCommonBean.getSyOrderNoList(), sunYouCommonBean.getCustomerNoList());
    }

    @PostMapping("/deletePackages")
    @ApiOperation(value = "批量获取包裹状态")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "sunYouCommonBean", value = "删除包裹请求参数对象", required = true, dataType = "SunYouCommonBean")})
    public String deletePackages(@RequestBody SunYouCommonBean sunYouCommonBean) throws Exception {
        return sunYouService.deletePackages(sunYouCommonBean.getSyOrderNoList(), sunYouCommonBean.getCustomerNoList());
    }

    @PostMapping("/getPackagesLabelVariables")
    @ApiOperation(value = "批量获取包裹面单变量")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "sunYouCommonBean", value = "获取包裹面单变量请求参数对象", required = true, dataType = "SunYouCommonBean")})
    public String getPackagesLabelVariables(@RequestBody SunYouCommonBean sunYouCommonBean) throws Exception {
        return sunYouService.getPackagesLabelVariables(sunYouCommonBean.getSyOrderNoList(), sunYouCommonBean.getCustomerNoList());
    }

    @PostMapping("/getPackagesLabel")
    @ApiOperation(value = "批量获取包裹状态")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "sunYouCommonBean", value = "删除包裹请求参数对象", required = true, dataType = "SunYouCommonBean")})
    public String getPackagesLabel(@RequestBody SunYouCommonBean sunYouCommonBean) throws Exception {
        return sunYouService.getPackagesLabel(sunYouCommonBean.getSyOrderNoList(), sunYouCommonBean.getCustomerNoList(), sunYouCommonBean.getPackMethod(), sunYouCommonBean.getDataFormat());
    }

    @PostMapping("/findShippingMethods")
    @ApiOperation(value = "批量获取包裹状态")
    public String findShippingMethods(String countryCode, String pickupCity, String postCode) throws Exception {
        return sunYouService.findShippingMethods(countryCode, pickupCity, postCode);
    }

    @PostMapping("/operationPackages")
    @ApiOperation(value = "批量修改预报重量")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "sunYouCommonBean", value = "修改预报重量请求参数对象", required = true, allowMultiple = true, dataType = "SunYouCommonBean")})
    public String operationPackages(@RequestBody List<SunYouCommonBean> list) throws Exception {
        return sunYouService.operationPackages(list);
    }
}
