package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.Amazon.AmazonEmpower;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
@Mapper
public interface AmazonEmpowerMapper extends BaseMapper<AmazonEmpower> {
    /*批量插入*/
    int insertBulk(List<AmazonEmpower> list);
    /*通过品连账号更新同步状态*/
    int updateIsSync(@Param("plAccount") String plAccount,@Param("marketplaceId") String marketplaceId,@Param("sellerId") String sellerId, @Param
            ("syncStatus") byte syncStatus);
    /*通过品连账号查询同步状态*/
    List<Byte> selectSyncStatus(String plAccount);
    /*通过品连账号查询授权信息*/
    List<AmazonEmpower> selectInfoByAccount(@Param("plAccount") String plAccount);
    /*根据sellerId查询亚马逊授权token*/
    List<String> selectMWSTokenBySellerId(@Param("sellerId") String sellerId,@Param("marketplaceId") String marketplaceId);

    Date queryLastUpdateTime(String sellerId);
    /*重置授权信息同步状态为未同步，7分钟后才可以进行同步订单*/
    int updateAmazonEmpowerDataReset();
}