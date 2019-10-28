package com.rondaful.cloud.finance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.rondaful.cloud.finance.entity.SystemFinanceRecord;
import com.rondaful.cloud.finance.vo.conditions.BaseConditionVo;

public interface SystemFinanceRecordMapper extends BaseMapper<SystemFinanceRecord> {

	@Select("<script>"//
			+ " SELECT "//
			+ " record_id, "//
			+ " create_time, "//
			+ " trade_type, "//
			+ " serial_no, "//
			+ " actual_amount, "//
			+ " type, "//
			+ " rest_balance, "//
			+ " tb_status "//
			+ " "//
			+ " FROM rdf_pl_system_finance_record "//
			+ " WHERE "//
			+ " "//
			+ " tb_status='normal' " + "  <if test='beginDate != null'> and create_time &gt;=#{beginDate} </if> "//
			+ "  <if test='endDate != null'> and create_time &lt;=#{endDate} </if> "//
			+ "  <if test='id != null'> and record_id =#{id} </if>    "//
			+ "  <if test='examineStatus != null'> and examine_status =#{examineStatus} </if>    "//
			+ "  <if test='serialNo != null'> and serial_no =#{serialNo} </if>    "//
			+ "  <if test='tradeType != null'> and trade_type =#{tradeType} </if>    "//
			+ "  <if test='type != null'> and type =#{type} </if>    "//
			+ ""//
			+ "</script>")
	@Override
	List<SystemFinanceRecord> pageQueryWithConditions(BaseConditionVo conditions);

}