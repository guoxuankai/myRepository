package com.rondaful.cloud.finance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.rondaful.cloud.finance.entity.SettlementRecord;
import com.rondaful.cloud.finance.vo.conditions.BaseConditionVo;

public interface SettlementRecordMapper extends BaseMapper<SettlementRecord> {
	
	@Select("<script>"//
			+ " SELECT "//
			+ " settlement_id, "//
			+ " settlement_no, "//
			+ " create_time, "//
			+ " settlement_cycle, "//
			+ " settlement_amount, "//
			+ " cost_type, "//
			+ " rest_balance, "//
			+ " supplier_name, "//
			+ " supplier_id, "//
			+ " tb_status "//
			+ " "//
			+ " FROM rdf_pl_settlement_record "//
			+ " WHERE "//
			+ " "//
			+ " tb_status='normal' "
			+ "  <if test='beginDate != null'> and create_time &gt;=#{beginDate} </if> "//
			+ "  <if test='endDate != null'> and create_time &lt;=#{endDate} </if> "//
			+ "  <if test='id != null'> and settlement_id =#{id} </if>    "//
			+ "  <if test='serialNo != null'> and settlement_no =#{serialNo} </if>    "//
			+ ""//
			+ "</script>")
	@Override
	List<SettlementRecord> pageQueryWithConditions(BaseConditionVo conditions);
	
	
}