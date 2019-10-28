package com.rondaful.cloud.supplier.entity.Logistics;

public class LogisticsMapping {

    private String erpLogisticsCode;

    private String erpLogisticsName;

    private String granaryLogisticsCode;

    private String granaryLogisticsName;

    public String getErpLogisticsCode() {
        return erpLogisticsCode;
    }

    public void setErpLogisticsCode(String erpLogisticsCode) {
        this.erpLogisticsCode = erpLogisticsCode;
    }

    public String getErpLogisticsName() {
        return erpLogisticsName;
    }

    public void setErpLogisticsName(String erpLogisticsName) {
        this.erpLogisticsName = erpLogisticsName;
    }

    public String getGranaryLogisticsCode() {
        return granaryLogisticsCode;
    }

    public void setGranaryLogisticsCode(String granaryLogisticsCode) {
        this.granaryLogisticsCode = granaryLogisticsCode;
    }

    public String getGranaryLogisticsName() {
        return granaryLogisticsName;
    }

    public void setGranaryLogisticsName(String granaryLogisticsName) {
        this.granaryLogisticsName = granaryLogisticsName;
    }

    @Override
    public String toString() {
        return "logisticsMapping{" +
                "erpLogisticsCode='" + erpLogisticsCode + '\'' +
                ", erpLogisticsName='" + erpLogisticsName + '\'' +
                ", granaryLogisticsCode='" + granaryLogisticsCode + '\'' +
                ", granaryLogisticsName='" + granaryLogisticsName + '\'' +
                '}';
    }
}
