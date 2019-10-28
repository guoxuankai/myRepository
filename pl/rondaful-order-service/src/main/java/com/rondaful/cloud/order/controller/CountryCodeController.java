package com.rondaful.cloud.order.controller;


import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.entity.CountryCode;
import com.rondaful.cloud.order.service.ICountryCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(description = "国家列表相关接口")
@RestController
@RequestMapping("/country")
public class CountryCodeController {

    private final Logger logger = LoggerFactory.getLogger(CountryCodeController.class);

    private final ICountryCodeService countryCodeService;

    @Autowired
    public CountryCodeController(ICountryCodeService countryCodeService) {
        this.countryCodeService = countryCodeService;
    }

    @GetMapping("/queryList")
    @ApiOperation("查询国家列表")
    public List<CountryCode> queryList(CountryCode code){
        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"本接口应产品要求不再提供服务，需要查询国家编码的去用户服务调用相关接口，详细情况请找夏情琼，或者咨询 Edward");


       /* try {
            List<CountryCode> countryCodes = countryCodeService.queryList(code);
            countryCodes.forEach(c -> c.setNameZh(Utils.translation(c.getNameZh())));
            return countryCodes;
        }catch (Exception e){
            logger.error("查询国家列表",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }*/
    }













}
