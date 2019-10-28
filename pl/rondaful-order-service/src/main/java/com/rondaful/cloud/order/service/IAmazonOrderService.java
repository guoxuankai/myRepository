package com.rondaful.cloud.order.service;

import com.alibaba.fastjson.JSONArray;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersResponse;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.order.entity.*;
import com.rondaful.cloud.order.entity.Amazon.AmazonEmpower;
import com.rondaful.cloud.order.entity.Amazon.AmazonItemProperty;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrderDetail;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderTransferInsertOrUpdateDTO;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface IAmazonOrderService extends BaseService<AmazonOrder> {

    void updateAmazonProcessStatus(SysOrderDTO sysOrder);

    /**
     * 批量更改亚马逊平台订单状态
     * @param updateDTOS
     */
    void updateAmazonOrderBatchForConvert(SysOrderTransferInsertOrUpdateDTO updateDTOS);

    /**
     * 处理转化前的Amazon订单集合
     * @param switchList
     * @return
     */
    List<SysOrderDTO> getSysOrderList(List<AmazonOrder> switchList);

    /**
     * amazon转单
     * @param list
     */
    void amazonConverSYS(List<SysOrderDTO> list);

    void addBulkInsert(List<AmazonOrder> amazonOrderList, List<AmazonOrderDetail> amazonOrderDetailList, List<AmazonItemProperty> amazonItemPropertyList);

    List<PlatformExport> setData(List<AmazonOrder> amazonOrderList, boolean isSeller);

    JSONArray export(List<PlatformExport> platformExportList);

    /*
     * 抓取亚马逊订单数据
     * */
    ListOrdersResponse getAmazonOrders(Time time, String sellerId, String marketplaceIds, String mwsAuthToken) throws ParserConfigurationException, SAXException, IOException, Exception;

    AmazonOrder getAmazonOrderDetailByOrderId(String orderId);

    //     * 自动同步亚马逊订单
    String addAutoGetAmazonOrders(List<List<AmazonEmpower>> list, boolean isAuto)
            throws ParseException;

    /*
     *   Amazon平台订单转系统订单
     *  根据PL卖家ID，站点，SellerId  订单转入状态为待转入去查出LIST<AmazonOrder> ，通过SKU映射，订单规则匹配然后转入系统订单、更新平台订单状态
     * */
    String addTurnToSysOrderAndUpdateStatus(List<AmazonOrder> switchList, boolean isAuto);

    void addAutoGetAmazonOrdersTask(List<AmazonEmpower> list) throws ParseException;

    AmazonOrder selectAmazonOrderByOrderId(String orderId);

    /**
     * 根据Amazon订单ID获取Amazon订单
     * @param orderId Amazon订单ID
     * @return {@link AmazonOrder}
     */
    AmazonOrder findAmazonOrderByOrderId(String orderId);

    List<AmazonOrder> getExportResults(String platformSellerAccount, String orderId, String sellerPlAccount, String orderStatus, String
            plstatus, String startDate, String endDate) throws Exception;

    PageInfo<AmazonOrder> selectAmazonOrderByMultiCondition(String shopName, String orderId, String sellerPlAccount, String orderStatus, String plstatus, String startDate, String endDate);


    List<AmazonEmpower> queryRemoteSellerServiceAndInsertDb(String setPlAccountIfManualSync);


    List<AmazonOrder> selectAmazonOrderByOrderListId(List<String> ids);

}
