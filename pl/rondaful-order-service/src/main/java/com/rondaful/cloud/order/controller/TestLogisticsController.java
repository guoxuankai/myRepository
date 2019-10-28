package com.rondaful.cloud.order.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.annotation.OpenAPI;
import com.rondaful.cloud.common.enums.ThirdMsgTypeEnum;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostEnum;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.LogisticsDetailVo;
import com.rondaful.cloud.common.model.vo.freight.SkuGroupVo;
import com.rondaful.cloud.common.model.vo.freight.SupplierGroupVo;
import com.rondaful.cloud.common.push.service.PushThirdService;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.order.enums.OrderSourceCovertToLogisticsServicePlatformEnum;
import com.rondaful.cloud.order.mapper.SysOrderPackageDetailMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsResultVO;
import com.rondaful.cloud.order.service.ISystemOrderService;
import com.rondaful.cloud.order.utils.OrderUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangzefei
 * @Classname TestLogisticsController
 * @Description 老数据物流费用计算
 * @Date 2019/7/29 10:28
 */
@RestController
@RequestMapping("/testLogistics")
public class TestLogisticsController extends BaseController {

    @Resource
    private ISystemOrderService orderService;

    @Resource
    private SysOrderPackageMapper packageMapper;

    @Resource
    private SysOrderPackageDetailMapper packageDetailMapper;
    private static Logger logger = LoggerFactory.getLogger(TestLogisticsController.class);

    @Autowired
    private PushThirdService pushThirdService;

    @GetMapping("/testPush")
    public void testPush(@RequestParam("appId")String appId){
        //5dabf2e419c36a19f7ca907c1d077f94
        SkuGroupVo skuGroupVo=new SkuGroupVo(1L,"1231",12, new BigDecimal(12.4),1234);
        pushThirdService.send(appId, ThirdMsgTypeEnum.TEST,JSON.toJSONString(skuGroupVo));
    }

    /**
     * 请求参数为单个实体类
     * @param param
     * @return
     */
    @OpenAPI
    @PostMapping("/testPostEntity")
    public SkuGroupVo testPostEntity(SkuGroupVo param){
        return param;
    }

    /**
     * 请求参数为 list
     * @param param
     * @param version
     * @return
     */
    @OpenAPI
    @PostMapping("/testPostList")
    public List<SkuGroupVo> testPostList(ArrayList<SkuGroupVo> param,@RequestParam("version") String version){
        return param;
    }
    /**
     * 请求参数为单个实体，并且需要获取appId
     * @param param
     * @param appId
     * @return
     */
    @OpenAPI
    @PostMapping("/testPost")
    public String testPost(SupplierGroupVo param,String appId){
        return appId;
    }

    /**
     * get请求获取请求参数
     * @param a
     * @param b
     * @param appId
     * @return
     */
    @OpenAPI
    @GetMapping("/testGetParams")
    public String testGetParams(@RequestParam("a")Integer a,@RequestParam("b")String b,String appId){
        return "a="+a+",b="+b+",appId="+appId;
    }



//    @GetMapping("/calc")
//    @ApiOperation("老数据的物流费用计算")
//    public void calc(@RequestParam("offset")Integer offset,@RequestParam("count")Integer count){
//        for(int i=offset;i<count;i+=10){
//            List<SysOrderPackage> packs= packageMapper.queryOrderPackageByLimit(i,10);
//            if(packs.size()<=0) break;
//            for(SysOrderPackage item:packs){
//                //当发货仓库为空或者小于0时,不处理
//                if(item.getDeliveryWarehouseId()==null||item.getDeliveryWarehouseId()<=0)
//                    continue;
//                calcEstimateFreight(item.getSysOrderId());
//            }
//        }
//    }

    private void calcEstimateFreight(String orderId){
        SysOrderNew sysOrderNew=orderService.getSysOrderNew(orderId);
        List<SysOrderPackageDTO> sysOrderPackageDTOList = new ArrayList<>();
        for (SysOrderPackage sysOrderPackage : sysOrderNew.getSysOrderPackageList()) {
            SysOrderPackageDTO sysOrderPackageDTO = new SysOrderPackageDTO();
            BeanUtils.copyProperties(sysOrderNew.getSysOrderPackageList().get(0), sysOrderPackageDTO);
            List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList = new ArrayList<>();
            List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackage.getSysOrderPackageDetailList();
            for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetails) {
                SysOrderPackageDetailDTO sysOrderPackageDetailDTO = new SysOrderPackageDetailDTO();
                BeanUtils.copyProperties(sysOrderPackageDetail, sysOrderPackageDetailDTO);
                sysOrderPackageDetailDTOList.add(sysOrderPackageDetailDTO);
            }
            sysOrderPackageDTO.setSysOrderPackageDetailList(sysOrderPackageDetailDTOList);
            sysOrderPackageDTOList.add(sysOrderPackageDTO);
        }
        SysOrderReceiveAddress sysOrderReceiveAddress = sysOrderNew.getSysOrderReceiveAddress();
        int platformType =OrderSourceCovertToLogisticsServicePlatformEnum.getLogisticsPlatformCode(sysOrderNew.getOrderSource());
        for (SysOrderPackageDTO sysOrderPackageDTO : sysOrderPackageDTOList) {
            LogisticsDetailVo logisticsDetailVo=getDeliveryMethodCode(sysOrderPackageDTO,sysOrderReceiveAddress);
            if(StringUtils.isEmpty(logisticsDetailVo.getLogisticsCode())) continue;
            CalculateLogisticsResultVO resultVo = orderService.calculateEstimateFreight(
                    sysOrderPackageDTO,
                    String.valueOf(platformType),
                    String.valueOf(sysOrderPackageDTO.getDeliveryWarehouseId()),
                    sysOrderReceiveAddress.getShipToCountry(),
                    sysOrderReceiveAddress.getShipToPostalCode(),
                    null,
                    logisticsDetailVo.getLogisticsCode(),sysOrderReceiveAddress.getShipToCity(), sysOrderNew.getPlatformShopId(), 1);

            calcLogistics(resultVo.getLogisticsCostData(),sysOrderPackageDTO.getOrderTrackId(),sysOrderPackageDTO.getActualShipCost());
//            if(StringUtils.isNotEmpty(logisticsDetailVo.getLogisticsName())){
//                updateSysPackage(logisticsDetailVo,sysOrderPackageDTO.getOrderTrackId());
//            }
            logger.info("数据组装之后的为{}", JSONObject.toJSONString(resultVo));
        }
    }

    /**
     * 获取物流方式
     */
    private LogisticsDetailVo getDeliveryMethodCode(SysOrderPackageDTO param,SysOrderReceiveAddress sysOrderReceiveAddress ){
        LogisticsDetailVo detailVo=new LogisticsDetailVo();
        if(StringUtils.isNotEmpty(param.getDeliveryMethodCode())){
            detailVo.setLogisticsCode(param.getDeliveryMethodCode());
        }
//        else{
//            SearchLogisticsListDTO searchLogisticsListDTO = new SearchLogisticsListDTO();
//            searchLogisticsListDTO.setCity(sysOrderReceiveAddress.getShipToCity());
//            searchLogisticsListDTO.setCountryCode(sysOrderReceiveAddress.getShipToCountry());
//            searchLogisticsListDTO.setPlatformType(String.valueOf(param.getOrderSource()));
//            searchLogisticsListDTO.setPostCode(sysOrderReceiveAddress.getShipToPostalCode());
//            searchLogisticsListDTO.setWarehouseId(String.valueOf(param.getDeliveryWarehouseId()));
//            searchLogisticsListDTO.setSearchType(1);
//
//            List<SearchLogisticsListSku> skuList = new ArrayList<>();
//            for (SysOrderPackageDetailDTO sysOrderPackageDetail : param.getSysOrderPackageDetailList()) {
//                SearchLogisticsListSku searchLogisticsListSku = new SearchLogisticsListSku();
//                searchLogisticsListSku.setSku(sysOrderPackageDetail.getSku());
//                searchLogisticsListSku.setSkuNumber(sysOrderPackageDetail.getSkuQuantity());
//                skuList.add(searchLogisticsListSku);
//            }
//            searchLogisticsListDTO.setSearchLogisticsListSkuList(skuList);
//            List<LogisticsDetailVo> list= orderService.getSuitLogisticsByType(searchLogisticsListDTO);
//            if(list!=null&&list.size()>0){
//                return list.get(0);
//            }
//        }
        return detailVo;
    }

    /**
     * 计算物流费
     * @param costVo
     * @param orderTrackId
     * @param actualShipCost
     */
    private void calcLogistics(LogisticsCostVo costVo,String orderTrackId,BigDecimal actualShipCost){
        SupplierGroupVo sellers=costVo.getSellers().get(0);
        if(actualShipCost.compareTo(BigDecimal.ZERO)==1){
            String result=JSONObject.toJSONString(sellers);
            SupplierGroupVo supplierGroupVo= JSONObject.parseObject(result, SupplierGroupVo.class);
            supplierGroupVo.setSupplierCost(actualShipCost);
            List<SupplierGroupVo> list=new ArrayList<>();
            list.add(supplierGroupVo);
            OrderUtils.calcLogisticsItems(list, LogisticsCostEnum.logistics);
            costVo.setLogistics(list);

        }
        updateLogisticsCost(costVo,orderTrackId);
        logger.info("订单跟踪号:{}", orderTrackId);
    }

    /**
     *
     * @param costVo
     * @param orderTrackId
     */
    private void updateLogisticsCost(LogisticsCostVo costVo,String orderTrackId){
        List<SysOrderPackageDetail> details=packageDetailMapper.queryOrderPackageDetails(orderTrackId);
        Map<String,SkuGroupVo> sellersMap=getSkuGroupMap(costVo.getSellers());
        Map<String,SkuGroupVo> supplierMap=getSkuGroupMap(costVo.getSupplier());
        Map<String,SkuGroupVo> logisticsMap=getSkuGroupMap(costVo.getLogistics());
        SkuGroupVo skuGroupVo;
        for(SysOrderPackageDetail item:details){
            skuGroupVo=sellersMap.get(item.getSku());
            if(skuGroupVo!=null){
                item.setSellerShipFee(skuGroupVo.getSkuPlCost());
            }
            skuGroupVo=supplierMap.get(item.getSku());
            if(skuGroupVo!=null){
                item.setSupplierShipFee(skuGroupVo.getSkuPlCost());
            }
            skuGroupVo=logisticsMap.get(item.getSku());
            if(skuGroupVo!=null){
                item.setLogisticCompanyShipFee(skuGroupVo.getSkuPlCost());
            }
            packageDetailMapper.updateByPrimaryKeySelective(item);
        }
    }

    private Map<String,SkuGroupVo> getSkuGroupMap(List<SupplierGroupVo> list){
        List<SkuGroupVo> skuList=new ArrayList<>();
        for(SupplierGroupVo item:list){
            skuList.addAll(item.getItems());
        }
        return skuList.stream().collect(Collectors.toMap(SkuGroupVo::getSku,SkuGroupVo->SkuGroupVo));
    }

    private void updateSysPackage(LogisticsDetailVo logisticsDetailVo,String orderTrackId){
       SysOrderPackage sysOrderPackage=packageMapper.queryOrderPackageByOrderTrackId(orderTrackId);
       if(sysOrderPackage!=null){
           sysOrderPackage.setDeliveryMethodCode(logisticsDetailVo.getLogisticsCode());
           sysOrderPackage.setDeliveryMethod(logisticsDetailVo.getLogisticsName());
           sysOrderPackage.setEstimateShipCost(logisticsDetailVo.getFreightCost());
           sysOrderPackage.setShippingCarrierUsed(logisticsDetailVo.getCarrierName());
           sysOrderPackage.setShippingCarrierUsedCode(logisticsDetailVo.getCarrierCode());
           sysOrderPackage.setAmazonCarrierName(logisticsDetailVo.getAmazonCarrier());
           sysOrderPackage.setEbayCarrierName(logisticsDetailVo.getEbayCarrier());
           packageMapper.updateByPrimaryKeySelective(sysOrderPackage);
       }
    }
}
