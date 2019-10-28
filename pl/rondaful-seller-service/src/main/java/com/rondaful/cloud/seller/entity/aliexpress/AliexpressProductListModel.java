package com.rondaful.cloud.seller.entity.aliexpress;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * 参考文档
 * https://developers.aliexpress.com/doc.htm?docId=30172&docType=2&source=search
 * @author chenhan
 *
 */
public class AliexpressProductListModel implements Serializable {

    private List<AliexpressProductModel> aeopAEProductDisplayDTOList = Lists.newArrayList();

    private String success;
    private Long currentPage;
    private Long totalPage;
    private Long productCount;

    public List<AliexpressProductModel> getAeopAEProductDisplayDTOList() {
        return aeopAEProductDisplayDTOList;
    }

    public void setAeopAEProductDisplayDTOList(List<AliexpressProductModel> aeopAEProductDisplayDTOList) {
        this.aeopAEProductDisplayDTOList = aeopAEProductDisplayDTOList;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }
}
