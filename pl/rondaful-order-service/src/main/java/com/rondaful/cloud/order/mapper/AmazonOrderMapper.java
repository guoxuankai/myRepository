package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface AmazonOrderMapper extends BaseMapper<AmazonOrder> {

    List<AmazonOrder>  findAmazonOrder();


    void updateplProcessStatusByOrderId( @Param("plProcessStatus") byte plProcessStatus, @Param("orderId")String orderId);

    /**
     * 批量更改亚马逊订单转入状态
     * @param list
     */
    void updateConvertStatusBatch(@Param("list") List<UpdateSourceOrderDTO> list);


    List<AmazonOrder> selectAmazonOrdersByPLProcessStatusAnaPlAccount
            (@Param("marketPlaceId") String marketPlaceId,
            @Param("date") String date,
            @Param("sellerId") String sellerId,
            @Param("plProcessStatus") Byte plProcessStatus);

    AmazonOrder getAmazonOrderDetailByOrderId(String orderId);

    int updateplAmazonPayStatusByOrderId(String plAmazonPayStatus,String orderId);

    int insertBulk(@Param("list") List<AmazonOrder> list);

    AmazonOrder selectAmazonOrderByOrderId(String orderId);

    AmazonOrder onlyQeryAmazonOrderByOrderId(String orderId);
    /*多条件不定查询亚马逊订单*/
    List<AmazonOrder> selectAmazonOrderByMultiCondition(@Param("shopNameIdLists")List<Integer> shopNameIdLists,@Param("orderId")String orderId,
                                                        @Param("plIdLists") List<Integer> plIdLists,@Param("orderStatuss") String orderStatus,@Param("plProcessStatuss") Byte plProcessStatuss,
                                                        @Param("startDate") String startDate,@Param("endDate") String endDate,@Param("bindex")Integer bindex,@Param("num")Integer num);
    /*多条件不定查询亚马逊订单  CMS*/
    List<AmazonOrder> selectAmazonOrderByMultiConditionByCMS(@Param("shopNameIdLists")List<Integer> shopNameIdLists,@Param("orderId")String orderId,
                                                        @Param("plIdLists") List<Integer> plIdLists,@Param("orderStatuss") String orderStatus,@Param("plProcessStatuss") Byte plProcessStatuss,
                                                        @Param("startDate") String startDate,@Param("endDate") String endDate,@Param("bindex")Integer bindex,@Param("num")Integer num);
    /*获取多条件不定查询的总记录条数*/
    Integer selectAmazonOrderByMultiConditionCounts(@Param("shopNameIdLists")List<Integer> shopNameIdLists,@Param("orderId")String orderId,
                                                        @Param("plIdLists") List<Integer> plIdLists,@Param("orderStatuss") String orderStatus,@Param("plProcessStatuss") Byte plProcessStatuss,
                                                        @Param("startDate") String startDate,@Param("endDate") String endDate);
    /*根据品连账号获取最后的更新时间*/
    String selectLastUpdateTimeByPlAccount(@Param("plAccount")String plAccount,@Param("marketPlaceId")String marketPlaceId,@Param("sellerId")String sellerId);

    /*查询该卖家是否存在亚马逊订单表*/
    List<AmazonOrder> selectPlAccountIsExist(@Param("plAccount")String plAccount,@Param("marketPlaceId")String marketPlaceId,@Param("sellerId")String sellerId);

    /**
     * 通过集合ID查询订单
     * @param ids
     * @return
     */
    List<AmazonOrder> selectAmazonOrderByOrderListId(@Param("ids") List<String> ids);

    AmazonOrder selectAmazonOrderOnlyUseConvertOrder(@Param("orderId")String orderId);

}