package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.LogisticsDetailVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import com.rondaful.cloud.supplier.dto.FreightTrialDTO;
import com.rondaful.cloud.supplier.entity.FreightTrial;
import com.rondaful.cloud.supplier.model.request.third.ThirdFreightReq;

import java.util.List;

public interface IFreightService {

    /**
    * @Description 标签查找运费
    * @Author  xieyanbin
    * @Param
    * @Return
    * @Exception
    *
    */
    List<LogisticsDetailVo> getSuitLogisticsByType(SearchLogisticsListDTO param);

    /**
    * @Description 获取合适的物流方式（刊登）
    * @Author  xieyanbin
    * @Param  freightTrial1
    * @Return      FreightTrialDTO
    * @Exception
    *
    */
    FreightTrialDTO getFreightTrialByType(FreightTrial freightTrial1);
    
    /**
    * @Description
    * @Author  xieyanbin
    * @Param
    * @Return      
    * @Exception   通过物流方式code查询运费
    * 
    */
    LogisticsCostVo queryFreightByLogisticsCode(LogisticsCostVo param);

    List<FreightTrialDTO> getFreight(ThirdFreightReq thirdFreightReq);
}
