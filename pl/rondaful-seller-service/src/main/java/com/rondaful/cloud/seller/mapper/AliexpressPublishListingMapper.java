package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.dto.AliexpressPublishListingDTO;
import com.rondaful.cloud.seller.dto.AliexpressPublishListingExcelDTO;
import com.rondaful.cloud.seller.entity.AliexpressPublishListing;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishListingMobile;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishModel;
import com.rondaful.cloud.seller.vo.AliexpressPublishListingSearchVO;
import com.rondaful.cloud.seller.vo.ResultPublishListingVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AliexpressPublishListingMapper extends BaseMapper<AliexpressPublishListing> {

    public List<AliexpressPublishListingDTO> findPage(AliexpressPublishListingSearchVO vo);

    /**
     * 导出
     * @param vo
     * @return
     */
    public List<AliexpressPublishListingExcelDTO> findAllExcel(AliexpressPublishListingSearchVO vo);


    /**
     * 查询移动端显示列表
     * @param sellerId 卖家
     * @return 列表
     */
    List<AliexpressPublishListingMobile> getAllMobile(@Param("sellerId") String sellerId,@Param("empowerIds")List<Integer> empowerIds);

    public AliexpressPublishListingMobile getAliexpressPublishListingMobileById(Long id);

    /**
     * 根据商品id查询
     * @param itemId
     * @return
     */
    public AliexpressPublishListing getAliexpressPublishListingByItemId(@Param("itemId")Long itemId,@Param("empowerId")Long empowerId);

    /**
     * 查询订单状态为4审核中的数据 定时查询是否审核成功
     * @return
     */
    List<AliexpressPublishModel> getAliexpressPublishModelList(@Param("publishStatus")Integer publishStatus);

    public Integer getUserNameCount(@Param("plAccount")String plAccount);

    /**
     * 统计sku刊登数据量
     * @return
     */
    List<Map<String,Object>> getAliexpressSkuNumber();


    List<ResultPublishListingVO> getAliexpressResultPublishListingVO(@Param("platformSkus")List<String> platformSkus,@Param("empowerId")Integer empowerId);

}