package com.rondaful.cloud.finance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.rondaful.cloud.finance.entity.WithdrawalRecord;
import com.rondaful.cloud.finance.vo.conditions.BaseConditionVo;

public interface WithdrawalRecordMapper extends BaseMapper<WithdrawalRecord> {
	
	@Select("<script>"//
			+ " SELECT "//
			+ " withdrawal_id, "//
			+ " withdrawal_no, "//
			+ " create_time, "//
			+ " pay_time, "//
			+ " modify_time, "//
			+ " supplier_name, "//
			+ " withdrawal_amount, "//
			+ " withdrawal_type, "//
			+ " bill_url, "//
			+ " transfer_receipt_url, "//
			+ " examine_status, "//
			+ " tb_status, "//
			+ " remark, "//
			+ " version, "//
			+ " supplier_id, "//
			+ " trans_serial_no, "//
			+ " annotation, "//
			+ " withdrawal_account "//
			+ " "//
			+ " FROM rdf_pl_withdrawal_record "//
			+ " WHERE "//
			+ " "//
			+ " tb_status='normal' "
			+ "  <if test='beginDate != null'> and create_time &gt;=#{beginDate} </if> "//
			+ "  <if test='endDate != null'> and create_time &lt;=#{endDate} </if> "//
			+ "  <if test='id != null'> and withdrawal_id =#{id} </if>    "//
			+ "  <if test='examineStatus != null'> and examine_status =#{examineStatus} </if>    "//
			+ "  <if test='serialNo != null'> and withdrawal_no =#{serialNo} </if>    "//
			
			+ "  <if test='withdrawalType != null'> and withdrawal_type =#{withdrawalType} </if>    "//
			+ "  <if test='withdrawalAccount != null'> and withdrawal_account =#{withdrawalAccount} </if>    "//
			+ ""//
			+ "</script>")
	@Override
	List<WithdrawalRecord> pageQueryWithConditions(BaseConditionVo conditions);
	
}