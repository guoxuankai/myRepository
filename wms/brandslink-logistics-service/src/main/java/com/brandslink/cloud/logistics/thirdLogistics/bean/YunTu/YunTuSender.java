package com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@ApiModel(value = "YunTuSender")
public class YunTuSender implements Serializable {
    @Size(max = 10, message = "发件人所在国家，字符个数必须小于10")
    @JsonProperty(value = "countryCode")
    @ApiModelProperty(value = "发件人所在国家，填写国际通用标准2位简码，可通过国家查询服务查询")
    private String CountryCode;

    @Size(max = 50, message = "发件人姓，字符个数必须小于50")
    @JsonProperty(value = "firstName")
    @ApiModelProperty(value = "发件人姓")
    private String FirstName;

    @Size(max = 50, message = "发件人名，字符个数必须小于50")
    @JsonProperty(value = "lastName")
    @ApiModelProperty(value = "发件人名")
    private String LastName;

    @Size(max = 500, message = "发件人公司名称，字符个数必须小于500")
    @JsonProperty(value = "company")
    @ApiModelProperty(value = "发件人公司名称")
    private String Company;

    @Size(max = 200, message = "发件人详细地址，字符个数必须小于200")
    @JsonProperty(value = "street")
    @ApiModelProperty(value = "发件人详细地址   FBA 必填")
    private String Street;

    @Size(max = 100, message = "发件人所在城市，字符个数必须小于100")
    @JsonProperty(value = "city")
    @ApiModelProperty(value = "发件人所在城市")
    private String City;

    @Size(max = 100, message = "发件人省/州，字符个数必须小于100")
    @JsonProperty(value = "state")
    @ApiModelProperty(value = "发件人省/州")
    private String State;

    @Size(max = 50, message = "发件人邮编，字符个数必须小于50")
    @JsonProperty(value = "zip")
    @ApiModelProperty(value = "发件人邮编")
    private String Zip;

    @Size(max = 20, message = "发件人电话，字符个数必须小于20")
    @JsonProperty(value = "phone")
    @ApiModelProperty(value = "发件人电话")
    private String Phone;
}
