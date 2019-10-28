package com.rondaful.cloud.order.task;

import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrderDetail;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class BatchDealConverSysOrderData {
    private static Logger _log = LoggerFactory.getLogger(BatchDealConverSysOrderData.class);
    @Autowired
    @Qualifier("orderSqlSessionFactory")
    SqlSessionFactory sqlSessionFactory;

    public Connection getConnection() throws SQLException {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        Connection conn = sqlSession.getConnection();
        conn.setAutoCommit(false);//手动提交
        return conn;
    }

    public void commitTransaction(Connection conn) throws SQLException {
        conn.commit();
        conn.close();
    }

    public void insertBatchSysOrderList(Connection conn, List<SysOrder> inlist) throws SQLException {
        final List<SysOrder> tempexpList = inlist;
        String sql = "INSERT INTO tb_sys_order\n" +
                "(sys_order_id, order_track_id, record_number, source_order_id, deliver_deadline, " +//5
                "conver_sys_status, order_source, order_delivery_status, platform_shop_id, platform_seller_account, shop_type," +//6
                "buyer_user_id, buyer_name, seller_pl_id, seller_pl_account, supply_chain_company_id, supply_chain_company_name, total, order_amount, " +//8
                "order_time, delivery_warehouse, delivery_warehouse_code, shipping_carrier_used_code, shipping_carrier_used, shipping_service_cost, " +//6
                "estimate_ship_cost, commodities_amount, reference_id, buyer_checkout_message, ship_to_name, ship_to_country, ship_to_country_name, ship_to_state, " +//8
                "ship_to_city, ship_to_addr_street1, ship_to_addr_street2, ship_to_addr_street3, ship_to_postal_code, " +//5
                "ship_to_phone, ship_to_email, ebay_carrier_name, delivery_method_code, delivery_method, create_by, update_by)\n" +//7---共45
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        for (SysOrder sysOrder : tempexpList) {
            try {
                pstmt.setString(1, sysOrder.getSysOrderId() == null ? "" : sysOrder.getSysOrderId());
                pstmt.setString(2, (sysOrder.getOrderTrackId() == null ? "" : sysOrder.getOrderTrackId()));
                pstmt.setString(3, (sysOrder.getRecordNumber() == null ? "" : sysOrder.getRecordNumber()));
                pstmt.setString(4, sysOrder.getSourceOrderId() == null ? "" : sysOrder.getSourceOrderId());
                pstmt.setString(5, sysOrder.getDeliverDeadline() == null ? "" : sysOrder.getDeliverDeadline());
                pstmt.setByte(6, sysOrder.getConverSysStatus() == null ? 0 : sysOrder.getConverSysStatus());
                pstmt.setByte(7, sysOrder.getOrderSource() == null ? 0 : sysOrder.getOrderSource());
                pstmt.setByte(8, sysOrder.getOrderDeliveryStatus() == null ? 1 : sysOrder.getOrderDeliveryStatus());
                pstmt.setInt(9, sysOrder.getPlatformShopId() == null ? null : sysOrder.getPlatformShopId());
                pstmt.setString(10, sysOrder.getPlatformSellerAccount() == null ? "" : sysOrder.getPlatformSellerAccount());
                pstmt.setString(11, sysOrder.getShopType() == null ? "" : sysOrder.getShopType());
                pstmt.setString(12, sysOrder.getBuyerUserId() == null ? "" : sysOrder.getBuyerUserId());
                pstmt.setString(13, sysOrder.getBuyerName() == null ? "" : sysOrder.getBuyerName());

                pstmt.setInt(14, sysOrder.getSellerPlId() == null ? null : sysOrder.getSellerPlId());
                pstmt.setString(15, sysOrder.getSellerPlAccount() == null ? "" : sysOrder.getSellerPlAccount());
                pstmt.setInt(16, sysOrder.getSupplyChainCompanyId() == null ? null : sysOrder.getSupplyChainCompanyId());
                pstmt.setString(17, sysOrder.getSupplyChainCompanyName() == null ? "" : sysOrder.getSupplyChainCompanyName());
                pstmt.setBigDecimal(18, sysOrder.getTotal() == null ? null : sysOrder.getTotal());
                pstmt.setBigDecimal(19, sysOrder.getOrderAmount() == null ? null : sysOrder.getOrderAmount());
                pstmt.setString(20, sysOrder.getOrderTime() == null ? "" : sysOrder.getOrderTime());
                pstmt.setString(21, sysOrder.getDeliveryWarehouse() == null ? "" : sysOrder.getDeliveryWarehouse());
                pstmt.setString(22, sysOrder.getDeliveryWarehouseCode() == null ? "" : sysOrder.getDeliveryWarehouseCode());
                pstmt.setString(23, sysOrder.getShippingCarrierUsedCode() == null ? "" : sysOrder.getShippingCarrierUsedCode());
                pstmt.setString(24, sysOrder.getShippingCarrierUsed() == null ? "" : sysOrder.getShippingCarrierUsed());
                pstmt.setBigDecimal(25, sysOrder.getShippingServiceCost() == null ? null : sysOrder.getShippingServiceCost());

                pstmt.setBigDecimal(26, sysOrder.getEstimateShipCost() == null ? null : sysOrder.getEstimateShipCost());
                pstmt.setBigDecimal(27, sysOrder.getCommoditiesAmount() == null ? null : sysOrder.getCommoditiesAmount());
                pstmt.setString(28, sysOrder.getReferenceId() == null ? "" : sysOrder.getReferenceId());
                pstmt.setString(29, sysOrder.getBuyerCheckoutMessage() == null ? "" : sysOrder.getBuyerCheckoutMessage());
                pstmt.setString(30, sysOrder.getShipToName() == null ? "" : sysOrder.getShipToName());
                pstmt.setString(31, sysOrder.getShipToCountry() == null ? "" : sysOrder.getShipToCountry());
                pstmt.setString(32, sysOrder.getShipToCountryName() == null ? "" : sysOrder.getShipToCountryName());
                pstmt.setString(33, sysOrder.getShipToState() == null ? "" : sysOrder.getShipToState());
                pstmt.setString(34, sysOrder.getShipToCity() == null ? "" : sysOrder.getShipToCity());
                pstmt.setString(35, sysOrder.getShipToAddrStreet1() == null ? "" : sysOrder.getShipToAddrStreet1());
                pstmt.setString(36, sysOrder.getShipToAddrStreet2() == null ? "" : sysOrder.getShipToAddrStreet2());
                pstmt.setString(37, sysOrder.getShipToAddrStreet3() == null ? "" : sysOrder.getShipToAddrStreet3());
                pstmt.setString(38, sysOrder.getShipToPostalCode() == null ? "" : sysOrder.getShipToPostalCode());
                pstmt.setString(39, sysOrder.getShipToPhone() == null ? "" : sysOrder.getShipToPhone());
                pstmt.setString(40, sysOrder.getShipToEmail() == null ? "" : sysOrder.getShipToEmail());
                pstmt.setString(41, sysOrder.getEbayCarrierName() == null ? "" : sysOrder.getEbayCarrierName());
                pstmt.setString(42, sysOrder.getDeliveryMethodCode() == null ? "" : sysOrder.getDeliveryMethodCode());
                pstmt.setString(43, sysOrder.getDeliveryMethod() == null ? "" : sysOrder.getDeliveryMethod());
                pstmt.setString(44, sysOrder.getCreateBy() == null ? "" : sysOrder.getCreateBy());
                pstmt.setString(45, sysOrder.getUpdateBy() == null ? "" : sysOrder.getUpdateBy());
                pstmt.addBatch();
            } catch (SQLException e) {
                _log.error("_______insertBatchSysOrderList_______出错的ebay平台订单号为：{}__________错误信息：{}_________", sysOrder.getSysOrderId(), e);
            }
        }
        pstmt.executeBatch();
        pstmt.close();
    }

    public void insertBatchSysOrderDetailList(Connection conn, List<SysOrderDetail> inlist) throws Exception {
        final List<SysOrderDetail> tempexpList = inlist;
        String sql = "INSERT INTO tb_sys_order_detail " +
                "(sys_order_id, source_order_id, source_order_line_item_id, order_line_item_id, " +//4
                "bulk, weight, item_id, deliver_deadline, item_cost, item_url, item_name, item_name_en, item_attr, " +//9
                "item_price, sku, sku_title, sku_quantity, sku_shipping_fee,  " +//5
                "fare_type_amount, supplier_id, supplier_name, supply_chain_company_id, supply_chain_company_name, " +//5
                "supplier_sku, supplier_sku_title, supplier_sku_price, record_number, create_by, update_by)" +//6
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        for (SysOrderDetail sysOrderDetail : tempexpList) {
            try {
                BigDecimal decimal = new BigDecimal(0.00);
                pstmt.setString(1, sysOrderDetail.getSysOrderId() == null ? "" : sysOrderDetail.getSysOrderId());
                pstmt.setString(2, sysOrderDetail.getSourceOrderId() == null ? "" : sysOrderDetail.getSourceOrderId());
                pstmt.setString(3, sysOrderDetail.getSourceOrderLineItemId() == null ? "" : sysOrderDetail.getSourceOrderLineItemId());
                pstmt.setString(4, sysOrderDetail.getOrderLineItemId() == null ? "" : sysOrderDetail.getOrderLineItemId());

                pstmt.setBigDecimal(5, sysOrderDetail.getBulk() == null ? decimal : sysOrderDetail.getBulk());
                pstmt.setBigDecimal(6, sysOrderDetail.getWeight() == null ? decimal : sysOrderDetail.getWeight());
                pstmt.setLong(7, sysOrderDetail.getItemId() == null ? 0 : sysOrderDetail.getItemId());
                pstmt.setString(8, sysOrderDetail.getDeliverDeadline() == null ? "" : sysOrderDetail.getDeliverDeadline());
                pstmt.setBigDecimal(9, sysOrderDetail.getItemCost() == null ? decimal : sysOrderDetail.getItemCost());
                pstmt.setString(10, sysOrderDetail.getItemUrl() == null ? "" : sysOrderDetail.getItemUrl());
                pstmt.setString(11, sysOrderDetail.getItemName() == null ? "" : sysOrderDetail.getItemName());
                pstmt.setString(12, sysOrderDetail.getItemNameEn() == null ? "" : sysOrderDetail.getItemNameEn());
                pstmt.setString(13, sysOrderDetail.getItemAttr() == null ? "" : sysOrderDetail.getItemAttr());
                pstmt.setBigDecimal(14, sysOrderDetail.getItemPrice() == null ? decimal : sysOrderDetail.getItemPrice());

                pstmt.setString(15, sysOrderDetail.getSku() == null ? "" : sysOrderDetail.getSku());
                pstmt.setString(16, sysOrderDetail.getSkuTitle() == null ? "" : sysOrderDetail.getSkuTitle());
                pstmt.setInt(17, sysOrderDetail.getSkuQuantity() == null ? 0 : sysOrderDetail.getSkuQuantity());
                pstmt.setBigDecimal(18, sysOrderDetail.getSkuShippingFee() == null ? decimal : sysOrderDetail.getSkuShippingFee());
                pstmt.setString(19, sysOrderDetail.getFareTypeAmount() == null ? "" : sysOrderDetail.getFareTypeAmount());

                pstmt.setLong(20, sysOrderDetail.getSupplierId() == null ? null : sysOrderDetail.getSupplierId());
                pstmt.setString(21, sysOrderDetail.getSupplierName() == null ? "" : sysOrderDetail.getSupplierName());
                pstmt.setInt(22, sysOrderDetail.getSupplyChainCompanyId() == null ? null : sysOrderDetail.getSupplyChainCompanyId());
                pstmt.setString(23, sysOrderDetail.getSupplyChainCompanyName() == null ? "" : sysOrderDetail.getSupplyChainCompanyName());
                pstmt.setString(24, sysOrderDetail.getSupplierSku() == null ? "" : sysOrderDetail.getSupplierSku());
                pstmt.setString(25, sysOrderDetail.getSupplierSkuTitle() == null ? "" : sysOrderDetail.getSupplierSkuTitle());
                pstmt.setBigDecimal(26, sysOrderDetail.getSupplierSkuPrice() == null ? null : sysOrderDetail.getSupplierSkuPrice());
                pstmt.setInt(27, sysOrderDetail.getRecordNumber() == null ? 0 : sysOrderDetail.getRecordNumber());
                pstmt.setString(28, sysOrderDetail.getCreateBy() == null ? "" : sysOrderDetail.getCreateBy());
                pstmt.setString(29, sysOrderDetail.getUpdateBy() == null ? "" : sysOrderDetail.getUpdateBy());
                pstmt.addBatch();
            } catch (SQLException e) {
                _log.error("_________insertBatchSysOrderDetailList__________出错的ebay平台订单项ID为：{}__________错误信息：{}___________",
                        sysOrderDetail.getSourceOrderLineItemId(), e);
            }
        }
        pstmt.executeBatch();
        pstmt.close();
    }

    public void updateBatchConverStatusByOrderItemId(Connection conn, List<EbayOrderDetail> inlist) throws Exception {
        final List<EbayOrderDetail> tempexpList = inlist;
        String sql = "update tb_ebay_order_detail\n" +
                "set conver_sys_status = ?, update_by = ?\n" +
                "where order_line_item_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        for (EbayOrderDetail detail : tempexpList) {
            try {
                pstmt.setByte(1, detail.getConverSysStatus() == null ? 0 : detail.getConverSysStatus());
                pstmt.setString(2, detail.getUpdateBy() == null ? "" : detail.getUpdateBy());
                pstmt.setString(3, detail.getOrderLineItemId() == null ? "" : detail.getOrderLineItemId());
                pstmt.addBatch();
            } catch (SQLException e) {
                _log.error("_______updateBatchConverStatusByOrderItemId_______ebay平台订单转系统订单出错的ebay平台订单项ID为：{}_________错误信息：{}_________",
                        detail.getOrderLineItemId(), e);
            }
        }
        pstmt.executeBatch();
        pstmt.close();
    }

    public void updateBatchEbayOrderConverStatus(Connection conn, List<EbayOrder> inlist) throws SQLException {
        final List<EbayOrder> tempexpList = inlist;
        String sql = "update tb_ebay_order_status\n" +
                "set conver_sys_status = ?, update_by = ?\n" +
                "where order_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        for (EbayOrder order : tempexpList) {
            try {
                BigDecimal decimal = new BigDecimal(0.00);
                pstmt.setByte(1, order.getConverSysStatus() == null ? 0 : order.getConverSysStatus());
                pstmt.setString(2, order.getUpdateBy() == null ? "" : order.getUpdateBy());
                pstmt.setString(3, order.getOrderId() == null ? "" : order.getOrderId());
                pstmt.addBatch();
            } catch (SQLException e) {
                _log.error("_______updateBatchEbayOrderConverStatus_______ebay平台订单转系统订单出错的ebay平台订单项ID为：{}_________错误信息：{}_________",
                        order.getOrderId(), e);
            }
        }
        pstmt.executeBatch();
        pstmt.close();
    }
}
