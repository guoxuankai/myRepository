package com.rondaful.cloud.seller.entity.aliexpress;

import java.io.Serializable;
import java.util.List;

public class AliexpressMobileDetail implements Serializable {
    private List<AliexpressMobileDetailContent> mobileDetail;
    private String version;
    private String versionNum;

    public List<AliexpressMobileDetailContent> getMobileDetail() {
        return mobileDetail;
    }

    public void setMobileDetail(List<AliexpressMobileDetailContent> mobileDetail) {
        this.mobileDetail = mobileDetail;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }
}
