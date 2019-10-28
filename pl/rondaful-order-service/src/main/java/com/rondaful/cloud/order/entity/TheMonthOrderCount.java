package com.rondaful.cloud.order.entity;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-02-13 17:56
 * 包名: com.rondaful.cloud.order.entity
 * 描述:
 */
public class TheMonthOrderCount {
    private String counts;
    private String createTime;
    private String week;

    @Override
    public String toString() {
        return "TheMonthOrderCount{" +
                "counts='" + counts + '\'' +
                ", createTime='" + createTime + '\'' +
                ", week='" + week + '\'' +
                '}';
    }

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
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
