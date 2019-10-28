package com.rondaful.cloud.supplier.controller;


import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.entity.DeliveryRecord;
import com.rondaful.cloud.supplier.entity.Logistics.ErpProviderLogistics;
import com.rondaful.cloud.supplier.entity.LogisticsInfo;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.service.IPlatformLogisticsService;
import com.rondaful.cloud.supplier.service.IThirdLogisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "提供erp接口")
@RestController
@RequestMapping("/erp/provider")
public class ErpProviderController {

    private final Logger logger = LoggerFactory.getLogger(ErpProviderController.class);

    @Autowired
    private IThirdLogisticsService thirdLogisticsService;

    @Autowired
    private IPlatformLogisticsService platformLogisticsService;

    @ApiOperation(value = "获取线上物流信息", response = DeliveryRecord.class)
    @GetMapping("/getOnlineLogistics")
    public ErpProviderLogistics getOnlineLogistics(String packageId,Integer type){
        logger.info("获取线上物流信息接口开始：param={}");
        ErpProviderLogistics erpProviderLogistics = new ErpProviderLogistics();
        try {
            if(1 == type){ //edis
                erpProviderLogistics = platformLogisticsService.getEdis(packageId);
            }else if(2 == type){ //速卖通
                erpProviderLogistics = platformLogisticsService.getAliExpress(packageId);
            }
        } catch (GlobalException e) {
            logger.error("获取线上物流信息接口失败", e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error("获取线上物流信息接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return erpProviderLogistics;
    }

    @ApiOperation(value = "erp物流方式废弃", notes = "code 物流方式代码  warehouseCode 仓库code")
    @PostMapping("/noticeLogisticsDiscard")
    public void noticeLogisticsDiscard(@RequestBody List<LogisticsInfo> logistics) {
        logger.info("erp物流方式废弃接口开始：logistics={}", logistics);
        try {
            if (CollectionUtils.isEmpty(logistics)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
            }
            thirdLogisticsService.noticeLogisticsDiscard(logistics);
        } catch (Exception e) {
            logger.error("erp物流方式废弃接口异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }
}
