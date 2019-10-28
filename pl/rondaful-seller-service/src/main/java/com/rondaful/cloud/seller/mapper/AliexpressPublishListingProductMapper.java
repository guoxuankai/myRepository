package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AliexpressPublishListingProductMapper extends BaseMapper<AliexpressPublishListingProduct> {

   public List<AliexpressPublishListingProduct> getPublishListingProductByListingId(Long publishListingId);

   public List<AliexpressPublishListingProduct> getPublishListingProductByListingIds(@Param("publishListingIds") List<Long> publishListingIds);

   /**
    * 根据刊登id删除数据
    * @param publishListingId
    * @return
    */
   public int deleteByListingId(Long publishListingId);

   /**
    * 验证平台sku是否重复
    * @param map
    * @return
    */
   public List<AliexpressPublishListingProduct> getPublishListingProductByPlatformSku(@Param("map") Map<String,Object> map,@Param("publishListingId")Long publishListingId);


   /**
    * 验证平台sku是否重复
    * @param map
    * @return
    */
   public List<AliexpressPublishListingProduct> getProductByPlatformSku(@Param("platformSku") String platformSku,@Param("publishListingId")Long publishListingId);

}