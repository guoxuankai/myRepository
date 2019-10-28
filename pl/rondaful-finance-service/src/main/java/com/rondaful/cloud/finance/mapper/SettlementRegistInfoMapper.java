package com.rondaful.cloud.finance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.rondaful.cloud.finance.entity.SettlementRegistInfo;

public interface SettlementRegistInfoMapper extends BaseMapper<SettlementRegistInfo> {

	@Select("SELECT" + " register_id,"//
			+ " supplier_id,"//
			+ " supplier_name,"//
			+ " create_time,"//
			+ " settlement_cycle,"//
			+ " last_settlement_time,"//
			+ " version,"//
			+ " tb_status,"//
			+ " next_settlement_time "//
			+ " FROM rdf_pl_settlement_regist_info"//
			+ " WHERE tb_status='normal' and DATE(NOW())= DATE(next_settlement_time) "//
			+ ""//
			+ "")
	List<SettlementRegistInfo> selectAllSettleableInfos();

}