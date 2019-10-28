package com.brandslink.cloud.user.controller;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.entity.request.CustomerShipperDetailRequestDTO;
import com.brandslink.cloud.user.dto.request.AddOrUpdateShipperRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerForAuditInfoRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerForBasicInfoRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerSignInRequestDTO;
import com.brandslink.cloud.user.dto.response.CodeAndNameResponseDTO;
import com.brandslink.cloud.user.dto.response.CustomerInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.CustomerShipperDetailResponseDTO;
import com.brandslink.cloud.user.dto.response.DeveloperCentreInfoResponseDTO;
import com.brandslink.cloud.user.entity.CustomerInfo;
import com.brandslink.cloud.user.entity.ShipperInfo;
import com.brandslink.cloud.user.service.ICustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 客户
 *
 * @ClassName CustomerController
 * @Author tianye
 * @Date 2019/7/16 11:24
 * @Version 1.0
 */
@RestController
@Api("客户相关接口")
@RequestMapping(value = "/customer")
public class CustomerController {

    @Resource
    private ICustomerService customerService;

    @ApiOperation("客户注册")
    @PostMapping("/signIn")
    @RequestRequire(require = "shortenedName,name,contactWay,password,authCode", parameter = CustomerSignInRequestDTO.class)
    public String signIn(@RequestBody CustomerSignInRequestDTO request) {
        return customerService.insertSignIn(request);
    }

    @ApiOperation("获取验证码")
    @PostMapping("/getAuthCode")
    public void getAuthCode(@ApiParam(name = "mobile", value = "手机号", required = true) @RequestParam("mobile") String mobile,
                            @ApiParam(name = "type", value = "0：更改手机号 1：手机号登录 2：客户注册", required = true) @RequestParam(value = "type") Integer type) {
        customerService.getAuthCode(mobile, type);
    }

    @ApiOperation("更改手机号")
    @PostMapping("/updatePhone")
    public void updatePhone(@ApiParam(name = "mobile", value = "手机号", required = true) @RequestParam("mobile") String mobile,
                            @ApiParam(name = "authCode", value = "验证码", required = true) @RequestParam(value = "authCode") String authCode) {
        customerService.updatePhone(mobile, authCode);
    }

    @ApiOperation("获取客户信息")
    @GetMapping("/getCustomer")
    public CustomerInfoResponseDTO getCustomer(@ApiParam(name = "id", value = "oms系统传账号id，其他系统传客户id", required = true) @RequestParam("id") Integer id) {
        return customerService.getCustomer(id);
    }

    @ApiOperation("编辑客户->基本信息")
    @PostMapping("/updateCustomerForBasicInfo")
    @RequestRequire(require = "id,shortenedChineseName,chineseName,contacts,provincial,address", parameter = CustomerForBasicInfoRequestDTO.class)
    public void updateCustomerForBasicInfo(@RequestBody CustomerForBasicInfoRequestDTO request) {
        customerService.updateCustomerForBasicInfo(request);
    }

    @ApiOperation("编辑客户->审核信息")
    @PostMapping("/updateCustomerForAuditInfo")
    @RequestRequire(require = "id,legalRepresentative,legalRepresentativeIdentityCard,businessLicense,identityCardFront,identityCardVerso", parameter = CustomerForAuditInfoRequestDTO.class)
    public void updateCustomerForAuditInfo(@RequestBody CustomerForAuditInfoRequestDTO request) {
        customerService.updateCustomerForAuditInfo(request);
    }

    @ApiOperation("提交审核信息")
    @GetMapping("/commitAuditInfo")
    public void commitAuditInfo() {
        customerService.commitAuditInfo();
    }

    @ApiOperation("新增货主")
    @PostMapping("/addShipper")
    public void addShipper(@RequestBody AddOrUpdateShipperRequestDTO request) {
        customerService.addShipper(request);
    }

    @ApiOperation("编辑货主")
    @PostMapping("/updateShipper")
    public void updateShipper(@RequestBody AddOrUpdateShipperRequestDTO request) {
        customerService.updateShipper(request);
    }

    @ApiOperation("获取当前登录用户货主编码和名称 -> oms系统")
    @GetMapping("/oms/getShipperCodeAndName")
    public List<CodeAndNameResponseDTO> getShipperCodeAndNameForOMS() {
        return customerService.getShipperCodeAndNameForOMS();
    }

    @ApiOperation("获取开发者中心信息")
    @GetMapping("/getDeveloperCentreInfo")
    public DeveloperCentreInfoResponseDTO getDeveloperCentreInfo() {
        return customerService.getDeveloperCentreInfo();
    }

    @ApiOperation("获取客户列表")
    @GetMapping("/getCustomerList")
    public Page<CustomerInfo> getCustomerList(@ApiParam(name = "page", value = "页码", required = true) @RequestParam("page") Integer page,
                                              @ApiParam(name = "row", value = "每页显示行数", required = true) @RequestParam(value = "row") Integer row,
                                              @ApiParam(name = "customerCode", value = "客户编码") @RequestParam(value = "customerCode", required = false) String customerCode,
                                              @ApiParam(name = "customerName", value = "客户名称(中文)") @RequestParam(value = "customerName", required = false) String customerName,
                                              @ApiParam(name = "status", value = "客户状态 0：正常 1：作废") @RequestParam(value = "status", required = false) String status,
                                              @ApiParam(name = "auditStatus", value = "审核状态 0：待提交 1：待审核 2：审核通过 3：审核不通过") @RequestParam(value = "auditStatus", required = false) String auditStatus) {
        Page.builder(page, row);
        return customerService.getCustomerList(customerCode, customerName, status, auditStatus);
    }

    @ApiOperation("新增客户")
    @PostMapping("/addCustomer")
    public void addCustomer(@RequestBody CustomerForBasicInfoRequestDTO request) {
        customerService.addCustomer(request);
    }

    @ApiOperation("获取首页信息")
    @GetMapping("/getHomePageInfo")
    public Map<String, String> getHomePageInfo() {
        return customerService.getHomePageInfo();
    }

    @ApiOperation("审核操作")
    @GetMapping("/audit")
    public void audit(@ApiParam(name = "id", value = "客户id", required = true) @RequestParam("id") Integer id,
                      @ApiParam(name = "auditStatus", value = "审核状态 2：审核通过 3：审核不通过", required = true) @RequestParam(value = "auditStatus") Integer auditStatus,
                      @ApiParam(name = "auditFailedCause", value = "审核不通过原因") @RequestParam(value = "auditFailedCause", required = false) String auditFailedCause) {
        customerService.updateAudit(id, auditStatus, auditFailedCause);
    }

    @ApiOperation("根据客户编码获取客户信息")
    @GetMapping("/getCustomerByCustomerCode")
    public CustomerInfoResponseDTO getCustomerByCustomerCode(@ApiParam(name = "customerCode", value = "客户编码", required = true) @RequestParam("customerCode") String customerCode) {
        return customerService.getCustomerByCustomerCode(customerCode);
    }


    /**
     * ==========================================================================
     */


    @ApiOperation("根据客户id查询货主列表 -> 内部调用")
    @GetMapping("/getShipperByCustomerId")
    public List<ShipperInfo> getShipperByCustomerId(@ApiParam(name = "customerId", value = "客户编码", required = true) @RequestParam("customerId") Integer customerId) {
        return customerService.getShipperByCustomerId(customerId);
    }

    @ApiOperation("获取客户名称以及货主名称  -> 内部调用")
    @PostMapping("/getCustomerShipperDetail")
    public List<CustomerShipperDetailResponseDTO> getCustomerShipperDetail(@RequestBody List<CustomerShipperDetailRequestDTO> list) {
        return customerService.getCustomerShipperDetail(list);
    }

    @ApiOperation("获取货主名称  -> 内部调用")
    @PostMapping("/getShipperDetail")
    public List<CodeAndNameResponseDTO> getShipperDetail(@RequestBody List<String> shipperCodeList) {
        return customerService.getShipperDetail(shipperCodeList);
    }

    @ApiOperation("获取客户编码和名称")
    @GetMapping("/getCustomerCodeAndName")
    public List<CodeAndNameResponseDTO> getCustomerCodeAndName() {
        return customerService.getCustomerCodeAndName();
    }

    @ApiOperation("获取货主编码和名称")
    @GetMapping("/getShipperCodeAndName")
    public List<CodeAndNameResponseDTO> getShipperCodeAndName(String customerCode) {
        return customerService.getShipperCodeAndName(customerCode);
    }
}
