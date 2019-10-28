package com.rondaful.cloud.finance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.rondaful.cloud.finance.entity.RechargeRecord;
import com.rondaful.cloud.finance.vo.conditions.BaseConditionVo;

public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {

	@Select("<script>"//
			+ " SELECT "//
			+ " recharge_id, "//
			+ " recharge_no, "//
			+ " recharge_account, "//
			+ " recharge_amount, "//
			+ " recharge_type, "//
			+ " create_time, "//
			+ " transfer_receipt_url, "//
			+ " modify_time, "//
			+ " examine_status, "//
			+ " tb_status, "//
			+ " remark, "//
			+ " version, "//
			+ " seller_name, "//
			+ " seller_id, "//
			+ " trans_serial_no, "//
			+ " annotation "//
			+ " "//
			+ " FROM rdf_pl_recharge_record "//
			+ " WHERE "//
			+ " "//
			+ " tb_status='normal' "
			+ "  <if test='beginDate != null'> and create_time &gt;=#{beginDate} </if> "//
			+ "  <if test='endDate != null'> and create_time &lt;=#{endDate} </if> "//
			+ "  <if test='id != null'> and recharge_id =#{id} </if>    "//
			+ "  <if test='examineStatus != null'> and examine_status =#{examineStatus} </if>    "//
			+ "  <if test='serialNo != null'> and recharge_no =#{serialNo} </if>    "//
			+ "  <if test='transSerialNo != null'> and trans_serial_no =#{transSerialNo} </if>    "//
			+ "  <if test='rechargeAccount != null'> and recharge_account =#{rechargeAccount} </if>    "//
			+ "  <if test='rechargeType != null'> and recharge_type =#{rechargeType} </if>    "//
			+ ""//
			+ "</script>")
	@Override
	List<RechargeRecord> pageQueryWithConditions(BaseConditionVo conditions);

}