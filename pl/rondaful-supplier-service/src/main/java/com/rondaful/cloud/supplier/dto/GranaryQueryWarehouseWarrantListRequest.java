package com.rondaful.cloud.supplier.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 谷仓查询入库单列表API
 *
 * @ClassName WarehouseWarrantResponse
 * @Author tianye
 * @Date 2019/4/28 9:41
 * @Version 1.0
 */
public class GranaryQueryWarehouseWarrantListRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    public GranaryQueryWarehouseWarrantListRequest() {}

    private GranaryQueryWarehouseWarrantListRequest(int pageSize, int page, List<String> receiving_code_arr) {
        this.pageSize = pageSize;
        this.page = page;
        this.receiving_code_arr = receiving_code_arr;
    }

    public GranaryQueryWarehouseWarrantListRequest(List<String> receiving_code_arr) {
        this(100,1,receiving_code_arr);
    }

    @ApiModelProperty(value = "每页数据长度，最大值为100")
    private int pageSize;

    @ApiModelProperty(value = "当前页")
    private int page;

    @ApiModelProperty(value = "多个入库单号,数组格式,最多100")
    private List<String> receiving_code_arr;

    @ApiModelProperty(value = "创建结束日期 yyyy-MM-dd HH:mm:ss")
    private String create_date_from;

    @ApiModelProperty(value = "创建开始日期 yyyy-MM-dd HH:mm:ss")
    private String create_date_to;

    @ApiModelProperty(value = "修改开始时间 yyyy-MM-dd HH:mm:ss")
    private String modify_date_from;

    @ApiModelProperty(value = "修改结束时间 yyyy-MM-dd HH:mm:ss")
    private String modify_date_to;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<String> getReceiving_code_arr() {
        return receiving_code_arr;
    }

    public void setReceiving_code_arr(List<String> receiving_code_arr) {
        this.receiving_code_arr = receiving_code_arr;
    }

    public String getCreate_date_from() {
        return create_date_from;
    }

    public void setCreate_date_from(String create_date_from) {
        this.create_date_from = create_date_from;
    }

    public String getCreate_date_to() {
        return create_date_to;
    }

    public void setCreate_date_to(String create_date_to) {
        this.create_date_to = create_date_to;
    }

    public String getModify_date_from() {
        return modify_date_from;
    }

    public void setModify_date_from(String modify_date_from) {
        this.modify_date_from = modify_date_from;
    }

    public String getModify_date_to() {
        return modify_date_to;
    }

    public void setModify_date_to(String modify_date_to) {
        this.modify_date_to = modify_date_to;
    }
}
