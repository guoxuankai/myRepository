package com.rondaful.cloud.supplier.dto;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 谷仓查询中转服务方式API
 *
 * @ClassName GranaryTransferWarehouseResponse
 * @Author tianye
 * @Date 2019/4/28 17:44
 * @Version 1.0
 */
public class GranarySmCodeResponse extends GranaryResponseBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private SmCodeData data;

    public SmCodeData getData() {
        return data;
    }

    public void setData(SmCodeData data) {
        this.data = data;
    }

    public static class SmCodeData{
        @ApiModelProperty(value = "空运服务方式")
        private List<SmCodeDeatil> AIR;

        @ApiModelProperty(value = "海运散货服务方式")
        private List<SmCodeDeatil> LCL;

        @ApiModelProperty(value = "快递服务方式")
        private List<SmCodeDeatil> EXPRESS;

        @ApiModelProperty(value = "铁运服务方式")
        private List<SmCodeDeatil> TRAIN;

        @ApiModelProperty(value = "海运整柜服务方式")
        private List<SmCodeDeatil> FCL;

        public List<SmCodeDeatil> getAIR() {
            return AIR;
        }

        public void setAIR(List<SmCodeDeatil> AIR) {
            this.AIR = AIR;
        }

        public List<SmCodeDeatil> getLCL() {
            return LCL;
        }

        public void setLCL(List<SmCodeDeatil> LCL) {
            this.LCL = LCL;
        }

        public List<SmCodeDeatil> getEXPRESS() {
            return EXPRESS;
        }

        public void setEXPRESS(List<SmCodeDeatil> EXPRESS) {
            this.EXPRESS = EXPRESS;
        }

        public List<SmCodeDeatil> getTRAIN() {
            return TRAIN;
        }

        public void setTRAIN(List<SmCodeDeatil> TRAIN) {
            this.TRAIN = TRAIN;
        }

        public List<SmCodeDeatil> getFCL() {
            return FCL;
        }

        public void setFCL(List<SmCodeDeatil> FCL) {
            this.FCL = FCL;
        }

        public static class SmCodeDeatil {

            @ApiModelProperty(value = "服务方式编号")
            private String smCode;

            @ApiModelProperty(value = "服务方式名称")
            private String smCodeName;

            public String getSmCode() {
                return smCode;
            }

            public void setSmCode(String smCode) {
                this.smCode = smCode;
            }

            public String getSmCodeName() {
                return smCodeName;
            }

            public void setSmCodeName(String smCodeName) {
                this.smCodeName = smCodeName;
            }
        }
    }
}
