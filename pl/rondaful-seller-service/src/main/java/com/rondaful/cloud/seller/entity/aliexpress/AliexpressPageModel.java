package com.rondaful.cloud.seller.entity.aliexpress;

/**
 * 队列的对象
 * @author chenhan
 *
 */
public class AliexpressPageModel {


    private Long currentPage;
    private Long pageSize;

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}
