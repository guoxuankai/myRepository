package com.rondaful.cloud.commodity.entity;


import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 运费试算实体类
 */
@ApiModel(value ="Trial")
public class Trial implements Serializable {

    @ApiModelProperty(value = "仓库编码" , required = true)
    private String warehouse_code;
    @ApiModelProperty(value = "国家编码" , required = true)
    private String country_code;
    @ApiModelProperty(value = "搜索类型（1-按重量， 2-按sku）" , required = true)
    private String search_type;
    @ApiModelProperty(value = "邮寄方式编码")
    private List<String> shipping_code_arr;
    @ApiModelProperty(value = "长（cm），search_type=1时传该参数")
    private String length;
    @ApiModelProperty(value = "宽（cm）search_type=1时传该参数")
    private String width;
    @ApiModelProperty(value = "高（cm），search_type=1时传该参数")
    private String height;
    @ApiModelProperty(value = "重量（g），search_type=1时传该参数")
    private String weight;
    @ApiModelProperty(value = "sku列表，search_type=2时传该参数")
    private List<Skus> skus;
    @ApiModelProperty(hidden = true)
    private String message;
    @ApiModelProperty(hidden = true)
    private String status;
    @ApiModelProperty(hidden = true)
    private Object data;
    @ApiModelProperty(value = "是否导出Excel")
    private Boolean isExport;
    @ApiModelProperty(value = "商品总额")
    private String sum_money;
    @ApiModelProperty(value = "产品利润")
    private String profit;
    @ApiModelProperty(value = "运费折扣")
    private String freightRebate;
    
    @ApiModelProperty(value = "平台：1-ebay，2-amazon，3-wish，4-aliExpress")
    private String channel_id;
    
    @ApiModelProperty(value = "调用平台 0：erp 1：谷仓", required = true)
	private Integer callPlatform;
    
    @ApiModelProperty(value = "邮政编码 | 谷仓必传")
	private String postCode;
    
    @ApiModelProperty(value = "平台佣金率")
    private String platformFeeRate; 
    
    @ApiModelProperty(value = "汇率币种")
    private String exRateCur;

    
    public String getPlatformFeeRate() {
		return platformFeeRate;
	}

	public void setPlatformFeeRate(String platformFeeRate) {
		this.platformFeeRate = platformFeeRate;
	}

	public String getExRateCur() {
		return exRateCur;
	}

	public void setExRateCur(String exRateCur) {
		this.exRateCur = exRateCur;
	}

	public String getSum_money() {
        return sum_money;
    }

    public void setSum_money(String sum_money) {
        this.sum_money = sum_money;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getFreightRebate() {
        return freightRebate;
    }

    public void setFreightRebate(String freightRebate) {
        this.freightRebate = freightRebate;
    }

    public String getWarehouse_code() {
        return warehouse_code;
    }

    public void setWarehouse_code(String warehouse_code) {
        this.warehouse_code = warehouse_code;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getSearch_type() {
        return search_type;
    }

    public void setSearch_type(String search_type) {
        this.search_type = search_type;
    }

    public List<String> getShipping_code_arr() {
        return shipping_code_arr;
    }

    public void setShipping_code_arr(List<String> shipping_code_arr) {
        this.shipping_code_arr = shipping_code_arr;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public List<Skus> getSkus() {
        return skus;
    }

    public void setSkus(List<Skus> skus) {
        this.skus = skus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public Boolean getIsExport() {
        return isExport;
    }

    public void setIsExport(Boolean export) {
        isExport = export;
    }

    public void setData(Object data) {
        this.data = data;
    }
	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}
	public Integer getCallPlatform() {
		return callPlatform;
	}

	public void setCallPlatform(Integer callPlatform) {
		this.callPlatform = callPlatform;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}





	@ApiModel(value ="Details")
    public static class Details {
        @ApiModelProperty(value = "渠道code")
        private String shipping_code;
       
        @ApiModelProperty(value = "折扣")
        private String shipping_fee_discount;
        
        @ApiModelProperty(value = "国家编码")
        private String currency_code;
        
        @ApiModelProperty(value = "打折前费用（未加燃油费和附加费）  运输服务费")
        private String before_amount;
       
        @ApiModelProperty(value = "挂号费金额")
        private String registered_fee;
        
        @ApiModelProperty(value = "燃油附加费")
        private String oli_additional_fee;
        
        @ApiModelProperty(value = "最快天数（不设置0） 预计运输时效")
        private String earliest_days;
        
        @ApiModelProperty(value = "最忙天数（不设置0）")
        private String latest_days;
        
        @ApiModelProperty(value = "最大限重")
        private String max_weight;
        
        @ApiModelProperty(value = "物流费用总运费（USD）")
        private String amount;
        
        @ApiModelProperty(value = "打折后物流费用   折后运费")
        private String after_discount_amount;
        
        @ApiModelProperty(value = "物流费用总运费（CNY）")
        private String cny_amount;
        
        @ApiModelProperty(value = "渠道简称")
        private String shipping_name;
        
        @ApiModelProperty(value = "处理费 操作费")
        private String handle_fee;
        
        @ApiModelProperty(value = "关税费" )
        private String tariff;
        
        @ApiModelProperty(value = "产品利润")
        private String profit;
        
        @ApiModelProperty(value = "其他费用")
        private String other;
        
        @ApiModelProperty(value = "总售价")
        private String total_sales_price;
        
        @ApiModelProperty(value = "平台佣金")
        private String platformFee; 
        
        @ApiModelProperty(value = "商品成本")
        private String sumMoney;

        
        
        public String getSumMoney() {
			return sumMoney;
		}

		public void setSumMoney(String sumMoney) {
			this.sumMoney = sumMoney;
		}

		public String getPlatformFee() {
			return platformFee;
		}

		public void setPlatformFee(String platformFee) {
			this.platformFee = platformFee;
		}

		public String getTariff() {
            return tariff;
        }

        public void setTariff(String tariff) {
            this.tariff = tariff;
        }

        public String getProfit() {
            return profit;
        }

        public void setProfit(String profit) {
            this.profit = profit;
        }

        public String getOther() {
            return other;
        }

        public void setOther(String other) {
            this.other = other;
        }

        public String getTotal_sales_price() {
            return total_sales_price;
        }

        public void setTotal_sales_price(String total_sales_price) {
            this.total_sales_price = total_sales_price;
        }

        public String getShipping_code() {
            return shipping_code;
        }

        public void setShipping_code(String shipping_code) {
            this.shipping_code = shipping_code;
        }

        public String getShipping_fee_discount() {
            return shipping_fee_discount;
        }

        public void setShipping_fee_discount(String shipping_fee_discount) {
            this.shipping_fee_discount = shipping_fee_discount;
        }

        public String getCurrency_code() {
            return currency_code;
        }

        public void setCurrency_code(String currency_code) {
            this.currency_code = currency_code;
        }

        public String getBefore_amount() {
            return before_amount;
        }

        public void setBefore_amount(String before_amount) {
            this.before_amount = before_amount;
        }

        public String getRegistered_fee() {
            return registered_fee;
        }

        public void setRegistered_fee(String registered_fee) {
            this.registered_fee = registered_fee;
        }

        public String getOli_additional_fee() {
            return oli_additional_fee;
        }

        public void setOli_additional_fee(String oli_additional_fee) {
            this.oli_additional_fee = oli_additional_fee;
        }

        public String getEarliest_days() {
            return earliest_days;
        }

        public void setEarliest_days(String earliest_days) {
            this.earliest_days = earliest_days;
        }

        public String getLatest_days() {
            return latest_days;
        }

        public void setLatest_days(String latest_days) {
            this.latest_days = latest_days;
        }

        public String getMax_weight() {
            return max_weight;
        }

        public void setMax_weight(String max_weight) {
            this.max_weight = max_weight;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAfter_discount_amount() {
            return after_discount_amount;
        }

        public void setAfter_discount_amount(String after_discount_amount) {
            this.after_discount_amount = after_discount_amount;
        }

        public String getCny_amount() {
            return cny_amount;
        }

        public void setCny_amount(String cny_amount) {
            this.cny_amount = cny_amount;
        }

        public String getShipping_name() {
            return shipping_name;
        }

        public void setShipping_name(String shipping_name) {
            this.shipping_name = shipping_name;
        }

        public String getHandle_fee() {
            return handle_fee;
        }

        public void setHandle_fee(String handle_fee) {
            this.handle_fee = handle_fee;
        }
    }

    @ApiModel(value ="Skus")
    public static class Skus {
        @ApiModelProperty(value = "sku")
        private String sku;
        @ApiModelProperty(value = "数量")
        private String num;

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }
    }

}



