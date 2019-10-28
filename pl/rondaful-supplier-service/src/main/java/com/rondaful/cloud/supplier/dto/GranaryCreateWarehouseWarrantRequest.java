package com.rondaful.cloud.supplier.dto;

import com.rondaful.cloud.supplier.entity.WarehouseAddresserInfo;
import com.rondaful.cloud.supplier.entity.WarehouseWarrantDetail;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 谷仓创建入库单列表API
 *
 * @ClassName WarehouseWarrantResponse
 * @Author tianye
 * @Date 2019/4/28 9:41
 * @Version 1.0
 */
public class GranaryCreateWarehouseWarrantRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "参考号")
    private String reference_no;

    @ApiModelProperty(value = "入库单类型：0:标准入库单 3-中转入库单 5-FBA入库单")
    private int transit_type;

    @ApiModelProperty(value = "入库单号")
    private String receiving_code;

    @ApiModelProperty(value = "运输方式：0：空运，1：海运散货 2：快递，3：铁运 ，4：海运整柜")
    private int receiving_shipping_type;

    @ApiModelProperty(value = "跟踪号/海柜号 。 必填条件： verify=1（入库单提交审核）时必填")
    private String tracking_number;

    @ApiModelProperty(value = "海外仓仓库编码")
    private String warehouse_code;

    @ApiModelProperty(value = "预计到达时间 yyyy-MM-dd")
    private String eta_date;

    @ApiModelProperty(value = "备注")
    private String receiving_desc;

    @ApiModelProperty(value = "是否审核值0：新建不审核(草稿状态)，值1：新建并审核，默认为0 ，审核通过之后，不可编辑")
    private int verify;

    @ApiModelProperty(value = "入库单明细")
    private List<WarrantDetailDTO> items;

    @ApiModelProperty(value = "入库单发货地址")
    private ShiperAddress shiper_address;

    @ApiModelProperty(value = "增值税信息。必填条件：warehouse_code 开启了清关税服务")
    private Vat vat;

    @ApiModelProperty(value = "transit_type=3特有。服务方式(通过获取服务方式接口获取)")
    private String sm_code;

    @ApiModelProperty(value = "transit_type=3特有。重量(KG)")
    private BigDecimal weight;

    @ApiModelProperty(value = "transit_type=3特有。体积（m³）。注：必须为正数，最多保留2位小数且不大约1000")
    private BigDecimal volume;

    @ApiModelProperty(value = "transit_type=3特有。国内中转仓仓库代码")
    private String transit_warehouse_code;

    @ApiModelProperty(value = "transit_type=3特有。提单类型,0:电放;1:正本")
    private int pickup_form;

    @ApiModelProperty(value = "transit_type=3特有。报关项,0:EDI报关,1:委托报关,2:报关自理")
    private int customs_type;

    @ApiModelProperty(value = "transit_type=3特有。报关附件customs_type=1时必填")
    private ClearanceFile clearance_file;

    @ApiModelProperty(value = "transit_type=3特有。发票文件base64转码,仅限xlsx模板文件")
    private String invoice_base64;

    @ApiModelProperty(value = "transit_type=3特有。揽收服务,0:自送货物,1:上门提货")
    private int collecting_service;

    @ApiModelProperty(value = "transit_type=3特有。揽收资料。必填条件：当collection_service=1（上门揽收）时必填")
    private List<CollectingAddress> collecting_address;

    @ApiModelProperty(value = "transit_type=3特有。揽收时间。注：揽收时间不允许小于创建时间 yyyy-MM-dd")
    private String collecting_time;

    @ApiModelProperty(value = "transit_type=3特有。增值服务，可选值:world_ease(worldease服务) origin_crt(产地证) fumigation(熏蒸)")
    private String value_add_service;

    @ApiModelProperty(value = "transit_type=3特有。是否自有税号清关。0：否 ；1：是。注：当warehouse_code开启了增值服务，必为1；如没开启，可选0或者1")
    private int clearance_service;

    @ApiModelProperty(value = "transit_type=3特有。出口商代码。必填条件：clearance_service=1（自有税号清关）")
    private int export_company;

    @ApiModelProperty(value = "transit_type=3特有。进口商代码。必填条件：clearance_service=1（自有税号清关）")
    private int import_company;

    public String getReceiving_code() {
        return receiving_code;
    }

    public void setReceiving_code(String receiving_code) {
        this.receiving_code = receiving_code;
    }

    public String getReference_no() {
        return reference_no;
    }

    public void setReference_no(String reference_no) {
        this.reference_no = reference_no;
    }

    public int getTransit_type() {
        return transit_type;
    }

    public void setTransit_type(int transit_type) {
        this.transit_type = transit_type;
    }

    public int getReceiving_shipping_type() {
        return receiving_shipping_type;
    }

    public void setReceiving_shipping_type(int receiving_shipping_type) {
        this.receiving_shipping_type = receiving_shipping_type;
    }

    public String getTracking_number() {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number;
    }

    public String getWarehouse_code() {
        return warehouse_code;
    }

    public void setWarehouse_code(String warehouse_code) {
        this.warehouse_code = warehouse_code;
    }

    public String getEta_date() {
        return eta_date;
    }

    public void setEta_date(String eta_date) {
        this.eta_date = eta_date;
    }

    public String getReceiving_desc() {
        return receiving_desc;
    }

    public void setReceiving_desc(String receiving_desc) {
        this.receiving_desc = receiving_desc;
    }

    public int getVerify() {
        return verify;
    }

    public void setVerify(int verify) {
        this.verify = verify;
    }

    public List<WarrantDetailDTO> getItems() {
        return items;
    }

    public void setItems(List<WarrantDetailDTO> items) {
        this.items = items;
    }

    public ShiperAddress getShiper_address() {
        return shiper_address;
    }

    public void setShiper_address(ShiperAddress shiper_address) {
        this.shiper_address = shiper_address;
    }

    public Vat getVat() {
        return vat;
    }

    public void setVat(Vat vat) {
        this.vat = vat;
    }

    public String getSm_code() {
        return sm_code;
    }

    public void setSm_code(String sm_code) {
        this.sm_code = sm_code;
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

    public String getTransit_warehouse_code() {
        return transit_warehouse_code;
    }

    public void setTransit_warehouse_code(String transit_warehouse_code) {
        this.transit_warehouse_code = transit_warehouse_code;
    }

    public int getPickup_form() {
        return pickup_form;
    }

    public void setPickup_form(int pickup_form) {
        this.pickup_form = pickup_form;
    }

    public int getCustoms_type() {
        return customs_type;
    }

    public void setCustoms_type(int customs_type) {
        this.customs_type = customs_type;
    }

    public ClearanceFile getClearance_file() {
        return clearance_file;
    }

    public void setClearance_file(String fileBase64, String type) {
        this.clearance_file = new ClearanceFile(fileBase64, type);
    }

    public String getInvoice_base64() {
        return invoice_base64;
    }

    public void setInvoice_base64(String invoice_base64) {
        this.invoice_base64 = invoice_base64;
    }

    public int getCollecting_service() {
        return collecting_service;
    }

    public void setCollecting_service(int collecting_service) {
        this.collecting_service = collecting_service;
    }

    public List<CollectingAddress> getCollecting_address() {
        return collecting_address;
    }

    public void setCollecting_address(WarehouseAddresserInfo addresserInfo) {
        this.collecting_address = Collections.singletonList(new CollectingAddress(addresserInfo));
    }

    public String getCollecting_time() {
        return collecting_time;
    }

    public void setCollecting_time(String collecting_time) {
        this.collecting_time = collecting_time;
    }

    public String getValue_add_service() {
        return value_add_service;
    }

    public void setValue_add_service(String value_add_service) {
        this.value_add_service = value_add_service;
    }

    public int getClearance_service() {
        return clearance_service;
    }

    public void setClearance_service(int clearance_service) {
        this.clearance_service = clearance_service;
    }

    public int getExport_company() {
        return export_company;
    }

    public void setExport_company(int export_company) {
        this.export_company = export_company;
    }

    public int getImport_company() {
        return import_company;
    }

    public void setImport_company(int import_company) {
        this.import_company = import_company;
    }

    /**
     * 添加入库单商品明细
     *
     * @param detailList
     */
    public void setItemsByWarehouseWarrantDetail(List<WarehouseWarrantDetail> detailList) {
        // 入库单商品明细
        List<WarrantDetailDTO> warrantDetailDTOList = new ArrayList<>();
        // 按箱号分组
        Map<Integer, List<WarehouseWarrantDetail>> listMap = detailList.stream().collect(Collectors.groupingBy(WarehouseWarrantDetail::getBoxNo));
        listMap.forEach((k, v) -> {
            WarrantDetailDTO detailDTO = new WarrantDetailDTO();
            detailDTO.setBox_no(k);
            List<BoxDetail> boxDetailList = new ArrayList<>();
            v.forEach(warrantDetail -> boxDetailList.add(new BoxDetail(warrantDetail.getProductSku(), warrantDetail.getQuantityShipped())));
            detailDTO.setBox_details(boxDetailList);
            warrantDetailDTOList.add(detailDTO);
        });
        this.items = warrantDetailDTOList;
    }

    /**
     * 添加增值税号、增值税豁免号信息
     *
     * @param vatNumber
     * @param exemptionNumber
     * @param eori
     */
    public void setVatByWarehouseWarrant(String vatNumber, String exemptionNumber, String eori) {
        Vat vat;
        if (StringUtils.isNotBlank(vatNumber)) {
            vat = new Vat(vatNumber, eori, true);
        } else {
            vat = new Vat(exemptionNumber, eori, false);
        }
        this.vat = vat;
    }

    private class CollectingAddress implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "揽收联系人-名")
        private String ca_first_name;

        @ApiModelProperty(value = "揽收联系人-姓")
        private String ca_last_name;

        @ApiModelProperty(value = "揽收联系人电话")
        private String ca_contact_phone;

        @ApiModelProperty(value = "揽收地址州/省份")
        private String ca_state;

        @ApiModelProperty(value = "揽收地址城市")
        private String ca_city;

        @ApiModelProperty(value = "揽收地址国家")
        private String ca_country_code;

        @ApiModelProperty(value = "揽收地址邮编")
        private String ca_zipcode;

        @ApiModelProperty(value = "揽收地址1")
        private String ca_address1;

        @ApiModelProperty(value = "揽收地址2")
        private String ca_address2;

        public CollectingAddress() {
        }

        public CollectingAddress(WarehouseAddresserInfo addresserInfo) {
            this.ca_first_name = addresserInfo.getCaFirstName();
            this.ca_last_name = addresserInfo.getCaLastName();
            this.ca_contact_phone = addresserInfo.getCaContactPhone();
            this.ca_state = addresserInfo.getCaState();
            this.ca_city = addresserInfo.getCaCity();
            this.ca_country_code = addresserInfo.getCaCountryCode();
            this.ca_zipcode = addresserInfo.getCaZipcode();
            this.ca_address1 = addresserInfo.getCaAddress1();
            this.ca_address2 = addresserInfo.getCaAddress2();
        }

        public CollectingAddress(String ca_first_name, String ca_last_name, String ca_contact_phone, String ca_state, String ca_city, String ca_country_code, String ca_zipcode, String ca_address1, String ca_address2) {
            this.ca_first_name = ca_first_name;
            this.ca_last_name = ca_last_name;
            this.ca_contact_phone = ca_contact_phone;
            this.ca_state = ca_state;
            this.ca_city = ca_city;
            this.ca_country_code = ca_country_code;
            this.ca_zipcode = ca_zipcode;
            this.ca_address1 = ca_address1;
            this.ca_address2 = ca_address2;
        }

        public String getCa_first_name() {
            return ca_first_name;
        }

        public void setCa_first_name(String ca_first_name) {
            this.ca_first_name = ca_first_name;
        }

        public String getCa_last_name() {
            return ca_last_name;
        }

        public void setCa_last_name(String ca_last_name) {
            this.ca_last_name = ca_last_name;
        }

        public String getCa_contact_phone() {
            return ca_contact_phone;
        }

        public void setCa_contact_phone(String ca_contact_phone) {
            this.ca_contact_phone = ca_contact_phone;
        }

        public String getCa_state() {
            return ca_state;
        }

        public void setCa_state(String ca_state) {
            this.ca_state = ca_state;
        }

        public String getCa_city() {
            return ca_city;
        }

        public void setCa_city(String ca_city) {
            this.ca_city = ca_city;
        }

        public String getCa_country_code() {
            return ca_country_code;
        }

        public void setCa_country_code(String ca_country_code) {
            this.ca_country_code = ca_country_code;
        }

        public String getCa_zipcode() {
            return ca_zipcode;
        }

        public void setCa_zipcode(String ca_zipcode) {
            this.ca_zipcode = ca_zipcode;
        }

        public String getCa_address1() {
            return ca_address1;
        }

        public void setCa_address1(String ca_address1) {
            this.ca_address1 = ca_address1;
        }

        public String getCa_address2() {
            return ca_address2;
        }

        public void setCa_address2(String ca_address2) {
            this.ca_address2 = ca_address2;
        }
    }

    /**
     * 报关附件信息
     */
    private class ClearanceFile implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "清关附件Base64转码,customs_type=1时必填")
        private String clearance_base64;

        @ApiModelProperty(value = "清关附件文件类型:pdf、png、jpg、jpeg，zip 、rar")
        private String clearance_type;

        public ClearanceFile(String clearance_base64, String clearance_type) {
            this.clearance_base64 = clearance_base64;
            this.clearance_type = clearance_type;
        }

        public String getClearance_base64() {
            return clearance_base64;
        }

        public void setClearance_base64(String clearance_base64) {
            this.clearance_base64 = clearance_base64;
        }

        public String getClearance_type() {
            return clearance_type;
        }

        public void setClearance_type(String clearance_type) {
            this.clearance_type = clearance_type;
        }
    }

    /**
     * 增值税信息
     */
    private class Vat implements Serializable {

        private static final long serialVersionUID = 1L;


        @ApiModelProperty(value = "增值税号，与增值税豁免号2选1。")
        private String vat_number;

        @ApiModelProperty(value = "增值税豁免号，与增值税号2选1。")
        private String exemption_number;

        @ApiModelProperty(value = "EORI")
        private String eori;

        public Vat() {
        }

        public Vat(String number, String eori, boolean flag) {
            if (flag) {
                this.vat_number = number;
            } else {
                this.exemption_number = number;
            }
            this.eori = eori;
        }

        public String getVat_number() {
            return vat_number;
        }

        public void setVat_number(String vat_number) {
            this.vat_number = vat_number;
        }

        public String getExemption_number() {
            return exemption_number;
        }

        public void setExemption_number(String exemption_number) {
            this.exemption_number = exemption_number;
        }

        public String getEori() {
            return eori;
        }

        public void setEori(String eori) {
            this.eori = eori;
        }
    }

    /**
     * 入库单明细
     */
    private class WarrantDetailDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        public WarrantDetailDTO() {
        }

        @ApiModelProperty(value = "箱号")
        private int box_no;

        @ApiModelProperty(value = "箱子参考号")
        private String reference_box_no;

        @ApiModelProperty(value = "箱号明细")
        private List<BoxDetail> box_details;

        public int getBox_no() {
            return box_no;
        }

        public void setBox_no(int box_no) {
            this.box_no = box_no;
        }

        public String getReference_box_no() {
            return reference_box_no;
        }

        public void setReference_box_no(String reference_box_no) {
            this.reference_box_no = reference_box_no;
        }

        public List<BoxDetail> getBox_details() {
            return box_details;
        }

        public void setBox_details(List<BoxDetail> box_details) {
            this.box_details = box_details;
        }
    }

    /**
     * 箱号明细
     */
    private class BoxDetail implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "商品SKU")
        private String product_sku;
        @ApiModelProperty(value = "数量")
        private Long quantity;
        @ApiModelProperty(value = "FBA商品编码。必填条件：transit_type=5（FBA入库单）")
        private String fba_product_code;

        public BoxDetail() {
        }

        public BoxDetail(String product_sku, Long quantity) {
            this.product_sku = product_sku;
            this.quantity = quantity;
        }

        public BoxDetail(String product_sku, Long quantity, String fba_product_code) {
            this.product_sku = product_sku;
            this.quantity = quantity;
            this.fba_product_code = fba_product_code;
        }

        public String getProduct_sku() {
            return product_sku;
        }

        public void setProduct_sku(String product_sku) {
            this.product_sku = product_sku;
        }

        public Long getQuantity() {
            return quantity;
        }

        public void setQuantity(Long quantity) {
            this.quantity = quantity;
        }

        public String getFba_product_code() {
            return fba_product_code;
        }

        public void setFba_product_code(String fba_product_code) {
            this.fba_product_code = fba_product_code;
        }
    }

    /**
     * 入库单发货地址
     */
    private class ShiperAddress implements Serializable {

        private static final long serialVersionUID = 1L;

        public ShiperAddress() {
        }

        public ShiperAddress(String sa_contacter, String sa_contact_phone, String sa_country_code, String sa_state, String sa_city, String sa_region, String sa_address1, String sa_address2) {
            this.sa_contacter = sa_contacter;
            this.sa_contact_phone = sa_contact_phone;
            this.sa_country_code = sa_country_code;
            this.sa_state = sa_state;
            this.sa_city = sa_city;
            this.sa_region = sa_region;
            this.sa_address1 = sa_address1;
            this.sa_address2 = sa_address2;
        }

        @ApiModelProperty(value = "联系人")
        private String sa_contacter;

        @ApiModelProperty(value = "联系电话（手机号）")
        private String sa_contact_phone;

        @ApiModelProperty(value = "发件国家简称")
        private String sa_country_code;

        @ApiModelProperty(value = "省/州")
        private String sa_state;

        @ApiModelProperty(value = "城市")
        private String sa_city;

        @ApiModelProperty(value = "区")
        private String sa_region;

        @ApiModelProperty(value = "地址1")
        private String sa_address1;

        @ApiModelProperty(value = "地址2")
        private String sa_address2;

        public String getSa_contacter() {
            return sa_contacter;
        }

        public void setSa_contacter(String sa_contacter) {
            this.sa_contacter = sa_contacter;
        }

        public String getSa_contact_phone() {
            return sa_contact_phone;
        }

        public void setSa_contact_phone(String sa_contact_phone) {
            this.sa_contact_phone = sa_contact_phone;
        }

        public String getSa_country_code() {
            return sa_country_code;
        }

        public void setSa_country_code(String sa_country_code) {
            this.sa_country_code = sa_country_code;
        }

        public String getSa_state() {
            return sa_state;
        }

        public void setSa_state(String sa_state) {
            this.sa_state = sa_state;
        }

        public String getSa_city() {
            return sa_city;
        }

        public void setSa_city(String sa_city) {
            this.sa_city = sa_city;
        }

        public String getSa_region() {
            return sa_region;
        }

        public void setSa_region(String sa_region) {
            this.sa_region = sa_region;
        }

        public String getSa_address1() {
            return sa_address1;
        }

        public void setSa_address1(String sa_address1) {
            this.sa_address1 = sa_address1;
        }

        public String getSa_address2() {
            return sa_address2;
        }

        public void setSa_address2(String sa_address2) {
            this.sa_address2 = sa_address2;
        }
    }

}
