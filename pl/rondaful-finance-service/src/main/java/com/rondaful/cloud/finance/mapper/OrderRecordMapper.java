package com.rondaful.cloud.finance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.rondaful.cloud.finance.entity.OrderRecord;
import com.rondaful.cloud.finance.vo.conditions.BaseConditionVo;

public interface OrderRecordMapper extends BaseMapper<OrderRecord> {

	@Update(" <script> "
			+ " UPDATE rdf_pl_order_record "//
			+ " set settlement_id=#{settlementId} "//
			+ " where tb_status='normal' and  order_id in  "//
			+ " <foreach collection='orderIds' index='index' item='item' open='(' separator=',' close=')'> " + 
			"          #{item} " + 
			"  </foreach> "//
			+ "  "//
			+ ""
			+ "</script>")
	int updateSettlementStatus(Integer settlementId, List<Integer> orderIds);

	/**
	 * 根据supplierId查询订单记录 (仅查询未结算)
	 * 
	 * @param supplierId
	 * @return
	 */
	@Select("SELECT "//
			+ "order_id,"//
			+ "serial_no,"//
			+ "order_no,"//
			+ "create_time,"//
			+ "modify_time,"//
			+ "seller_name,"//
			+ "product_amount,"//
			+ "logistics_fare,"//
			+ "payable_amount,"//
			+ "actual_amount,"//
			+ "fill_logistics_fare,"//
			+ "examine_status,"//
			+ "remark,"//
			+ "version,"//
			+ "tb_status,"//
			+ "seller_id,"//
			+ "seller_account,"//
			+ "actual_logistics_fare,"//
			+ "supplier_id,"//
			+ "supplier_name,"//
			+ " settlement_id "//
			+ " FROM rdf_pl_order_record "//
			+ " WHERE supplier_id=#{supplierId} and tb_status='normal' and settlement_id is null and examine_status in ('付款成功','已付款补扣失败') "//
			+ "")
	List<OrderRecord> selectBySupplierIdForSettle(Integer supplierId);

	@Select("SELECT "//
			+ "order_id,"//
			+ "serial_no,"//
			+ "order_no,"//
			+ "create_time,"//
			+ "modify_time,"//
			+ "seller_name,"//
			+ "product_amount,"//
			+ "logistics_fare,"//
			+ "payable_amount,"//
			+ "actual_amount,"//
			+ "fill_logistics_fare,"//
			+ "examine_status,"//
			+ "remark,"//
			+ "version,"//
			+ "tb_status,"//
			+ "seller_id,"//
			+ "seller_account,"//
			+ "actual_logistics_fare,"//
			+ "supplier_id,"//
			+ "supplier_name,"//
			+ " settlement_id "//
			+ " FROM rdf_pl_order_record "//
			+ " WHERE order_no=#{orderNo} and tb_status='normal' "//
			+ "")
	OrderRecord selectByOrderNo(String orderNo);

	@Select("<script>"//
			+ " SELECT "//
			+ "order_id,"//
			+ "serial_no,"//
			+ "order_no,"//
			+ "create_time,"//
			+ "modify_time,"//
			+ "seller_name,"//
			+ "product_amount,"//
			+ "logistics_fare,"//
			+ "payable_amount,"//
			+ "actual_amount,"//
			+ "fill_logistics_fare,"//
			+ "examine_status,"//
			+ "remark,"//
			+ "version,"//
			+ "tb_status,"//
			+ "seller_id,"//
			+ "seller_account,"//
			+ "actual_logistics_fare,"//
			+ "supplier_id,"//
			+ "supplier_name,"//
			+ " settlement_id "//
			+ " "//
			+ " FROM rdf_pl_order_record "//
			+ " WHERE "//
			+ " "//
			+ " tb_status='normal' " + "  <if test='beginDate != null'> and create_time &gt;=#{beginDate} </if> "//
			+ "  <if test='endDate != null'> and create_time &lt;=#{endDate} </if> "//
			+ "  <if test='id != null'> and order_id =#{id} </if>    "//
			+ "  <if test='examineStatus != null'> and examine_status =#{examineStatus} </if>    "//
			+ "  <if test='serialNo != null'> and serial_no =#{serialNo} </if>    "//
			+ "  <if test='sellerAccount != null'> and seller_account =#{sellerAccount} </if>    "//
			+ "  <if test='orderNo != null'> and order_no =#{orderNo} </if>    "//
			+ "  <if test='settlementId != null'> and settlement_id =#{settlementId} </if>    "//
			+ ""//
			+ "</script>")
	@Override
	List<OrderRecord> pageQueryWithConditions(BaseConditionVo conditions);

}