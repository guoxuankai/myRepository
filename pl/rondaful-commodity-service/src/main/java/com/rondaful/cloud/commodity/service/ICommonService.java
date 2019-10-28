package com.rondaful.cloud.commodity.service;

import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.CommodityDetails;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.vo.ErpUpdateCommodityVo;
import com.rondaful.cloud.common.entity.Page;

import java.util.List;
import java.util.Map;

public interface ICommonService {
    Map addCommodity(CommodityBase commodityBase, CommodityDetails commodityDetails, List<CommoditySpec> commoditySpec);
    
    /**
     * @Description:上/下架商品
     * @param vo
     * @return void
     * @author:范津
     */
    void UpOrDownStateCommodity(ErpUpdateCommodityVo vo);
    
    
    StringBuilder uploadImg(String[] urlArr);
}
