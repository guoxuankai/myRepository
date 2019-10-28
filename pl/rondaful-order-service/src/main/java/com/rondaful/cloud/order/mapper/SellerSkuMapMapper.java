package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.orderRule.SellerSkuMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SellerSkuMapMapper extends BaseMapper<SellerSkuMap> {

    int deleteByPrimaryKey(Long id);

    int insert(SellerSkuMap record);

    int insertSelective(SellerSkuMap record);

    SellerSkuMap selectByPrimaryKey(Long id);

    SellerSkuMap selectByEntry(SellerSkuMap map);

    int updateByPrimaryKeySelective(SellerSkuMap record);

    int updateByPrimaryKey(SellerSkuMap record);

    int updateStatusByPlSku(SellerSkuMap record);

    List<SellerSkuMap> page(SellerSkuMap map);
    
    void deleteByPlSku(@Param("plSku")String plSku);

    /**
     * 平台SKU查询品连SKU
     *
     * @param platform  平台
     * @param platformSku  其他平台的SKU
     * @param status 状态
     * @return
     */
    SellerSkuMap getSellerSkuMapByOtherPlatformSku(@Param("platform") String platform, @Param("platformSku")  String platformSku,
                                                   @Param("status") Integer status,@Param("authorizationId") Integer authorizationId);
    
    List<SellerSkuMap> getAllByPlSku(@Param("plSku")String plSku);
}