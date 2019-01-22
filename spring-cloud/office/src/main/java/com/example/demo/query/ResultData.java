package com.example.demo.query;

import java.math.BigInteger;
import java.util.List;

public class ResultData<T> {

    private int page;
    private int pageSize;
    private BigInteger totalCount;


    public int getTotalPage() {
        Integer bint = Integer.valueOf(totalCount.toString());
        int i = bint / pageSize;
        if (bint % pageSize > 0) {
            i++;
        }
        return i;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    private int totalPage;
    private List<T> list;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public BigInteger getTotalCount() {
        if (totalCount == null) {
            return new BigInteger("0");
        }
        return totalCount;
    }

    public void setTotalCount(BigInteger totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
