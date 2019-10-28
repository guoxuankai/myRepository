package com.rondaful.cloud.seller.entity.aliexpress;

import com.google.common.collect.Lists;
import com.rondaful.cloud.seller.entity.AliexpressPhoto;

import java.util.List;

/**
 * 队列的对象
 * @author chenhan
 *
 */
public class AliexpressPhotoUrlModel {

    private List<AliexpressPhoto> images = Lists.newArrayList();
    private AliexpressPageModel query;
    private String success;
    private Long total;
    private Long totalPage;

    public List<AliexpressPhoto> getImages() {
        return images;
    }

    public void setImages(List<AliexpressPhoto> images) {
        this.images = images;
    }

    public AliexpressPageModel getQuery() {
        return query;
    }

    public void setQuery(AliexpressPageModel query) {
        this.query = query;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }
}
