package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

/**
* @Description:谷仓商品
* @author:范津 
* @date:2019年4月25日 下午2:34:03
 */
public class GoodCangProduct implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//SKU 
	private String product_sku;
	
	//客户参考代码 Option
	private String reference_no;
	
	//商品名称不能超过255个字符
	private String product_name_cn;
	
	//商品英文名称不能超过255个字符,不支持中文
	private String product_name_en;
	
	//重量，单位KG，重量可为0.001-9999.999范围内且最多保留3位小数
	private float product_weight;
	
	//长，单位CM，长可为0.01-9999.99范围内且最多保留2位小数
	private float product_length;
	
	//宽，单位CM，宽可为0.01-9999.99范围内且最多保留2位小数
	private float product_width;
	
	//高，单位CM，高可为0.01-9999.99范围内且最多保留2位小数
	private float product_height;
	
	//货物属性，0普货，1含电池，2纯电池，3纺织品，4易碎品默认为0
	private int contain_battery;
	
	//包裹类型。 0包裹，1信封
	private int type_of_goods;
	
	//申报价值，范围0.01-99999999.99，币种默认为USD
	private float product_declared_value;
	
	//品类语言，zh中文，en英文，默认为zh Option
	private String cat_lang;
	
	//三级品类
	private int cat_id_level2;
	
	//值 0：创建 状态草稿下需要手动在oms 提交审核；值 1：默认推送wms 审核；默认值是 0  Option
	private int verify;
	
	//是否品牌，0否，1是
	private int branded;
	
	//当branded为1时必填，商品品牌，不能超过100个字符；option
	private String product_brand;
	
	//当branded为1时必填，商品型号，不能超过100个字符；option
	private String product_model;
	
	//商品链接,最长1000字符：
	private String product_link;
	
	//枚举值，单位有：台、米、箱等，具体请看成交单位表，默认为PCS， option
	private String unit;
	
	//商品图片链接。最多传5个  Option
	private List<String> image_link;
	
	//出口国关税信息维护
	private JSONArray export_country;
	
	//进口国清关信息维护
	private JSONArray import_country;
	
	//商品状态，X:废弃， D:草稿，S:可用，W:审核中，R:审核不通过
	private String product_status;

	public String getProduct_sku() {
		return product_sku;
	}

	public void setProduct_sku(String product_sku) {
		this.product_sku = product_sku;
	}

	public String getReference_no() {
		return reference_no;
	}

	public void setReference_no(String reference_no) {
		this.reference_no = reference_no;
	}

	public String getProduct_name_cn() {
		return product_name_cn;
	}

	public void setProduct_name_cn(String product_name_cn) {
		this.product_name_cn = product_name_cn;
	}

	public String getProduct_name_en() {
		return product_name_en;
	}

	public void setProduct_name_en(String product_name_en) {
		this.product_name_en = product_name_en;
	}

	public float getProduct_weight() {
		return product_weight;
	}

	public void setProduct_weight(float product_weight) {
		this.product_weight = product_weight;
	}

	public float getProduct_length() {
		return product_length;
	}

	public void setProduct_length(float product_length) {
		this.product_length = product_length;
	}

	public float getProduct_width() {
		return product_width;
	}

	public void setProduct_width(float product_width) {
		this.product_width = product_width;
	}

	public float getProduct_height() {
		return product_height;
	}

	public void setProduct_height(float product_height) {
		this.product_height = product_height;
	}

	public int getContain_battery() {
		return contain_battery;
	}

	public void setContain_battery(int contain_battery) {
		this.contain_battery = contain_battery;
	}

	public int getType_of_goods() {
		return type_of_goods;
	}

	public void setType_of_goods(int type_of_goods) {
		this.type_of_goods = type_of_goods;
	}

	public float getProduct_declared_value() {
		return product_declared_value;
	}

	public void setProduct_declared_value(float product_declared_value) {
		this.product_declared_value = product_declared_value;
	}

	public String getCat_lang() {
		return cat_lang;
	}

	public void setCat_lang(String cat_lang) {
		this.cat_lang = cat_lang;
	}

	public int getCat_id_level2() {
		return cat_id_level2;
	}

	public void setCat_id_level2(int cat_id_level2) {
		this.cat_id_level2 = cat_id_level2;
	}

	public int getVerify() {
		return verify;
	}

	public void setVerify(int verify) {
		this.verify = verify;
	}

	public int getBranded() {
		return branded;
	}

	public void setBranded(int branded) {
		this.branded = branded;
	}

	public String getProduct_brand() {
		return product_brand;
	}

	public void setProduct_brand(String product_brand) {
		this.product_brand = product_brand;
	}

	public String getProduct_model() {
		return product_model;
	}

	public void setProduct_model(String product_model) {
		this.product_model = product_model;
	}

	public String getProduct_link() {
		return product_link;
	}

	public void setProduct_link(String product_link) {
		this.product_link = product_link;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<String> getImage_link() {
		return image_link;
	}

	public void setImage_link(List<String> image_link) {
		this.image_link = image_link;
	}

	public JSONArray getExport_country() {
		return export_country;
	}

	public void setExport_country(JSONArray export_country) {
		this.export_country = export_country;
	}

	public JSONArray getImport_country() {
		return import_country;
	}

	public void setImport_country(JSONArray import_country) {
		this.import_country = import_country;
	}

	public String getProduct_status() {
		return product_status;
	}

	public void setProduct_status(String product_status) {
		this.product_status = product_status;
	}
	
}
