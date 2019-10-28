package com.brandslink.cloud.finance.service.impl;

import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.constants.LogisticsFeesStatusConstant;
import com.brandslink.cloud.finance.mapper.ImportFailureMapper;
import com.brandslink.cloud.finance.mapper.LogisticsFeesMapper;
import com.brandslink.cloud.finance.pojo.dto.CustomerDto;
import com.brandslink.cloud.finance.pojo.entity.ImportFailure;
import com.brandslink.cloud.finance.pojo.entity.LogisticsFees;
import com.brandslink.cloud.finance.pojo.feature.LogisticsCostFeature;
import com.brandslink.cloud.finance.pojo.vo.LogisticsCostVo;
import com.brandslink.cloud.finance.pojo.vo.LogisticsFeesVO;
import com.brandslink.cloud.finance.service.CustomerFlowService;
import com.brandslink.cloud.finance.service.LogisticsFeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class LogisticsFeesServiceImpl extends BaseServiceImpl<LogisticsFees> implements LogisticsFeesService {

    @Resource
    LogisticsFeesMapper logisticsFeesMapper;

    @Resource
    ImportFailureMapper importFailureMapper;

    @Resource
    CustomerFlowService customerFlowService;

    @Override
    public List<LogisticsFees> list(LogisticsFeesVO logisticsFeesQuery) {
        return logisticsFeesMapper.list(logisticsFeesQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(int[] idArr, int status) {
        for(Integer id:idArr){
            LogisticsFees fees=logisticsFeesMapper.selectByPrimaryKey((long)id);
            if(fees==null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"物流费记录不存在");
            }
            if(fees.getLogisticFreight()==null|| BigDecimal.ZERO.compareTo(fees.getLogisticFreight())==1){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"实际物流费未导入");
            }
            customerFlowService.calcActualLogisticsCost(fees.getOrderNo(),fees.getLogisticFreight());
        }
        logisticsFeesMapper.updateStatus(idArr, status);
    }

    @Override
    public void importData(List<LogisticsFees> logisticsFees) {

        for (LogisticsFees logisticsFee : logisticsFees) {
            Integer id = logisticsFee.getId();
            LogisticsFees getOne = logisticsFeesMapper.selectByPrimaryKey(Long.valueOf(id));
            if (getOne != null) {
                logisticsFee.setStatus(LogisticsFeesStatusConstant.TO_CONFIRMED);
                logisticsFeesMapper.updateByPrimaryKey(logisticsFee);
            } else {
                ImportFailure importFailure = new ImportFailure(logisticsFee.getPackageNo(), logisticsFee.getWaybill(), "未查询到该运单", "导入人员1");
                importFailureMapper.insert(importFailure);
            }

        }

    }

    /**
     * 新增物流费记录
     * @param param
     */
    @Override
    public void save(LogisticsCostVo param){
        LogisticsCostVo costVo=(LogisticsCostVo)param;
        LogisticsCostFeature feature=costVo.getFeature();

        LogisticsFees fees=new LogisticsFees();
        fees.setStatus(1);
        fees.setCustomerName(param.getCustomerName());
        fees.setWarehouse(costVo.getWarehouseName());
        fees.setOrderNo(costVo.getSourceNo());
        fees.setWaybill(costVo.getWaybillNo());
        fees.setTrackingNumber(feature.getTrackNo());
        fees.setPackageNo(feature.getLogNo());
        fees.setLogisticsProvider(feature.getLogName());
        fees.setMailingMethod(feature.getPostType());
        fees.setDeliveryTime(new Date());
        fees.setCountry(feature.getCountry());
        fees.setCity(feature.getCity());
        fees.setCalculativeWeight(feature.getChargedWeight());
        fees.setActualWeight(feature.getActualWeight());
        fees.setWarehouseFreight(feature.getFreightCost());
        fees.setLogisticCalculativeWeight(feature.getLogChargedWeight());
        fees.setLogisticActualWeight(feature.getLogActualWeight());

        logisticsFeesMapper.insertSelective(fees);
    }
}
