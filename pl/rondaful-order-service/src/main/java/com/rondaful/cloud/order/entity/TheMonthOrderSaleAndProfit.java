package com.rondaful.cloud.order.entity;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-02-13 17:56
 * 包名: com.rondaful.cloud.order.entity
 * 描述:
 */
public class TheMonthOrderSaleAndProfit {
    private String profit;
    private String sale;
    private String createTime;
    private String week;

    @Override
    public String toString() {
        return "TheMonthOrderSaleAndProfit{" +
                "profit='" + profit + '\'' +
                ", sale='" + sale + '\'' +
                ", createTime='" + createTime + '\'' +
                ", week='" + week + '\'' +
                '}';
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
