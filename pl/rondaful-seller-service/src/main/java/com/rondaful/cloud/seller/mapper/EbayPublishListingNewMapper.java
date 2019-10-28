package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.dto.EbayPublishListingAPPDTO;
import com.rondaful.cloud.seller.dto.EbayPublishListingDTO;
import com.rondaful.cloud.seller.entity.EbayPublishListingNew;
import com.rondaful.cloud.seller.entity.ebay.EbayListingMQModel;
import com.rondaful.cloud.seller.vo.EbayMaxTimeVO;
import com.rondaful.cloud.seller.vo.PublishListingAppSearchVO;
import com.rondaful.cloud.seller.vo.PublishListingSearchVO;
import com.rondaful.cloud.seller.vo.ResultPublishListingVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EbayPublishListingNewMapper extends BaseMapper<EbayPublishListingNew> {


    /**
     * pc分页查询
     * @param VO
     * @return
     */
    List<EbayPublishListingDTO> findPage(PublishListingSearchVO VO);


    /**
     * 根据商品itemId查询
     * @param itemId
     * @return
     */
    public EbayPublishListingNew getEbayPublishListingByItemId(@Param("itemId")String itemId,@Param("empowerId")Long empowerId);

    /**
     * 分页查询 历史刊登的商品spu记录
     * @param VO
     * @return
     */
    List<String> getEbayHistoryPage(PublishListingSearchVO VO);

    /**
     * 手机分页
     * @param VO
     * @return
     */
    List<EbayPublishListingAPPDTO> pageByApp(PublishListingAppSearchVO VO);

    /**
     * 统计数量
     * @param seller
     * @return
     */
    int getOnlineCountBySeller(@Param("sellerId") Long sellerId);

    /**
     * 最大备货天数
     */
    List<Map<String,Object>> getDispatchTimeMax(EbayMaxTimeVO vo);

    /**
     * 统计sku刊登数据量
     * @return
     */
    List<Map<String,Object>> getEbaySkuNumber();

    /**
     * 定时下架商品
     * @return
     */
    List<Long> findListingByTask();

    List<Long> getByStatusTask(@Param("status")Integer status);

    List<ResultPublishListingVO> getEbayResultPublishListingVO(@Param("empowerId")Integer empowerId,@Param("platformSkus")List<String> platformSkus);

    List<EbayListingMQModel> getEbayListingMQModelList(@Param("empowerId")Long empowerId, @Param("startTime")Date startTime,@Param("endTime")Date endTime);

}