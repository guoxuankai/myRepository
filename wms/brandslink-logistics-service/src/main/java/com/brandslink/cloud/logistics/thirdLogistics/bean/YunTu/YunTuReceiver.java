package com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@ApiModel(value = "YunTuReceiver")
public class YunTuReceiver implements Serializable {

    @Size(max = 10, message = "收件人企业税号，字符最大个数为10")
    @JsonProperty(value = "taxId")
    @ApiModelProperty(value = "收件人企业税号，欧盟可以填EORI，巴西可以填CPF等，非必填")
    private String TaxId;

    @NotBlank(message = "收件人所在国家不能为空")
    @Size(min = 1, max = 50, message = "收件人所在国家字符个数必须在1和50之间")
    @JsonProperty(value = "countryCode")
    @ApiModelProperty(value = "收件人所在国家，填写国际通用标准2位简码，可通过国家查询服务查询")
    private String CountryCode;

    @NotBlank(message = "收件人姓不能为空")
    @Size(min = 1, max = 50, message = "收件人姓，字符最大个数为10")
    @JsonProperty(value = "firstName")
    @ApiModelProperty(value = "收件人姓")
    private String FirstName;

    @Size(max = 50, message = "收件人名，字符最大个数为50")
    @JsonProperty(value = "lastName")
    @ApiModelProperty(value = "收件人名")
    private String LastName;

    @Size(max = 50, message = "收件人公司名称，字符最大个数为50")
    @JsonProperty(value = "company")
    @ApiModelProperty(value = "收件人公司名称")
    private String Company;

    @NotBlank(message = "收件人详细地址不能为空")
    @Size(min = 1, max = 200, message = "收件人详细地址字符个数必须在1和200之间")
    @JsonProperty(value = "street")
    @ApiModelProperty(value = "收件人详细地址")
    private String Street;

    @Size(max = 200, message = "收件人详细地址1，字符最大个数为200")
    @JsonProperty(value = "streetAddress1")
    @ApiModelProperty(value = "收件人详细地址1")
    private String StreetAddress1;

    @Size(max = 200, message = "收件人详细地址2，字符最大个数为200")
    @JsonProperty(value = "streetAddress2")
    @ApiModelProperty(value = "收件人详细地址2")
    private String StreetAddress2;

    @NotBlank(message = "收件人所在城市不能为空")
    @Size(min = 1, max = 100, message = "收件人所在城市字符个数必须在1和100之间")
    @JsonProperty(value = "city")
    @ApiModelProperty(value = "收件人所在城市")
    private String City;

    @Size(max = 100, message = "收件人省/州，字符个数必须小于100")
    @JsonProperty(value = "state")
    @ApiModelProperty(value = "收件人省/州")
    private String State;

    @Size(max = 50, message = "收件人邮编，字符个数必须小于50")
    @JsonProperty(value = "zip")
    @ApiModelProperty(value = "收件人邮编")
    private String Zip;

    @Size(max = 200,message = "收件人电话，字符个数必须小于200")
    @JsonProperty(value = "phone")
    @ApiModelProperty(value = "收件人电话")
    private String Phone;

    @JsonProperty(value = "houseNumber")
    @ApiModelProperty(value = "收件人地址门牌号")
    private String HouseNumber;
}
