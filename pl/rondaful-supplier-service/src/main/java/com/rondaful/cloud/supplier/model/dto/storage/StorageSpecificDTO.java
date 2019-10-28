package com.rondaful.cloud.supplier.model.dto.storage;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
public class StorageSpecificDTO implements Serializable {

    private static final long serialVersionUID = -1171640152359174093L;
    @ApiModelProperty(value = "主入库单id")
    private Long id;

    @ApiModelProperty(value = "transit_type=3特有。服务方式(通过获取服务方式接口获取)")
    private String smCode;

    private String smCodeName;

    @ApiModelProperty(value = "transit_type=3特有。重量(KG)")
    private BigDecimal weight;

    @ApiModelProperty(value = "transit_type=3特有。体积（m³）。注：必须为正数，最多保留2位小数且不大约1000")
    private BigDecimal volume;

    @ApiModelProperty(value = "transit_type=3特有。国内中转仓仓库代码")
    private String transitWarehouseCode;

    private String transitWarehouseCodeName;

    @ApiModelProperty(value = "transit_type=3特有。提单类型,0:电放;1:正本")
    private Integer pickupForm;

    @ApiModelProperty(value = "transit_type=3特有。报关项,0:EDI报关,1:委托报关,2:报关自理")
    private Integer customsType;

    @ApiModelProperty(value = "transit_type=3特有。url报关附件customs_type=1时必填； 1.需要上传的报关资料包括：装箱单、发票、合同、报关委托书、报关草单； 2.支持上传一个rar/zip格式文件，并且不超过20M。报关文件；")
    private String clearanceFile;

    @ApiModelProperty(value = "transit_type=3特有。发票文件base64转码,仅限xlsx模板文件")
    private String invoiceBase64;

    @ApiModelProperty(value = "	transit_type=3特有。揽收服务,0:自送货物,1:上门提货")
    private Integer collectingService;

    @ApiModelProperty(value = "transit_type=3特有。揽收时间。注：揽收时间不允许小于创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date collectingTime;

    @ApiModelProperty(value = "transit_type=3特有。增值服务，可选值:world_ease(worldease服务) origin_crt(产地证) fumigation(熏蒸 )")
    private String valueAddService;

    @ApiModelProperty(value = "transit_type=3特有。是否自有税号清关。0：否 ；1：是。注：当warehouse_code开启了增值服务，必为1；如没开启，可选0或者1")
    private Integer clearanceService;

    @ApiModelProperty(value = "transit_type=3特有。出口商编码。必填条件：clearance_service=1（自有税号清关）")
    private Integer exportCompany;

    @ApiModelProperty(value = "transit_type=3特有。进口商编码。必填条件：clearance_service=1（自有税号清关）")
    private Integer importCompany;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSmCode() {
        return smCode;
    }

    public void setSmCode(String smCode) {
        this.smCode = smCode;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public String getTransitWarehouseCode() {
        return transitWarehouseCode;
    }

    public void setTransitWarehouseCode(String transitWarehouseCode) {
        this.transitWarehouseCode = transitWarehouseCode;
    }

    public Integer getPickupForm() {
        return pickupForm;
    }

    public void setPickupForm(Integer pickupForm) {
        this.pickupForm = pickupForm;
    }

    public Integer getCustomsType() {
        return customsType;
    }

    public void setCustomsType(Integer customsType) {
        this.customsType = customsType;
    }

    public String getClearanceFile() {
        return clearanceFile;
    }

    public void setClearanceFile(String clearanceFile) {
        this.clearanceFile = clearanceFile;
    }

    public String getInvoiceBase64() {
        return invoiceBase64;
    }

    public void setInvoiceBase64(String invoiceBase64) {
        this.invoiceBase64 = invoiceBase64;
    }

    public Integer getCollectingService() {
        return collectingService;
    }

    public void setCollectingService(Integer collectingService) {
        this.collectingService = collectingService;
    }

    public Date getCollectingTime() {
        return collectingTime;
    }

    public void setCollectingTime(Date collectingTime) {
        this.collectingTime = collectingTime;
    }

    public String getValueAddService() {
        return valueAddService;
    }

    public void setValueAddService(String valueAddService) {
        this.valueAddService = valueAddService;
    }

    public Integer getClearanceService() {
        return clearanceService;
    }

    public void setClearanceService(Integer clearanceService) {
        this.clearanceService = clearanceService;
    }

    public Integer getExportCompany() {
        return exportCompany;
    }

    public void setExportCompany(Integer exportCompany) {
        this.exportCompany = exportCompany;
    }

    public Integer getImportCompany() {
        return importCompany;
    }

    public void setImportCompany(Integer importCompany) {
        this.importCompany = importCompany;
    }

    public String getTransitWarehouseCodeName() {
        return transitWarehouseCodeName;
    }

    public void setTransitWarehouseCodeName(String transitWarehouseCodeName) {
        this.transitWarehouseCodeName = transitWarehouseCodeName;
    }

    public String getSmCodeName() {
        return smCodeName;
    }

    public void setSmCodeName(String smCodeName) {
        this.smCodeName = smCodeName;
    }
}
