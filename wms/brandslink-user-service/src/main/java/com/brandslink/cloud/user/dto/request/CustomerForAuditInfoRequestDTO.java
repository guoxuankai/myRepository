package com.brandslink.cloud.user.dto.request;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 编辑客户审核信息请求model
 *
 * @ClassName CustomerForAuditInfoRequestDTO
 * @Author tianye
 * @Date 2019/9/5 10:21
 * @Version 1.0
 */
public class CustomerForAuditInfoRequestDTO implements Serializable {

    @ApiModelProperty(value = "oms系统传账号id，其他系统传客户id")
    private Integer id;

    @ApiModelProperty(value = "法定代表人")
    private String legalRepresentative;

    @ApiModelProperty(value = "法定代表人身份证号码")
    private String legalRepresentativeIdentityCard;

    @ApiModelProperty(value = "营业执照")
    private String businessLicense;

    @ApiModelProperty(value = "法定代表人身份证扫描件（正面）")
    private String identityCardFront;

    @ApiModelProperty(value = "法定代表人身份证扫描件（反面）")
    private String identityCardVerso;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getLegalRepresentativeIdentityCard() {
        return legalRepresentativeIdentityCard;
    }

    public void setLegalRepresentativeIdentityCard(String legalRepresentativeIdentityCard) {
        this.legalRepresentativeIdentityCard = legalRepresentativeIdentityCard;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getIdentityCardFront() {
        return identityCardFront;
    }

    public void setIdentityCardFront(String identityCardFront) {
        this.identityCardFront = identityCardFront;
    }

    public String getIdentityCardVerso() {
        return identityCardVerso;
    }

    public void setIdentityCardVerso(String identityCardVerso) {
        this.identityCardVerso = identityCardVerso;
    }
}
