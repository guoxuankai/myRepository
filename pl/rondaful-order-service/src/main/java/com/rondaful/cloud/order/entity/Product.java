package com.rondaful.cloud.order.entity;



import java.util.Date;

/**
 * Created by chengcheng on 2017/12/5.
 */
public class Product {

    private String name;
    private double price;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
