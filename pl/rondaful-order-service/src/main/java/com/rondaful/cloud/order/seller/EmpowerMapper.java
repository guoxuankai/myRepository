package com.rondaful.cloud.order.seller;

import com.rondaful.cloud.common.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmpowerMapper extends BaseMapper<Empower> {
    /**
     * 查询物品刊登表(ebay_publish_listing)最大备货天数
     */
    List<Map<String, Object>> getDispatchTimeMax(@Param("empowerId") Integer empowerId, @Param("itemIDList") List<String> itemIDList);

    /**
     * 查询ebay平台所有可用授权token
     *
     * @return
     */
    List<Empower> selectEbayAvailableToken();

    /**
     * 根据品连账号查询此账号对应的eBay平台Token
     *
     * @param sellerAccount
     * @return
     */
    String selectTokenByPLAccount(String sellerAccount);
}