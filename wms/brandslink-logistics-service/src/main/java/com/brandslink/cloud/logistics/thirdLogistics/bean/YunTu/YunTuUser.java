package com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@ApiModel("YunTuUser")
public class YunTuUser {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 50, message = "用户名字符个数最小为1最大为50")
    @JsonProperty(value = "userName")
    @ApiModelProperty(value = "用户名")
    private String UserName;

    @NotBlank(message = "密码不能为空")
    @Size(min = 1, max = 50, message = "密码字符个数最小为1最大为50")
    @JsonProperty(value = "passWord")
    @ApiModelProperty(value = "密码")
    private String PassWord;

    @NotBlank(message = "联系人不能为空")
    @Size(min = 1, max = 200, message = "联系人字符个数最小为1最大为200")
    @JsonProperty(value = "contact")
    @ApiModelProperty(value = "联系人")
    private String Contact;

    @NotBlank(message = "联系人电话（mobile）不能为空")
    @Size(min = 1, max = 200, message = "联系人电话字符个数最小为1最大为200")
    @JsonProperty(value = "mobile")
    @ApiModelProperty(value = "联系人电话")
    private String Mobile;

    @NotBlank(message = "联系人电话（telephone）不能为空")
    @Size(min = 1, max = 200, message = "联系人电话个数最小为1最大为200")
    @JsonProperty(value = "telephone")
    @ApiModelProperty(value = "联系人电话")
    private String Telephone;

    @NotBlank(message = "客户名称/公司名称不能为空")
    @Size(min = 1, max = 50, message = "客户名称/公司名称字符个数最小为1最大为50")
    @JsonProperty(value = "name")
    @ApiModelProperty(value = "客户名称/公司名称")
    private String Name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱必须为邮箱样式")
    @Size(min = 1, max = 50, message = "邮箱字符个数最小为1最大为50")
    @JsonProperty(value = "email")
    @ApiModelProperty(value = "邮箱")
    private String Email;

    @NotBlank(message = "详细地址不能为空")
    @Size(min = 1, max = 50, message = "详细地址字符个数最小为1最大为50")
    @JsonProperty(value = "address")
    @ApiModelProperty(value = "详细地址")
    private String Address;

    @NotNull(message = "平台ID(通途平台--2)不能为空")
    @JsonProperty(value = "platForm")
    @ApiModelProperty(value = "平台ID(通途平台--2)")
    private Integer PlatForm;
}
