package com.rondaful.cloud.order.task;

import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrderDetail;
import com.rondaful.cloud.order.entity.eBay.EbayOrderStatus;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BatchSyncEbayOrderDealData {
    private static Logger _log = LoggerFactory.getLogger(BatchSyncEbayOrderDealData.class);
    @Autowired
    @Qualifier("orderSqlSessionFactory")
    SqlSessionFactory sqlSessionFactory;

    public Integer insertUpdateEbayOrder(List<EbayOrder> inlist) throws Exception {
        final List<EbayOrder> tempexpList = inlist;
        String sql = "INSERT INTO tb_ebay_order (" +
                "order_id, " +//1
                "total, created_time, paid_time, seller_user_id, record_number, buyer_user_id, buyer_email, " +//7
                "shipped_time, shipping_service_cost, amount_paid, payment_status, payment_method, reference_id, " +//6
                "last_modified_time, seller_email, buyer_checkout_message, name, phone, country," +//6
                "country_name, state_or_province, city_name, street1, street2, postal_code, create_by, update_by, is_show_on_list)" +//6
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
                "ON DUPLICATE KEY UPDATE " +
                "total = ?, created_time = ?, paid_time = ?, seller_user_id = ?, record_number = ?, buyer_user_id = ?, buyer_email = ?," +//7
                "shipped_time = ?, shipping_service_cost = ?, amount_paid = ?, payment_status = ?, payment_method = ?, reference_id = ?, " +//6
                "last_modified_time = ?, seller_email = ?, buyer_checkout_message = ?, name = ?, phone = ?, country = ?," +//6
                "country_name = ?, state_or_province = ?, city_name = ?, street1 = ?, street2 = ?, postal_code = ?, create_by = ?, update_by = ?, is_show_on_list=?";//8/*不支持total = VALUES(total)*/
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        Connection conn = sqlSession.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        for (EbayOrder ebayOrder : tempexpList) {
            try {
                pstmt.setString(1, ebayOrder.getOrderId() == null ? "" : ebayOrder.getOrderId());
                pstmt.setString(2, ebayOrder.getTotal() == null ? "" : ebayOrder.getTotal());
                pstmt.setString(3, ebayOrder.getCreatedTime() == null ? "" : ebayOrder.getCreatedTime());
                pstmt.setString(4, ebayOrder.getPaidTime() == null ? "" : ebayOrder.getPaidTime());
                pstmt.setString(5, ebayOrder.getSellerUserId() == null ? "" : ebayOrder.getSellerUserId());
                pstmt.setString(6, ebayOrder.getRecordNumber() == null ? "" : ebayOrder.getRecordNumber());
                pstmt.setString(7, ebayOrder.getBuyerUserId() == null ? "" : ebayOrder.getBuyerUserId());
                pstmt.setString(8, ebayOrder.getBuyerEmail() == null ? "" : ebayOrder.getBuyerEmail());
                pstmt.setString(9, ebayOrder.getShippedTime() == null ? "" : ebayOrder.getShippedTime());
                pstmt.setString(10, ebayOrder.getShippingServiceCost() == null ? "" : ebayOrder.getShippingServiceCost());
                pstmt.setString(11, ebayOrder.getAmountPaid() == null ? "" : ebayOrder.getAmountPaid());
                pstmt.setString(12, ebayOrder.getPaymentStatus() == null ? "" : ebayOrder.getPaymentStatus());
                pstmt.setString(13, ebayOrder.getPaymentMethod() == null ? "" : ebayOrder.getPaymentMethod());
                pstmt.setString(14, ebayOrder.getReferenceId() == null ? "" : ebayOrder.getReferenceId());
                pstmt.setString(15, ebayOrder.getLastModifiedTime() == null ? "" : ebayOrder.getLastModifiedTime());
                pstmt.setString(16, ebayOrder.getSellerEmail() == null ? "" : ebayOrder.getSellerEmail());
                pstmt.setString(17, ebayOrder.getBuyerCheckoutMessage() == null ? "" : ebayOrder.getBuyerCheckoutMessage());
                pstmt.setString(18, ebayOrder.getName() == null ? "" : ebayOrder.getName());
                pstmt.setString(19, ebayOrder.getPhone() == null ? "" : ebayOrder.getPhone());
                pstmt.setString(20, ebayOrder.getCountry() == null ? "" : ebayOrder.getCountry());
                pstmt.setString(21, ebayOrder.getCountryName() == null ? "" : ebayOrder.getCountryName());
                pstmt.setString(22, ebayOrder.getStateOrProvince() == null ? "" : ebayOrder.getStateOrProvince());
                pstmt.setString(23, ebayOrder.getCityName() == null ? "" : ebayOrder.getCityName());
                pstmt.setString(24, ebayOrder.getStreet1() == null ? "" : ebayOrder.getStreet1());
                pstmt.setString(25, ebayOrder.getStreet2() == null ? "" : ebayOrder.getStreet2());
                pstmt.setString(26, ebayOrder.getPostalCode() == null ? "" : ebayOrder.getPostalCode());
                pstmt.setString(27, ebayOrder.getCreateBy() == null ? "" : ebayOrder.getCreateBy());
                pstmt.setString(28, ebayOrder.getUpdateBy() == null ? "" : ebayOrder.getUpdateBy());
                pstmt.setString(29, ebayOrder.getShowOnList() == null ? "" : ebayOrder.getShowOnList());

                pstmt.setString(30, ebayOrder.getTotal() == null ? "" : ebayOrder.getTotal());
                pstmt.setString(31, ebayOrder.getCreatedTime() == null ? "" : ebayOrder.getCreatedTime());
                pstmt.setString(32, ebayOrder.getPaidTime() == null ? "" : ebayOrder.getPaidTime());
                pstmt.setString(33, ebayOrder.getSellerUserId() == null ? "" : ebayOrder.getSellerUserId());
                pstmt.setString(34, ebayOrder.getRecordNumber() == null ? "" : ebayOrder.getRecordNumber());
                pstmt.setString(35, ebayOrder.getBuyerUserId() == null ? "" : ebayOrder.getBuyerUserId());
                pstmt.setString(36, ebayOrder.getBuyerEmail() == null ? "" : ebayOrder.getBuyerEmail());
                pstmt.setString(37, ebayOrder.getShippedTime() == null ? "" : ebayOrder.getShippedTime());
                pstmt.setString(38, ebayOrder.getShippingServiceCost() == null ? "" : ebayOrder.getShippingServiceCost());
                pstmt.setString(39, ebayOrder.getAmountPaid() == null ? "" : ebayOrder.getAmountPaid());
                pstmt.setString(40, ebayOrder.getPaymentStatus() == null ? "" : ebayOrder.getPaymentStatus());
                pstmt.setString(41, ebayOrder.getPaymentMethod() == null ? "" : ebayOrder.getPaymentMethod());
                pstmt.setString(42, ebayOrder.getReferenceId() == null ? "" : ebayOrder.getReferenceId());
                pstmt.setString(43, ebayOrder.getLastModifiedTime() == null ? "" : ebayOrder.getLastModifiedTime());
                pstmt.setString(44, ebayOrder.getSellerEmail() == null ? "" : ebayOrder.getSellerEmail());
                pstmt.setString(45, ebayOrder.getBuyerCheckoutMessage() == null ? "" : ebayOrder.getBuyerCheckoutMessage());
                pstmt.setString(46, ebayOrder.getName() == null ? "" : ebayOrder.getName());
                pstmt.setString(47, ebayOrder.getPhone() == null ? "" : ebayOrder.getPhone());
                pstmt.setString(48, ebayOrder.getCountry() == null ? "" : ebayOrder.getCountry());
                pstmt.setString(49, ebayOrder.getCountryName() == null ? "" : ebayOrder.getCountryName());
                pstmt.setString(50, ebayOrder.getStateOrProvince() == null ? "" : ebayOrder.getStateOrProvince());
                pstmt.setString(51, ebayOrder.getCityName() == null ? "" : ebayOrder.getCityName());
                pstmt.setString(52, ebayOrder.getStreet1() == null ? "" : ebayOrder.getStreet1());
                pstmt.setString(53, ebayOrder.getStreet2() == null ? "" : ebayOrder.getStreet2());
                pstmt.setString(54, ebayOrder.getPostalCode() == null ? "" : ebayOrder.getPostalCode());
                pstmt.setString(55, ebayOrder.getCreateBy() == null ? "" : ebayOrder.getCreateBy());
                pstmt.setString(56, ebayOrder.getUpdateBy() == null ? "" : ebayOrder.getUpdateBy());
                pstmt.setString(57, ebayOrder.getShowOnList() == null ? "1" : ebayOrder.getShowOnList());
                pstmt.addBatch();
            } catch (SQLException e) {
                _log.error("_______insertUpdateEbayOrder__________出错的ebay平台订单号为：{}________该条订单属于：{}__________错误信息:{}__________",
                        ebayOrder.getOrderId(), ebayOrder.getSellerUserId(), e);
            }
        }
        pstmt.executeBatch();
        conn.commit();
        List<Integer> list = new ArrayList<>();
        ResultSet rs = pstmt.getGeneratedKeys(); //获取结果
        while (rs.next()) {
            list.add(rs.getInt(1));//取得ID
        }
        int num = list.size();
        //提交事务
        sqlSession.commit();
        conn.close();
        pstmt.close();
        rs.close();
        return num;
    }

    public Integer insertUpdateEbayOrderDetail(List<EbayOrderDetail> inlist) throws Exception {
        final List<EbayOrderDetail> tempexpList = inlist;
        String sql = "insert into tb_ebay_order_detail (" +
                "order_id, order_line_item_id, " +
                "transaction_id, buyer_email, transaction_price, " +
                "quantity_purchased, item_id, handle_by_time, item_title, sku, variation_sku, variation_title, " +
                "variation_view_item_url, shipped_time, shipping_carrier_used, " +
                "shipment_tracking_number, record_number, create_by, update_by)" +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
                "ON DUPLICATE KEY UPDATE " +
                "buyer_email = ?, transaction_price = ?, quantity_purchased = ?, item_id = ?, handle_by_time = ?, item_title = ?, sku = ?," +
                "variation_sku = ?, variation_title = ?, variation_view_item_url = ?, shipped_time = ?, " +
                "shipping_carrier_used = ?, shipment_tracking_number = ?, record_number = ?, create_by = ?, update_by = ?";
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        Connection conn = sqlSession.getConnection();
        conn.setAutoCommit(false);//手动提交
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        for (EbayOrderDetail ebayOrderDetail : tempexpList) {
            try {
                pstmt.setString(1, (ebayOrderDetail.getOrderId() == null ? "" : ebayOrderDetail.getOrderId()));
                pstmt.setString(2, ebayOrderDetail.getOrderLineItemId() == null ? "" : ebayOrderDetail.getOrderLineItemId());
                pstmt.setString(3, ebayOrderDetail.getTransactionId() == null ? "" : ebayOrderDetail.getTransactionId());
                pstmt.setString(4, ebayOrderDetail.getBuyerEmail() == null ? "" : ebayOrderDetail.getBuyerEmail());
                pstmt.setString(5, ebayOrderDetail.getTransactionPrice() == null ? "" : ebayOrderDetail.getTransactionPrice());
                pstmt.setInt(6, ebayOrderDetail.getQuantityPurchased() == null ? 0 : ebayOrderDetail.getQuantityPurchased());
                pstmt.setString(7, ebayOrderDetail.getItemId() == null ? "" : ebayOrderDetail.getItemId());
                pstmt.setString(8, ebayOrderDetail.getHandleByTime() == null ? "" : ebayOrderDetail.getHandleByTime());
                pstmt.setString(9, ebayOrderDetail.getItemTitle() == null ? "" : ebayOrderDetail.getItemTitle());
                pstmt.setString(10, ebayOrderDetail.getSku() == null ? "" : ebayOrderDetail.getSku());
                pstmt.setString(11, ebayOrderDetail.getVariationSku() == null ? "" : ebayOrderDetail.getVariationSku());
                pstmt.setString(12, ebayOrderDetail.getVariationTitle() == null ? "" : ebayOrderDetail.getVariationTitle());
                pstmt.setString(13, ebayOrderDetail.getVariationViewItemUrl() == null ? "" : ebayOrderDetail.getVariationViewItemUrl());
                pstmt.setString(14, ebayOrderDetail.getShippedTime() == null ? "" : ebayOrderDetail.getShippedTime());
                pstmt.setString(15, ebayOrderDetail.getShippingCarrierUsed() == null ? "" : ebayOrderDetail.getShippingCarrierUsed());
                pstmt.setString(16, ebayOrderDetail.getShipmentTrackingNumber() == null ? "" : ebayOrderDetail.getShipmentTrackingNumber());
                pstmt.setInt(17, ebayOrderDetail.getRecordNumber() == null ? 0 : ebayOrderDetail.getRecordNumber());
                pstmt.setString(18, ebayOrderDetail.getCreateBy() == null ? "" : ebayOrderDetail.getCreateBy());
                pstmt.setString(19, ebayOrderDetail.getUpdateBy() == null ? "" : ebayOrderDetail.getUpdateBy());

                pstmt.setString(20, ebayOrderDetail.getBuyerEmail() == null ? "" : ebayOrderDetail.getBuyerEmail());
                pstmt.setString(21, ebayOrderDetail.getTransactionPrice() == null ? "" : ebayOrderDetail.getTransactionPrice());
                pstmt.setInt(22, ebayOrderDetail.getQuantityPurchased() == null ? 0 : ebayOrderDetail.getQuantityPurchased());
                pstmt.setString(23, ebayOrderDetail.getItemId() == null ? "" : ebayOrderDetail.getItemId());
                pstmt.setString(24, ebayOrderDetail.getHandleByTime() == null ? "" : ebayOrderDetail.getHandleByTime());
                pstmt.setString(25, ebayOrderDetail.getItemTitle() == null ? "" : ebayOrderDetail.getItemTitle());
                pstmt.setString(26, ebayOrderDetail.getSku() == null ? "" : ebayOrderDetail.getSku());
                pstmt.setString(27, ebayOrderDetail.getVariationSku() == null ? "" : ebayOrderDetail.getVariationSku());
                pstmt.setString(28, ebayOrderDetail.getVariationTitle() == null ? "" : ebayOrderDetail.getVariationTitle());
                pstmt.setString(29, ebayOrderDetail.getVariationViewItemUrl() == null ? "" : ebayOrderDetail.getVariationViewItemUrl());
                pstmt.setString(30, ebayOrderDetail.getShippedTime() == null ? "" : ebayOrderDetail.getShippedTime());
                pstmt.setString(31, ebayOrderDetail.getShippingCarrierUsed() == null ? "" : ebayOrderDetail.getShippingCarrierUsed());
                pstmt.setString(32, ebayOrderDetail.getShipmentTrackingNumber() == null ? "" : ebayOrderDetail.getShipmentTrackingNumber());
                pstmt.setInt(33, ebayOrderDetail.getRecordNumber() == null ? 0 : ebayOrderDetail.getRecordNumber());
                pstmt.setString(34, ebayOrderDetail.getCreateBy() == null ? "" : ebayOrderDetail.getCreateBy());
                pstmt.setString(35, ebayOrderDetail.getUpdateBy() == null ? "" : ebayOrderDetail.getUpdateBy());
                pstmt.addBatch();
            } catch (SQLException e) {
                _log.error("_______insertUpdateEbayOrderDetail________出错的ebay平台订单号为：{}_________错误信息：{}________", ebayOrderDetail.getOrderId(), e);
            }
        }
        pstmt.executeBatch();
        conn.commit();
        List<Integer> list = new ArrayList<>();
        ResultSet rs = pstmt.getGeneratedKeys(); //获取结果
        while (rs.next()) {
            list.add(rs.getInt(1));//取得ID
        }
        int num = list.size();
        //提交事务
        sqlSession.commit();
        conn.close();
        pstmt.close();
        rs.close();
        return num;
    }

    public Integer insertUpdateEbayOrderStatus(List<EbayOrderStatus> inlist) throws Exception {
        final List<EbayOrderStatus> tempexpList = inlist;
        String sql = "INSERT INTO tb_ebay_order_status\n" +
                "(order_id, seller_pl_id, seller_pl_account, empower_id, seller_user_id, order_status, " +
                "handle_by_time, last_modified_time, cancel_status, payment_status, " +
                "refund_status, create_by, update_by,seller_pl_shop_account)\n" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "order_status = ?, handle_by_time = ?, last_modified_time = ?, cancel_status = ?," +
                "payment_status = ?, refund_status = ?, create_by = ?, update_by = ?";
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        Connection conn = sqlSession.getConnection();
        conn.setAutoCommit(false);//手动提交
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        for (EbayOrderStatus ebayOrderStatus : tempexpList) {
            try {
                pstmt.setString(1, ebayOrderStatus.getOrderId() == null ? "" : ebayOrderStatus.getOrderId());
                pstmt.setInt(2, ebayOrderStatus.getSellerPlId() == null ? null : ebayOrderStatus.getSellerPlId());
                pstmt.setString(3, ebayOrderStatus.getSellerPlAccount() == null ? "" : ebayOrderStatus.getSellerPlAccount());
                pstmt.setInt(4, ebayOrderStatus.getEmpowerId() == null ? null : ebayOrderStatus.getEmpowerId());
                pstmt.setString(5, ebayOrderStatus.getSellerUserId() == null ? "" : ebayOrderStatus.getSellerUserId());
                pstmt.setString(6, ebayOrderStatus.getOrderStatus() == null ? "" : ebayOrderStatus.getOrderStatus());
                pstmt.setString(7, ebayOrderStatus.getHandleByTime() == null ? "" : ebayOrderStatus.getHandleByTime());
                pstmt.setString(8, ebayOrderStatus.getLastModifiedTime() == null ? "" : ebayOrderStatus.getLastModifiedTime());
                pstmt.setString(9, ebayOrderStatus.getCancelStatus() == null ? "" : ebayOrderStatus.getCancelStatus());
                pstmt.setString(10, ebayOrderStatus.getPaymentStatus() == null ? "" : ebayOrderStatus.getPaymentStatus());
                pstmt.setString(11, ebayOrderStatus.getRefundStatus() == null ? "" : ebayOrderStatus.getRefundStatus());
                pstmt.setString(12, ebayOrderStatus.getCreateBy() == null ? "" : ebayOrderStatus.getCreateBy());
                pstmt.setString(13, ebayOrderStatus.getUpdateBy() == null ? "" : ebayOrderStatus.getUpdateBy());
                pstmt.setString(14, ebayOrderStatus.getSellerPlShopAccount() == null ? "" : ebayOrderStatus.getSellerPlShopAccount());

                pstmt.setString(15, ebayOrderStatus.getOrderStatus() == null ? "" : ebayOrderStatus.getOrderStatus());
                pstmt.setString(16, ebayOrderStatus.getHandleByTime() == null ? "" : ebayOrderStatus.getHandleByTime());
                pstmt.setString(17, ebayOrderStatus.getLastModifiedTime() == null ? "" : ebayOrderStatus.getLastModifiedTime());
                pstmt.setString(18, ebayOrderStatus.getCancelStatus() == null ? "" : ebayOrderStatus.getCancelStatus());
                pstmt.setString(19, ebayOrderStatus.getPaymentStatus() == null ? "" : ebayOrderStatus.getPaymentStatus());
                pstmt.setString(20, ebayOrderStatus.getRefundStatus() == null ? "" : ebayOrderStatus.getRefundStatus());
                pstmt.setString(21, ebayOrderStatus.getCreateBy() == null ? "" : ebayOrderStatus.getCreateBy());
                pstmt.setString(22, ebayOrderStatus.getUpdateBy() == null ? "" : ebayOrderStatus.getUpdateBy());
                pstmt.addBatch();
            } catch (SQLException e) {
                _log.error("_________insertUpdateEbayOrder____________出错的ebay平台订单号为：{}____________该条订单属于：{}__________错误信息：{}_________",
                        ebayOrderStatus.getOrderId(), ebayOrderStatus.getSellerUserId(), e);
            }
        }
        pstmt.executeBatch();
        conn.commit();
        List<Integer> list = new ArrayList<>();
        ResultSet rs = pstmt.getGeneratedKeys(); //获取结果
        while (rs.next()) {
            list.add(rs.getInt(1));//取得ID
        }
        int num = list.size();
        //提交事务
        sqlSession.commit();
        conn.close();
        pstmt.close();
        rs.close();
        return num;
    }
}
