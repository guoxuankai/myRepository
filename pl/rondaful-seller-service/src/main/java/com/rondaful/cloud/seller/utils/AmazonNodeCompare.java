package com.rondaful.cloud.seller.utils;

import com.rondaful.cloud.seller.entity.amazon.AmazonNode;

import java.util.Comparator;

public class AmazonNodeCompare implements Comparator<AmazonNode> {

    /**
     * 排序方式 true 正序  false 逆序
     */
    private boolean sort = true;

    public AmazonNodeCompare() {
    }

    /**
     * 排序方式
     * @param sort 排序方式 true 正序  false 逆序
     */
    public AmazonNodeCompare(boolean sort) {
        this.sort = sort;
    }

    @Override
    public int compare(AmazonNode node1, AmazonNode node2) {
        if(sort){
            return node1.getFieldName().compareTo(node2.getFieldName());
        }else
            return node2.getFieldName().compareTo(node1.getFieldName());
    }
}
