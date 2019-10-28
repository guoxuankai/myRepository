package com.rondaful.cloud.seller.entity.aliexpress;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingProduct;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 队列的对象
 * @author chenhan
 *
 */
public class AliexpressPhotoModel {

    private int type=0;//0图片1速卖通商品同步2单个图片路径查询同步3分页查询商品
    //卖家id
    private Long sellerId;
    //刊登账号
    private Long empowerId;
    private String token;
    private Long pageSize;
    private Long currentPage;
    //查询类型
    private String locationType;
    //
    private Long userId;
    private String userName;
    private Long itemId;
    private String productStatusType;
    private String productMaxPrice;

    private String productMinPrice;
    //图片url
    private String imageUrl;

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getEmpowerId() {
        return empowerId;
    }

    public void setEmpowerId(Long empowerId) {
        this.empowerId = empowerId;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getProductStatusType() {
        return productStatusType;
    }

    public void setProductStatusType(String productStatusType) {
        this.productStatusType = productStatusType;
    }

    public String getProductMaxPrice() {
        return productMaxPrice;
    }

    public void setProductMaxPrice(String productMaxPrice) {
        this.productMaxPrice = productMaxPrice;
    }

    public String getProductMinPrice() {
        return productMinPrice;
    }

    public void setProductMinPrice(String productMinPrice) {
        this.productMinPrice = productMinPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
