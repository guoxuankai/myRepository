package com.rondaful.cloud.transorder.entity.commodity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品规格表
 * 实体类对应的数据表为：  t_commodity_spec
 * @author ZJL
 * @date 2019-3-18 09:26:01
 */
@Data
@ApiModel(value ="CommoditySpec")
public class CommoditySpec implements Serializable {
    @ApiModelProperty(value = "唯一id")
    private Long id;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "关联商品id")
    private Long commodityId;

    @Excel(name = "品连SPU" ,orderNum = "0")
    @ApiModelProperty(value = "SPU")
    private String SPU;

    @Excel(name = "品连SKU" ,orderNum = "1")
    @ApiModelProperty(value = "系统sku")
    private String systemSku;

    @Excel(name = "供应商SKU" ,orderNum = "2")
    @ApiModelProperty(value = "供应商sku")
    private String supplierSku;

    @Excel(name = "品牌" ,orderNum = "3")
    @ApiModelProperty(value = "商品品牌")
    private String brandName;

    @Excel(name = "生产厂商" ,orderNum = "4")
    @ApiModelProperty(value = "生产商")
    private String producer;

    @Excel(name = "商品长" ,orderNum = "5")
    @ApiModelProperty(value = "商品尺寸长度")
    private BigDecimal commodityLength;

    @Excel(name = "商品宽" ,orderNum = "6")
    @ApiModelProperty(value = "商品尺寸宽度")
    private BigDecimal commodityWidth;

    @Excel(name = "商品高" ,orderNum = "7")
    @ApiModelProperty(value = "商品尺寸高度")
    private BigDecimal commodityHeight;

    @Excel(name = "商品重量" ,orderNum = "8")
    @ApiModelProperty(value = "商品重量")
    private BigDecimal commodityWeight;

    @Excel(name = "包装长" ,orderNum = "9")
    @ApiModelProperty(value = "包装尺寸长度", required = true)
    private BigDecimal packingLength;

    @Excel(name = "包装宽" ,orderNum = "10")
    @ApiModelProperty(value = "包装尺寸宽度", required = true)
    private BigDecimal packingWidth;

    @Excel(name = "包装高" ,orderNum = "11")
    @ApiModelProperty(value = "包装尺寸高度", required = true)
    private BigDecimal packingHeight;

    @Excel(name = "包装重量" ,orderNum = "12")
    @ApiModelProperty(value = "包装重量", required = true)
    private BigDecimal packingWeight;

    @Excel(name = "属性" ,orderNum = "13")
    @ApiModelProperty(value = "商品规格列表，属性名:属性值，多个以|隔开")
    private String commoditySpec;

    @Excel(name = "商品价格" ,orderNum = "14")
    @ApiModelProperty(value = "商品价")
    private BigDecimal commodityPrice;

    @Excel(name = "建议零售价" ,orderNum = "15")
    @ApiModelProperty(value = "建议零售价")
    private BigDecimal retailPrice;

    @Excel(name = "商品中文名" ,orderNum = "16")
    @ApiModelProperty(value = "商品中文名称")
    private String commodityNameCn;

    @Excel(name = "商品英文名" ,orderNum = "17")
    @ApiModelProperty(value = "商品英文名称")
    private String commodityNameEn;

    @Excel(name = "搜索关键词英文" ,orderNum = "18")
    private String searchKeywordsEn;

    @Excel(name = "搜索关键词中文" ,orderNum = "19")
    private String searchKeywordsCn;

    @Excel(name = "搜索关键词法语" ,orderNum = "20")
    private String searchKeywordsFr;

    @Excel(name = "搜索关键词德语" ,orderNum = "21")
    private String searchKeywordsDe;

    @Excel(name = "搜索关键词意大利语" ,orderNum = "22")
    private String searchKeywordsIt;

    @Excel(name = "商品亮点英文" ,orderNum = "23")
    private String strengthEn;

    @Excel(name = "商品亮点中文" ,orderNum = "24")
    private String strengthCn;

    @Excel(name = "商品亮点法语" ,orderNum = "25")
    private String strengthFr;

    @Excel(name = "商品亮点德语" ,orderNum = "26")
    private String strengthDe;

    @Excel(name = "商品亮点意大利语" ,orderNum = "27")
    private String strengthIt;

    @Excel(name = "包装清单英文" ,orderNum = "28")
    private String packingListEn;

    @Excel(name = "包装清单中文" ,orderNum = "29")
    private String packingListCn;

    @Excel(name = "包装清单法语" ,orderNum = "30")
    private String packingListFr;

    @Excel(name = "包装清单德语" ,orderNum = "31")
    private String packingListDe;

    @Excel(name = "包装清单意大利语" ,orderNum = "32")
    private String packingListIt;

    @Excel(name = "商品英文描述" ,orderNum = "33")
    private String commodityDescEn;

    @Excel(name = "商品中文描述" ,orderNum = "34")
    private String commodityDescCn;

    @Excel(name = "商品法语描述" ,orderNum = "35")
    private String commodityDescFr;

    @Excel(name = "商品德语描述" ,orderNum = "36")
    private String commodityDescDe;

    @Excel(name = "商品意大利语描述" ,orderNum = "37")
    private String commodityDescIt;

    @Excel(name = "商品特性英文" ,orderNum = "38")
    @ApiModelProperty(value = "产品特性英文，多个以|隔开")
    private String productFeaturesEn;

    @Excel(name = "商品特性中文" ,orderNum = "39")
    @ApiModelProperty(value = "产品特性中文，多个以|隔开")
    private String productFeaturesCn;

    @ApiModelProperty(value = "商品标题")
    private String tittle;

    @ApiModelProperty(value = "SKU附图，多个以|隔开")
    private String additionalPicture;

    @ApiModelProperty(value = "关联属性id，多个以,隔开")
    private String attributeId;

    @ApiModelProperty(value = "商品状态，-1：待提交，0：待审核，1：审核通过，2：审核失败，3：失效")
    private Integer state;

    @ApiModelProperty(value = "是否上架")
    private Boolean isUp;

    @ApiModelProperty(value = "库存")
    private Integer inventory;

    @ApiModelProperty(value = "SKU主图")
    private String masterPicture;

    @ApiModelProperty(value = "审核描述")
    private String auditDesc;

    @ApiModelProperty(value = "供应商id")
    private String supplierId;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "供应商公司名称")
    private String supplierCompanyName;

    @ApiModelProperty(value = "佣金百分比")
    private Double feeRate;

    @ApiModelProperty(value = "佣金，和佣金百分比二选一")
    private Double feePrice;

    @ApiModelProperty(value = "报关中文名")
    private String customsNameCn;

    @ApiModelProperty(value = "报关英文名")
    private String customsNameEn;

    @ApiModelProperty(value = "报关价格")
    private BigDecimal customsPrice;

    @ApiModelProperty(value = "报关重量")
    private BigDecimal customsWeight;

    @ApiModelProperty(value = "海关编码")
    private String customsCode;

    @ApiModelProperty(value = "分类名称")
    private String categoryName;

    @ApiModelProperty(value = "供应商所属供应链公司ID")
    private Integer supChainCompanyId;

    @ApiModelProperty(value = "供应商所属供应链公司名称")
    private String supChainCompanyName;


    @ApiModelProperty(value = "商品价美元")
    private BigDecimal commodityPriceUs;

    @ApiModelProperty(value = "佣金美元")
    private Double feePriceUs;

    @ApiModelProperty(value = "是否包邮。1-包邮，0-不包邮")
    private Integer freeFreight;

    @ApiModelProperty(value = "sku仓库相关信息")
    private List<SkuInventoryVo> inventoryList;
}