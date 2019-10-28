package com.rondaful.cloud.seller.entity.amazon;

import com.rondaful.cloud.seller.generated.ProductImage;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

public class AmazonSubRequestProduct {

    /** 品连sku */
    @ApiModelProperty(value="品连sku",required=true)
    @NotBlank(message="品连sku不能为空")
    private String plSku;

    /** 平台sku 对应product.xsd.SKU */
    @ApiModelProperty(value="平台sku",required=true)
    @NotBlank(message="平台sku不能为空")
    private String sku;

    /** 标题/父体标题  Product.xsd.DescriptionData.title */
    @ApiModelProperty(value="标题/父体标题 ",required=true)
    @NotBlank(message="标题不能为空")
    private String title;

    /** 品牌 DescriptionData.brand  */
    @ApiModelProperty(value="品牌")
    private String brand;

    /** Product.xsd.manufacturer 制造商  */
    @ApiModelProperty(value="制造商 ")
    @NotBlank(message="制造商不能为空")
    private String manufacturer;

    /** partNumber Product.xsd.MfrPartNumber 零件号码*/
    @ApiModelProperty(value="零件号码 ")
    @NotBlank(message="Part Number不能为空")
    private String mfrPartNumber;

    /** 图片类型 图片地址*/
    @ApiModelProperty(value="图片列表 ",required=true)
    private List<ProductImage> images = new ArrayList<>();


    /** 库存数量   */
    @ApiModelProperty(value="库存数量",required=true)
    @Min(value=1,message="库存数量不能小于1")
    @Max(value=999999999,message="库存数量不能大于999999999")
    private Long quantity;


    public String getPlSku() {
        return plSku;
    }

    public void setPlSku(String plSku) {
        this.plSku = plSku;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getMfrPartNumber() {
        return mfrPartNumber;
    }

    public void setMfrPartNumber(String mfrPartNumber) {
        this.mfrPartNumber = mfrPartNumber;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
