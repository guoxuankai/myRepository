package com.rondaful.cloud.commodity.vo;

import com.rondaful.cloud.commodity.entity.CommodityBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author: luozheng
 * @BelongsPackage:com.rondaful.cloud.commodity.vo
 * @Date: 2019-04-26 18:22:22
 * @FileName:${FILENAME}
 * @Description: 卖家首页根据二级分类显示分页商品（10条）
 */
@ApiModel("分类显示商品VO类")
public class CategoryCommodityVO {
    @ApiModelProperty(value = "二级分类id")
    private Integer categoryId;
    @ApiModelProperty(value = "二级分类对应的商品")
    private List<CommodityBase> commodityBases;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public List<CommodityBase> getCommodityBases() {
        return commodityBases;
    }

    public void setCommodityBases(List<CommodityBase> commodityBases) {
        this.commodityBases = commodityBases;
    }
}
