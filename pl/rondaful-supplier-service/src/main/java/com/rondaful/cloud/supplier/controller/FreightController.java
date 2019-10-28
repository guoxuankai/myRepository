package com.rondaful.cloud.supplier.controller;


import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.LogisticsDetailVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import com.rondaful.cloud.supplier.dto.FreightTrialDTO;
import com.rondaful.cloud.supplier.entity.FreightTrial;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.model.request.third.ThirdFreightReq;
import com.rondaful.cloud.supplier.remote.RemoteCommodityService;
import com.rondaful.cloud.supplier.service.IFreightService;
import com.rondaful.cloud.supplier.utils.FreightUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(description = "运费试算接口")
@RestController
@RequestMapping("/freight")
public class FreightController {

    private final Logger logger = LoggerFactory.getLogger(FreightController.class);

    @Autowired
    private IFreightService freightService;

    @Autowired
    private FreightUtil freightUtil;

    @Autowired
    private RemoteCommodityService remoteCommodityService;


    @ApiOperation(value = "获取运费")
    @PostMapping("/getFreight")
    public List<FreightTrialDTO> getFreight(@RequestBody ThirdFreightReq param){
        logger.info("获取运费接口开始：param={}", param);
        List<FreightTrialDTO> list = new ArrayList<>();
        try {
            list = freightService.getFreight(param);
        } catch (GlobalException e) {
            logger.error("获取运费接口失败", e);
            if("GY".equals(e.getErrorCode().split("_")[0])){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100410,e.getMessage());
            }else{
                throw new GlobalException(e.getErrorCode(),e.getMessage());
            }
        } catch (Exception e) {
            logger.error("获取运费接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return list;
    }

    @ApiOperation(value = "标签查询合适物流方式", notes = "")
    @PostMapping("/getSuitLogisticsByType")
    public List<LogisticsDetailVo> getSuitLogisticsByType(@RequestBody SearchLogisticsListDTO param){
        logger.info("标签查询合适物流方式接口开始：param={}", param);
        List<LogisticsDetailVo>  list = new ArrayList<>();
        try {
            checkParam(param);
            list = freightService.getSuitLogisticsByType(param);
        } catch (GlobalException e) {
            logger.error("标签查询合适物流方式接口失败", e);
            if("GY".equals(e.getErrorCode().split("_")[0])){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100410,e.getMessage());
            }else{
                throw new GlobalException(e.getErrorCode(),e.getMessage());
            }
        } catch (Exception e) {
            logger.error("标签查询合适物流方式接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return list;
    }

    @ApiOperation(value = "标签查询运费（刊登）")
    @PostMapping("/getFreightTrialByType")
    public FreightTrialDTO getFreightTrialByType(@RequestBody FreightTrial freightTrial){
        logger.info("标签查询运费接口开始：freightTrial={}", freightTrial);
        FreightTrialDTO result;
        try {
            result = freightService.getFreightTrialByType(freightTrial);
        } catch (GlobalException e) {
            logger.error("标签查询运费（刊登）接口失败", e);
            if("GY".equals(e.getErrorCode().split("_")[0])){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100410,e.getMessage());
            }else{
                throw new GlobalException(e.getErrorCode(),e.getMessage());
            }
        } catch (Exception e) {
            logger.error("标签查询运费（刊登）接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return result;
    }

    @ApiOperation(value = "查询具体的运费")
    @PostMapping("/queryFreightByLogisticsCode")
    public LogisticsCostVo queryFreightByLogisticsCode(@RequestBody LogisticsCostVo param){
        logger.info("查询具体的运费接口开始：param={}", param);
        LogisticsCostVo logisticsCostVo = new LogisticsCostVo();
        try {
            logisticsCostVo = freightService.queryFreightByLogisticsCode(param);
        } catch (GlobalException e) {
            logger.error("查询具体的运费接口失败", e);
            if("GY".equals(e.getErrorCode().split("_")[0])){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100410,e.getMessage());
            }else{
                throw new GlobalException(e.getErrorCode(),e.getMessage());
            }
        } catch (Exception e) {
            logger.error("查询具体的运费接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return logisticsCostVo;
    }


    private void checkParam(SearchLogisticsListDTO param) {
        if (null == param.getWarehouseId()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "仓库id不能为空");
        }
        if (StringUtils.isEmpty(param.getCountryCode())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "国家code不能为空");
        }
        if (CollectionUtils.isEmpty(param.getSearchLogisticsListSkuList())){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "sku不能为空");
         }
        if (null == param.getSearchType()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "搜索条件不能为空");
        }
//        if (StringUtils.isEmpty(param.getPostCode())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "邮编不能为空");
//        }

    }


}
