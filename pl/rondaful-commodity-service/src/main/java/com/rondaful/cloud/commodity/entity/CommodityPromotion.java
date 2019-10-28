package com.rondaful.cloud.commodity.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @Author: luozheng
 * @BelongsPackage:com.rondaful.cloud.commodity.entity
 * @Date: 2019-04-25 13:45:02
 * @FileName:${FILENAME}
 * @Description:
 */
public class CommodityPromotion {
    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = -274506423907123160L;
    @ApiModelProperty("卖家推广id")
    private Integer promotionId;
    @ApiModelProperty("商品推广创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;
    @ApiModelProperty("商品id，关联commodity_baseId")
    private Long commodityId;
    @ApiModelProperty("创建角色账号")
    private String createUserName;
    @ApiModelProperty("销售类型")
    private Integer saleType;

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }
}
