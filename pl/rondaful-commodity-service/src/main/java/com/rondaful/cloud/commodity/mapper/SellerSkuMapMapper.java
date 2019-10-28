package com.rondaful.cloud.commodity.mapper;

import com.rondaful.cloud.commodity.entity.SellerSkuMap;
import com.rondaful.cloud.common.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SellerSkuMapMapper extends BaseMapper<SellerSkuMap> {

    int deleteByPrimaryKey(Long id);

    int insert(SellerSkuMap record);

    int insertSelective(SellerSkuMap record);

    SellerSkuMap selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellerSkuMap record);

    int updateByPrimaryKey(SellerSkuMap record);

    List<SellerSkuMap> page(SellerSkuMap map);
    
    
    int getUniqueNum(@Param("platform")String platform, @Param("authorizationId")String authorizationId, @Param("platformSku")String platformSku);
    
    void updateStatus(@Param("id")Long id,@Param("status")Integer status);
    
    SellerSkuMap getByUniqueKey(@Param("platform")String platform, @Param("authorizationId")String authorizationId, @Param("platformSku")String platformSku);
}