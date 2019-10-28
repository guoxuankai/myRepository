package com.rondaful.cloud.order.entity;


import java.util.concurrent.atomic.AtomicInteger;

public class ExcelOrderStatisticsDTO{
    private AtomicInteger totalCount = new AtomicInteger(0);
    private AtomicInteger successCount = new AtomicInteger(0);

    public AtomicInteger getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(AtomicInteger totalCount) {
        this.totalCount = totalCount;
    }

    public AtomicInteger getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(AtomicInteger successCount) {
        this.successCount = successCount;
    }
}
