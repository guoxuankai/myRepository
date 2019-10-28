package com.brandslink.cloud.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.constants.CustomerFlowConstant;
import com.brandslink.cloud.finance.mapper.*;
import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import com.brandslink.cloud.finance.pojo.dto.*;
import com.brandslink.cloud.finance.pojo.entity.*;
import com.brandslink.cloud.finance.pojo.feature.*;
import com.brandslink.cloud.finance.pojo.feature.details.InStockAllFeature;
import com.brandslink.cloud.finance.pojo.feature.details.OutStockPackFeature;
import com.brandslink.cloud.finance.pojo.feature.details.ReturnDetailFeature;
import com.brandslink.cloud.finance.pojo.vo.*;
import com.brandslink.cloud.finance.service.CustomerFlowService;
import com.brandslink.cloud.finance.service.LogisticsFeesService;
import com.brandslink.cloud.finance.utils.FinanceCommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author yangzefei
 * @Classname CustomerFlowServiceImpl
 * @Description 客户资金流水服务
 * @Date 2019/8/26 10:08
 */
@Service
public class CustomerFlowServiceImpl extends BaseServiceImpl<CustomerFlow> implements CustomerFlowService {

    @Resource
    private CustomerFlowMapper customerFlowMapper;

    @Resource
    private CustomerFlowDetailMapper customerFlowDetailMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private FinanceCommonUtil financeCommonUtil;

    @Resource
    private LogisticsFeesService logisticsFeesService;

    /**
     * 获取交易流水
     * @param param
     * @return
     */
    @Override
    public List<CustomerFlowDto> getList(CustomerFlowVo param){
        //默认上报时间降序
        if(!param.isValidSort("createTime",CustomerFlow.class)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"排序字段或者排序方式不正确");
        }
        List<CustomerFlowDto> list=customerFlowMapper.getList(param);
        list.stream().forEach(p->p.setSerialNo(list.indexOf(p)+1));
        return list;
    }
    /**
     * 获取客户交易流水
     * @param param
     * @return
     */
    @Override
    public List<CustomerSelfFlowDto> getListBySelf(CustomerFlowVo param){
        //设置客户编码
        param.setCustomerCode("");
        List<CustomerFlowDto> list=getList(param);
        List<CustomerSelfFlowDto> selfList=new ArrayList<>();
        selfList.addAll(list);
        return selfList;
    }
    /**
     * 获取流水详情
     * @param id
     * @return
     */
    @Override
    public CustomerFlowDto getById(Integer id){
        CustomerFlowDto infoDto=customerFlowMapper.selectById(id);
        if(infoDto==null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"交易流水详情不存在");
        return infoDto;
    }



    /**
     * 获取sku货型ID与名称
     * @param p
     * @return
     */
    private SkuTypeDto getSkuType(ProductDto p){
        SkuTypeDto skuTypeDto=new SkuTypeDto();
        compareSkuType(skuTypeDto,CustomerFlowConstant.CELLS_TYPE_WEIGHT,p.getPackageWeight()/1000);
        compareSkuType(skuTypeDto,CustomerFlowConstant.CELLS_TYPE_LENGTH,p.getPackageLength()/10);
        compareSkuType(skuTypeDto,CustomerFlowConstant.CELLS_TYPE_WIDTH,p.getPackageWeight()/10);
        compareSkuType(skuTypeDto,CustomerFlowConstant.CELLS_TYPE_HEIGHT,p.getPackageHeight()/10);
        return skuTypeDto;
    }

    /**
     * 与数据库查询出来的比较序号
     * @param old 旧对象
     * @param cellsType 配置类型
     * @param value 区间值
     * @return
     */
    private SkuTypeDto compareSkuType(SkuTypeDto old,Integer cellsType,Double value){
        SkuTypeDto current=customerMapper.getSkuType(cellsType,value);
        if(old.getRowIndex()<current.getRowIndex()){
            old=current;
        }
        return old;
    }

    /**
     * 获取客户配置，并验证客户阀值
     * @return
     */
    private CustomerConfigEntity getConfig(String customerCode){
        CustomerDto customerDto=customerMapper.getByCustomerCode(customerCode);
        if(customerDto==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"客户不存在");
        }
        //1、获取折率
        CustomerConfigEntity config=customerMapper.getConfig(customerCode);
        if(config==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"客户配置不存在");
        }
        //2、判断客户阀值
        if(customerDto.getUsableMoney().compareTo(config.getThresholdMoney())==-1){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"客户余额不足");
        }
        return config;
    }

    /**
     * 获取流水特性
     * @param detailType
     * @return
     */
    private BaseFeature getFeature(Integer detailType){
        if(CustomerFlowConstant.DETAIL_TYPE_RETURN.equals(detailType)){
            return new ReturnCostFeature();
        }else{
            return new InStockCostFeature(detailType);
        }
    }

    /**
     * 获取流水详情特性
     * @param rowId 商品类型ID
     * @param cellsId QC配置ID
     * @param shelveCellsId 上架配置ID
     * @param detailType 详情类型
     * @param skuNumber 商品数量
     * @return
     */
    private BaseFeature getDetailFeature(Integer rowId,Integer cellsId,Integer shelveCellsId,Integer detailType,Integer skuNumber){
        //4、获取货型所对应的入库价格
        StandardQuoteDetail quote= customerMapper.getQuoteDetail(rowId,cellsId,CustomerFlowConstant.QUOTE_TYPE_OPERATE);
        if(quote==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"QC报价配置不存在");
        }
        if(CustomerFlowConstant.DETAIL_TYPE_RETURN.equals(detailType)){
            return new ReturnDetailFeature(quote.getQuoteId(),skuNumber,quote.getQuoteValue());
        }else{
            StandardQuoteDetail shelveQuote=customerMapper.getQuoteDetail(rowId,shelveCellsId,CustomerFlowConstant.QUOTE_TYPE_OPERATE);
            if(shelveQuote==null){
               throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"上架报价配置不存在");
            }
            return new InStockAllFeature(quote.getQuoteId(),skuNumber,quote.getQuoteValue(),shelveQuote.getQuoteValue());
        }
    }

    /**
     * 获取流水详情
     * @param p 商品信息
     * @param feature 流水特性
     * @param detailFeature 流水详情特性
     * @param detailType 详情类型
     * @param discount 折扣
     * @return
     */
    private CustomerFlowDetail getCustomerFlowDetail(ProductDto p,BaseFeature feature,BaseFeature detailFeature,Integer detailType,Double discount){
        CustomerFlowDetail detail=new CustomerFlowDetail();
        detail.setDetailType(detailType);
        detail.setSku(p.getProductSku());
        detail.setSkuNumber(p.getProductNumber());
        detail.setSkuName(p.getProductName());
        detail.setOperate(financeCommonUtil.getOperate());
        if(CustomerFlowConstant.DETAIL_TYPE_RETURN.equals(detailType)){
            //销退
            ReturnDetailFeature returnDetailFeature=(ReturnDetailFeature)detailFeature;
            detail.setOriginalCost(returnDetailFeature.getShelveCost());
            ReturnCostFeature returnCostFeature=(ReturnCostFeature)feature;
            returnCostFeature.add(returnDetailFeature.getShelveCost());

        }else if(CustomerFlowConstant.DETAIL_TYPE_OUT_STOCK.equals(detailType)){
            //出库
            OutStockPackFeature outStockPackFeature=(OutStockPackFeature)detailFeature;
            detail.setOriginalCost(outStockPackFeature.getOperateCost().add(outStockPackFeature.getPackCost()));
            OutStockCostFeature outStockCostFeature=(OutStockCostFeature)feature;
            outStockCostFeature.add(outStockPackFeature.getOperateCost(),outStockPackFeature.getPackCost());
        } else{
            //入库
            InStockAllFeature inStockAllFeature=(InStockAllFeature) detailFeature;
            detail.setOriginalCost(inStockAllFeature.getQcCost().add(inStockAllFeature.getShelveCost()));
            InStockCostFeature costFeature=(InStockCostFeature)feature;
            costFeature.add(inStockAllFeature.getQcCost(),inStockAllFeature.getShelveCost());
        }
        detail.setFeatureJson(JSON.toJSONString(feature));
        detail.setDiscount(discount);
        detail.setDiscountCost(detail.getOriginalCost().multiply(new BigDecimal(discount)));
        return detail;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveCustomerFlow(List<CustomerFlowDetail> details, CustomerFlow customerFlow, BaseCostVo param, String customerCode){
        customerFlow.setOperate(financeCommonUtil.getOperate());
        customerFlow.setCustomerCode(customerCode);
        customerFlow.setOrderType(2);
        customerFlow.setWarehouseCode(param.getWarehouseCode());
        customerFlow.setWarehouseName(param.getWarehouseName());
        customerFlow.setSourceNo(param.getSourceNo());
        customerFlow.setWaybillNo(param.getWaybillNo());
        customerFlow.setBillTime(new Date());

        if(CustomerFlowConstant.COST_TYPE_LOGISTICS.equals(customerFlow.getCostType())){
            //如果是物流费，则冻结金额,并清空应扣金额与折率
            LogisticsCostVo costVo=(LogisticsCostVo) param;
            Boolean isFreeze=customerMapper.updateByFreeze(customerCode,costVo.getFeature().getFreezeMoney());
            if(!isFreeze){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"账户余额不足");
            }
            logisticsFeesService.save(costVo);
        }else{
            //客户总支出增加，账户余额与可用余额减少
            Boolean isExpend=customerMapper.updateByExpend(customerCode,customerFlow.getDiscountCost());
            if(!isExpend){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"账户余额不足");
            }
        }
        CustomerDto customerDto= customerMapper.getByCustomerCode(customerCode);
        customerFlow.setBeforeMoney(customerDto.getBalanceMoney().add(customerFlow.getDiscountCost()));
        customerFlow.setAfterMoney(customerDto.getBalanceMoney());
        customerFlow.setUsableMoney(customerDto.getUsableMoney());
        customerFlow.setFreezeMoney(customerDto.getFreezeMoney());
        customerFlowMapper.insertSelective(customerFlow);
        if(details!=null){
            for(CustomerFlowDetail item:details){
                item.setCustomerFlowId(customerFlow.getId());
                customerFlowDetailMapper.insertSelective(item);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calcInStockCost(StockCostVo param){
        for(String customerCode:param.getCustomerMap().keySet()){
            CustomerConfigEntity config=getConfig(customerCode);
            List<ProductDto> list= param.getCustomerMap().get(customerCode);
            BaseFeature feature=getFeature(param.getDetailType());
            CustomerFlow customerFlow=new CustomerFlow();
            List<CustomerFlowDetail> details=new ArrayList<>();
            Integer costType=CustomerFlowConstant.COST_TYPE_IN_STOCK;
            Integer cellsId,shelveCellsId=0;
            if(CustomerFlowConstant.DETAIL_TYPE_RETURN.equals(param.getDetailType())){
                cellsId=customerMapper.getCellIdByCellsType(CustomerFlowConstant.CELLS_TYPE_SHELVE_RETURN);//获取销退的配置ID
                customerFlow.setOrderNo(financeCommonUtil.getOrderNo(CustomerFlowConstant.BILL_NO_XT));
                costType=CustomerFlowConstant.COST_TYPE_RETURN;
            }else{
                shelveCellsId=customerMapper.getCellIdByCellsType(CustomerFlowConstant.CELLS_TYPE_SHELVE_SOURCE);//获取上架的配置ID
                cellsId=customerMapper.getCellIdByCellsType(CustomerFlowConstant.TYPE_MAP.get(param.getDetailType()));
                customerFlow.setOrderNo(financeCommonUtil.getOrderNo(CustomerFlowConstant.BILL_NO_RK));
            }
            for(ProductDto p:list){
                //3、获取sku货型ID与名称
                SkuTypeDto skuTypeDto=getSkuType(p);
                BaseFeature detailFeature=getDetailFeature(skuTypeDto.getId(),cellsId,shelveCellsId,param.getDetailType(),p.getProductNumber());
                CustomerFlowDetail detail=getCustomerFlowDetail(p,feature,detailFeature,param.getDetailType(),config.getInstockFee());

                customerFlow.setNumber(customerFlow.getNumber()+p.getProductNumber());
                customerFlow.setOriginalCost(customerFlow.getOriginalCost().add(detail.getOriginalCost()));
                customerFlow.setDiscountCost(customerFlow.getDiscountCost().add(detail.getDiscountCost()));

                details.add(detail);
            }
            customerFlow.setDiscount(config.getInstockFee());
            customerFlow.setFeatureJson(JSON.toJSONString(feature));
            customerFlow.setCostType(costType);
            saveCustomerFlow(details,customerFlow,param,customerCode);
        }
    }

    @Override
    public void calcReturnCost(StockCostVo param){
        calcInStockCost(param);
    }

    @Override
    public void calcOutStockCost(OutStockCostVo param){
        for(String customerCode:param.getCustomerMap().keySet()){
            CustomerConfigEntity config=getConfig(customerCode);
            List<ProductDto> list= param.getCustomerMap().get(customerCode);
            Integer skuCount= list.stream().mapToInt(p->p.getProductNumber()).sum();
            OutStockCostFeature feature=new OutStockCostFeature(param.getOrderType(),param.getPlatformOrderNo());
            CellsUnitDto cellsUnit=customerMapper.getCellUnit(skuCount);
            Integer operateCellsId=customerMapper.getCellIdByCellsType(CustomerFlowConstant.CELLS_TYPE_OPERATE);
            CustomerFlow customerFlow=new CustomerFlow();
            List<CustomerFlowDetail> details=new ArrayList<>();
            for(ProductDto p:list){
                //3、获取sku货型ID与名称
                SkuTypeDto skuTypeDto=getSkuType(p);
                StandardQuoteDetail operateQuote= customerMapper.getQuoteDetail(skuTypeDto.getId(),operateCellsId,param.getOrderType());
                if(operateQuote==null){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"出库操作费报价不存在");
                }
                StandardQuoteDetail packQuote= customerMapper.getQuoteDetail(skuTypeDto.getId(),cellsUnit.getId(),param.getOrderType());
                if(packQuote==null){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"出库打包费报价不存在");
                }
                OutStockPackFeature detailFeature;
                if(cellsUnit.getUnit()==1){
                    //以包裹计费
                    detailFeature=new OutStockPackFeature(operateQuote.getQuoteId(),p.getProductNumber(),operateQuote.getQuoteValue(),BigDecimal.ZERO);
                }else{
                    //以件计费
                    detailFeature=new OutStockPackFeature(operateQuote.getQuoteId(),p.getProductNumber(),operateQuote.getQuoteValue(),packQuote.getQuoteValue());
                }
                CustomerFlowDetail detail=getCustomerFlowDetail(p,feature,detailFeature,param.getDetailType(),config.getOutstockFee());
                //如果以包裹计费，则单独赋值
                if(cellsUnit.getUnit()==1){
                    feature.setPackCost(packQuote.getQuoteValue());
                }
                customerFlow.setNumber(customerFlow.getNumber()+p.getProductNumber());
                customerFlow.setOriginalCost(customerFlow.getOriginalCost().add(detail.getOriginalCost()));
                customerFlow.setDiscountCost(customerFlow.getDiscountCost().add(detail.getDiscountCost()));

                details.add(detail);
            }
            customerFlow.setOrderNo(financeCommonUtil.getOrderNo(CustomerFlowConstant.BILL_NO_CK));
            customerFlow.setFeatureJson(JSON.toJSONString(feature));
            customerFlow.setDiscount(config.getOutstockFee());
            customerFlow.setCostType(CustomerFlowConstant.COST_TYPE_OUT_STOCK);
            saveCustomerFlow(details,customerFlow,param,customerCode);
        }
    }

    @Override
    public void calcInterceptCost(InterceptCostVo param){
        String customerCode=param.getCustomerCode();
        CustomerConfigEntity config=getConfig(customerCode);
        Integer cellsId=customerMapper.getCellIdByCellsType(param.getInterceptType());
        StandardQuoteDetail quote= customerMapper.getQuoteDetail(0,cellsId,CustomerFlowConstant.QUOTE_TYPE_INCREMENT);
        if(quote==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"订单拦截报价不存在");
        }
        InterceptCostFeature feature=new InterceptCostFeature(param.getPlatformOrderNo(),param.getInterceptType(),quote.getQuoteValue());
        CustomerFlow customerFlow=new CustomerFlow();
        customerFlow.setOriginalCost(quote.getQuoteValue());
        customerFlow.setDiscount(config.getIncrementFee());
        customerFlow.setDiscountCost(quote.getQuoteValue().multiply(new BigDecimal(config.getIncrementFee())));
        customerFlow.setOrderNo(financeCommonUtil.getOrderNo(CustomerFlowConstant.BILL_NO_LJ));
        customerFlow.setFeatureJson(JSON.toJSONString(feature));
        customerFlow.setCostType(CustomerFlowConstant.COST_TYPE_INTERCEPT);
        saveCustomerFlow(null,customerFlow,param,customerCode);
    }

    @Override
    public void calcLogisticsCost(LogisticsCostVo param){
        String customerCode=param.getCustomerCode();
        getConfig(customerCode); //判断用户阀值
        LogisticsCostFeature feature=param.getFeature();
        if(feature==null||BigDecimal.ZERO.compareTo(feature.getFreightCost())==1){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"物流费必须大于零");
        }
        CustomerDto customerDto=customerMapper.getByCustomerCode(customerCode);
        param.setCustomerName(customerDto.getCustomerName());

        feature.setFreezeMoney(feature.getFreightCost());
        CustomerFlow customerFlow=new CustomerFlow();
        customerFlow.setOrderNo(financeCommonUtil.getOrderNo(CustomerFlowConstant.BILL_NO_WL));
        customerFlow.setFeatureJson(JSON.toJSONString(feature));
        customerFlow.setCostType(CustomerFlowConstant.COST_TYPE_LOGISTICS);
        saveCustomerFlow(null,customerFlow,param,customerCode);
    }

    /**
     * 实际物流费计算
     * @param sourceNo
     * @param logFreightCost
     */
    @Override
    public void calcActualLogisticsCost(String sourceNo, BigDecimal logFreightCost){
        CustomerFlow customerFlow=customerFlowMapper.selectBySourceNo(sourceNo);
        LogisticsCostFeature feature=JSON.parseObject(customerFlow.getFeatureJson(),LogisticsCostFeature.class);
        BigDecimal freezeMoney=feature.getFreezeMoney();
        feature.setFreezeMoney(BigDecimal.ZERO);
        feature.setLogFreightCost(logFreightCost);

        CustomerConfigEntity config=getConfig(customerFlow.getCustomerCode());
        customerFlow.setOriginalCost(logFreightCost);
        customerFlow.setDiscount(config.getLogisticsFee());
        customerFlow.setDiscountCost(logFreightCost.multiply(new BigDecimal(config.getLogisticsFee())));
        customerFlow.setOrderNo(financeCommonUtil.getOrderNo(CustomerFlowConstant.BILL_NO_WL));
        customerFlow.setFeatureJson(JSON.toJSONString(feature));
        customerFlow.setBillTime(new Date());

        //客户总支出增加,冻结金额回退至可用余额,可用余额减少，账户余额减少
        Boolean isUnFreeze=customerMapper.updateByUnFreeze(customerFlow.getCustomerCode(),customerFlow.getDiscountCost(),freezeMoney);
        if(!isUnFreeze){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"账户余额不足");
        }
        CustomerDto customerDto= customerMapper.getByCustomerCode(customerFlow.getCustomerCode());
        customerFlow.setBeforeMoney(customerDto.getBalanceMoney().add(customerFlow.getDiscountCost()));
        customerFlow.setAfterMoney(customerDto.getBalanceMoney());
        customerFlow.setUsableMoney(customerDto.getUsableMoney());
        customerFlow.setFreezeMoney(customerDto.getFreezeMoney());
        customerFlowMapper.insertSelective(customerFlow);
    }
}
