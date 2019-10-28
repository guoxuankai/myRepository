package com.brandslink.cloud.logistics.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.logistics.VO.*;
import com.brandslink.cloud.logistics.VO.MethodZoneFreightVO;
import com.brandslink.cloud.logistics.model.*;
import com.brandslink.cloud.logistics.service.*;
import com.brandslink.cloud.logistics.utils.BeanConvertorUtils;
import com.brandslink.cloud.logistics.utils.UserUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/basic")
public class BasicDataController {
    @Autowired
    private ILogisticsProviderService providerService;
    @Autowired
    private IPlatformInfoService platformInfoService;
    @Autowired
    private ILogisticsMethodAddressService addressService;
    @Autowired
    private ILogisticsCargoPropService cargoPropService;
    @Autowired
    private ILogisticsCollectorService collectorService;
    @Autowired
    private ILogisticsMethodService methodService;
    @Autowired
    private IInvoiceSpecificationService specificationService;
    @Autowired
    private IMethodZoneFreightService zoneService;
    @Autowired
    private IMethodZoneCountryService zoneCountryService;
    @Autowired
    private UserUtil userUtil;

    private final static Logger _log = LoggerFactory.getLogger(BasicDataController.class);

    @PostMapping("/editLogisticsProvider")
    @ApiOperation(value = "新增/更新物流商数据")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "providerVO", value = "物流商数据对象", required = true, dataType = "LogisticsProviderVO")})
    public Long editLogisticsProvider(@RequestBody @Valid LogisticsProviderVO providerVO) {
        LogisticsProviderModel providerModel = BeanConvertorUtils.convertBean(providerVO, LogisticsProviderModel.class);
        providerModel.setCreateBy(userUtil.getUserName());
        providerModel.setUpdateBy(userUtil.getUserName());
        return providerService.editLogisticsProvider(providerModel);
    }

    @GetMapping("/selectLogisticsProvider")
    @ApiOperation(value = "查询物流商数据")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    public Page<LogisticsProviderModel> selectLogisticsProvider(String page, String row, LogisticsProviderVO providerVO) {
        LogisticsProviderModel providerModel = BeanConvertorUtils.convertBean(providerVO, LogisticsProviderModel.class);
        Page.builder(page, row);
        Page<LogisticsProviderModel> p = providerService.page(providerModel);
        return p;
    }

    @PostMapping("/editPlatform")
    @ApiOperation(value = "新增/更新平台信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "platformInfoVO", value = "平台数据对象", required = true, dataType = "PlatformInfoVO")})
    public Long editPlatform(@RequestBody @Valid PlatformInfoVO platformInfoVO) {
        PlatformInfoModel platformInfoModel = BeanConvertorUtils.convertBean(platformInfoVO, PlatformInfoModel.class);
        platformInfoModel.setCreateBy(userUtil.getUserName());
        platformInfoModel.setUpdateBy(userUtil.getUserName());
        return platformInfoService.editPlatform(platformInfoModel);
    }

    @GetMapping("/selectPlatform")
    @ApiOperation(value = "查询平台信息")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    public Page<PlatformInfoModel> selectPlatform(String page, String row, PlatformInfoVO platformInfoVO) {
        PlatformInfoModel platformInfoModel = BeanConvertorUtils.convertBean(platformInfoVO, PlatformInfoModel.class);
        Page.builder(page, row);
        Page<PlatformInfoModel> p = platformInfoService.page(platformInfoModel);
        return p;
    }

    @PostMapping("/editMethodAddress")
    @ApiOperation(value = "新增/更新邮寄方式地址信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "addressVO", value = "邮寄方式人员地址数据对象", required = true, dataType = "LogisticsMethodAddressVO")})
    public Long editMethodAddress(@RequestBody @Valid LogisticsMethodAddressVO addressVO) {
        LogisticsMethodAddressModel addressModel = BeanConvertorUtils.convertBean(addressVO, LogisticsMethodAddressModel.class);
        addressModel.setCreateBy(userUtil.getUserName());
        addressModel.setUpdateBy(userUtil.getUserName());
        return addressService.editMethodAddress(addressModel);
    }

    @GetMapping("/selectMethodAddress")
    @ApiOperation(value = "查询邮寄方式地址信息")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    public Page<LogisticsMethodAddressModel> selectMethodAddress(String page, String row, LogisticsMethodAddressVO addressVO) {
        LogisticsMethodAddressModel addressModel = BeanConvertorUtils.convertBean(addressVO, LogisticsMethodAddressModel.class);
        Page.builder(page, row);
        Page<LogisticsMethodAddressModel> p = addressService.page(addressModel);
        return p;
    }

    @PostMapping("/editCargoProp")
    @ApiOperation(value = "新增/更新物流货物属性数据")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "cargoPropVO", value = "物流货物属性数据对象", required = true, dataType = "LogisticsCargoPropVO")})
    public Long editCargoProp(@RequestBody @Valid LogisticsCargoPropVO cargoPropVO) {
        LogisticsCargoPropModel cargoPropModel = BeanConvertorUtils.convertBean(cargoPropVO, LogisticsCargoPropModel.class);
        cargoPropModel.setCreateBy(userUtil.getUserName());
        cargoPropModel.setUpdateBy(userUtil.getUserName());
        return cargoPropService.editCargoProp(cargoPropModel);
    }

    @GetMapping("/selectCargoProp")
    @ApiOperation(value = "查询物流货物属性数据")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    public Page<LogisticsCargoPropModel> selectCargoProp(String page, String row, LogisticsCargoPropVO cargoPropVO) {
        LogisticsCargoPropModel cargoPropModel = BeanConvertorUtils.convertBean(cargoPropVO, LogisticsCargoPropModel.class);
        Page.builder(page, row);
        Page<LogisticsCargoPropModel> p = cargoPropService.page(cargoPropModel);
        return p;
    }

    @PostMapping("/editCollector")
    @ApiOperation(value = "新增/更新物流揽收商数据")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "collectorVO", value = "物流揽收商数据对象", required = true, dataType = "LogisticsCollectorVO")})
    public Long editcollector(@RequestBody @Valid LogisticsCollectorVO collectorVO) {
        LogisticsCollectorModel collectorModel = JSONObject.parseObject(JSON.toJSONString(collectorVO), LogisticsCollectorModel.class);
        collectorModel.setCreateBy(userUtil.getUserName());
        collectorModel.setUpdateBy(userUtil.getUserName());
        return collectorService.editCollector(collectorModel);
    }

    @GetMapping("/selectCollector")
    @ApiOperation(value = "查询物流揽收商数据")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    public Page<LogisticsCollectorModel> selectcollector(String page, String row, LogisticsCollectorVO collectorVO) {
        LogisticsCollectorModel collectorModel = JSONObject.parseObject(JSON.toJSONString(collectorVO), LogisticsCollectorModel.class);
        Page.builder(page, row);
        Page<LogisticsCollectorModel> p = collectorService.selectCollector(collectorModel);
        return p;
    }

    @GetMapping("/selectMethodListByCollectorId")
    @ApiOperation(value = "根据揽收商ID查询邮寄方式列表")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    public Page<CollectorMethodModel> selectMethodListByCollectorId(String page, String row, Long collectorId) {
        Page.builder(page, row);
        Page<CollectorMethodModel> p = collectorService.selectMethodListByCollectorId(collectorId);
        return p;
    }

    @PostMapping("/editMethod")
    @ApiOperation(value = "新增/更新邮寄方式数据")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "methodVO", value = "邮寄方式数据对象", required = true, dataType = "LogisticsMethodVO")})
    public Long editMethod(@RequestBody @Valid LogisticsMethodVO methodVO) {
        LogisticsMethodModel methodModel = JSONObject.parseObject(JSON.toJSONString(methodVO), LogisticsMethodModel.class);
        methodModel.setCreateBy(userUtil.getUserName());
        methodModel.setUpdateBy(userUtil.getUserName());
        return methodService.editMethod(methodModel);
    }

    @GetMapping("/selectMethod")
    @ApiOperation(value = "查询物流方式基本信息")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    public Page<LogisticsMethodModel> selectMethod(String page, String row, LogisticsMethodVO methodVO) {
        LogisticsMethodModel methodModel = JSONObject.parseObject(JSON.toJSONString(methodVO), LogisticsMethodModel.class);
        Page.builder(page, row);
        Page<LogisticsMethodModel> p = methodService.selectMethod(methodModel);
        return p;
    }

    @GetMapping("/selectMethodListByProviderID")
    @ApiOperation(value = "根据物流商ID查询物流方式列表")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "providerId", value = "物流商ID", dataType = "long", paramType = "query", required = true)})
    public Page<LogisticsMethodModel> selectMethodListByProviderID(String page, String row, @NotNull(message = "物流商ID不能为空") @Valid Long providerId) {
        Page.builder(page, row);
        LogisticsMethodModel methodModel = new LogisticsMethodModel();
        methodModel.setProviderId(providerId);
        Page<LogisticsMethodModel> p = methodService.page(methodModel);
        return p;
    }

    @GetMapping("/selectMethodBasicInfoByID")
    @ApiOperation(value = "根据物流商ID查询物流方式基本信息数据")
    @ApiImplicitParams({@ApiImplicitParam(name = "methodId", value = "邮寄方式ID", dataType = "long", paramType = "query", required = true)})
    public LogisticsMethodModel selectMethodBasicInfoByID(@NotNull(message = "邮寄方式ID不能为空") @Valid Long methodId) {
        return methodService.selectMethodBasicInfoByID(methodId);
    }

    @PostMapping("/enableDisableMethod")
    @ApiOperation(value = "启用/停用邮寄方式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "methodId", value = "邮寄方式ID", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "isValid", value = "启用停用类型", dataType = "byte", paramType = "query", required = true)})
    public void enableDisableMethod(@RequestParam @NotNull(message = "邮寄方式ID不能为空") Long methodId, @RequestParam @NotNull(message = "启用停用类型不能为空") Byte isValid) {
        methodService.enableDisableMethod(methodId, isValid);
    }

    @GetMapping("/selectMethodZoneList")
    @ApiOperation(value = "查询物流方式分区列表")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    public Page<MethodZoneFreightModel> selectMethodZoneList(String page, String row, Long methodId) {
        Page.builder(page, row);
        Page<MethodZoneFreightModel> p = zoneService.selectMethodZoneList(methodId);
        return p;
    }

    @GetMapping("/selectZoneByID")
    @ApiOperation(value = "查询物流方式分区详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "zoneId", value = "分区ID", dataType = "long", paramType = "query", required = true)})
    public MethodZoneFreightModel selectZoneByID(@RequestParam @NotNull(message = "分区ID不能为空") Long zoneId) {
        MethodZoneFreightModel zoneModel = zoneService.selectZoneByID(zoneId);
        return zoneModel;
    }

    @GetMapping("/selectZoneIDByMethodIDCountry")
    @ApiOperation(value = "根据邮寄方式ID和国家编码查询分区ID")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "methodId", value = "邮寄方式ID", dataType = "long", paramType = "query", required = true)})
    public List<MethodZoneCountryModel> selectZoneIDByMethodIDCountry(@RequestParam @NotNull(message = "邮寄方式ID不能为空") Long methodId,
                                                                 @ApiParam(value = "国家编码集合", required = true) @RequestParam("countryList") @NotEmpty(message = "国家编码集合不能为空") String[] countryArray) {
        return zoneService.selectZoneIDByMethodIDCountry(methodId, countryArray);
    }

    @PostMapping("/editMethodZone")
    @ApiOperation(value = "新增/更新邮寄方式时效运费数据")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "zoneVO", value = "邮寄方式分区时效运费数据对象", required = true, dataType = "MethodZoneFreightVO")})
    public Long editMethodZoneFreight(@RequestBody @NotNull(message = "邮寄方式分区时效运费数据对象不能为空") @Valid MethodZoneFreightVO zoneVO) throws Exception {
        MethodZoneFreightModel zoneModel = JSONObject.parseObject(JSON.toJSONString(zoneVO), MethodZoneFreightModel.class);
        return zoneService.editMethodZoneFreight(zoneModel);
    }

    @PostMapping("/deleteZoneCountry")
    @ApiOperation(value = "删除分区中国家数据")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "countryId", value = "物流方式分区国家ID", required = true, dataType = "long")})
    public void deleteZoneCountry(@RequestBody @NotNull(message = "物流方式分区国家ID不能为空") Long countryId) {
        zoneCountryService.deleteZoneCountry(countryId);
    }

    @PostMapping("/deleteZoneByID")
    @ApiOperation(value = "删除分区数据")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "zoneId", value = "分区ID", required = true, dataType = "long")})
    public void deleteZoneByID(@RequestBody @NotNull(message = "分区ID不能为空") Long zoneId) {
        zoneService.deleteZoneByID(zoneId);
    }

    @PostMapping("/editInvoiceSpecification")
    @ApiOperation(value = "新增/更新物流面单规格数据")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "invoiceSpecification", value = "物流面单规格数据对象", required = true, dataType = "InvoiceSpecificationVO")})
    public Long editInvoiceSpecification(@RequestBody @Valid InvoiceSpecificationVO invoiceSpecificationVO) {
        InvoiceSpecificationModel specificationModel = BeanConvertorUtils.convertBean(invoiceSpecificationVO, InvoiceSpecificationModel.class);
        specificationModel.setCreateBy(userUtil.getUserName());
        specificationModel.setUpdateBy(userUtil.getUserName());
        return specificationService.editInvoiceSpecification(specificationModel);
    }

    @GetMapping("/selectInvoiceSpecification")
    @ApiOperation(value = "查询物流面单规格数据")
    @RequestRequire(require = "page, row", parameter = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    public Page<InvoiceSpecificationModel> selectInvoiceSpecification(String page, String row, InvoiceSpecificationVO invoiceSpecificationVO) {
        InvoiceSpecificationModel specificationModel = BeanConvertorUtils.convertBean(invoiceSpecificationVO, InvoiceSpecificationModel.class);
        Page.builder(page, row);
        Page<InvoiceSpecificationModel> p = specificationService.page(specificationModel);
        return p;
    }
}
