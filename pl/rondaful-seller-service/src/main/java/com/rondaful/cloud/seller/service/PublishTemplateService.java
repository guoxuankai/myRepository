package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.entity.EbayPublishListingVariant;
import com.rondaful.cloud.seller.entity.PublishTemplate;
import com.rondaful.cloud.seller.vo.PublishTemplateSearchVO;

import java.util.List;
import java.util.Map;

public interface PublishTemplateService {

    /**
     * 列表信息
     * @param vo
     * @return
     * @throws Exception
     */
    Page<PublishTemplate> findPage(PublishTemplateSearchVO vo) throws Exception;

    /**
     * 下拉数据
     * @param site
     * @param templateType
     * @param plAccount
     * @return
     */
    List<PublishTemplate> getPublishTemplateALLList(Integer platform,String site, Integer templateType, String plAccount,Boolean defaultIs,String empowerId);
    /**
     * 新增或者保存
     * @param publishTemplate
     * @return
     */
    PublishTemplate savePublishTemplate(PublishTemplate publishTemplate);

    /**
     * 删除
     * @param id
     * @return
     */
    int deletePublishTemplate(Long id);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    PublishTemplate getPublishTemplateById(Long id);


    /**
     * 验证平台sku是否重复
     * @param list
     * @return
     */
    public List<EbayPublishListingVariant> getVariantByPlatformSku(String platformSku, Long listingId);

    /**
     * sku规则
     * @param platform
     * @param site
     * @param skus
     * @param sellerName
     * @param listingId
     * @param sellerNameNum
     * @param userName
     * @return
     */
    public Map<String,String> findPlatformSku(Integer platform,String site,String skus,String sellerName,Long listingId,Integer sellerNameNum ,String userName);

}
