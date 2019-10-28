package com.rondaful.cloud.supplier.model.dto.logistics;

public class LogisticsAddress {

    private long address_id;
    private String city;
    private String country;
    private String county;
    private String email;
    private int is_default;
    private String language;
    private String member_type;
    private String name;
    private String phone;
    private String postcode;
    private String province;
    private String street;
    private String street_address;
    private String trademanage_id;

    public long getAddress_id() {
        return address_id;
    }

    public void setAddress_id(long address_id) {
        this.address_id = address_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIs_default() {
        return is_default;
    }

    public void setIs_default(int is_default) {
        this.is_default = is_default;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMember_type() {
        return member_type;
    }

    public void setMember_type(String member_type) {
        this.member_type = member_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getTrademanage_id() {
        return trademanage_id;
    }

    public void setTrademanage_id(String trademanage_id) {
        this.trademanage_id = trademanage_id;
    }

    @Override
    public String toString() {
        return "LogisticsAddress{" +
                "address_id=" + address_id +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", county='" + county + '\'' +
                ", email='" + email + '\'' +
                ", is_default=" + is_default +
                ", language='" + language + '\'' +
                ", member_type='" + member_type + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", postcode='" + postcode + '\'' +
                ", province='" + province + '\'' +
                ", street='" + street + '\'' +
                ", street_address='" + street_address + '\'' +
                ", trademanage_id='" + trademanage_id + '\'' +
                '}';
    }

}
