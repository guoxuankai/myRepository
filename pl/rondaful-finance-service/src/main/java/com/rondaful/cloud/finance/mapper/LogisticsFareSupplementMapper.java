package com.rondaful.cloud.finance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.rondaful.cloud.finance.entity.LogisticsFareSupplement;

public interface LogisticsFareSupplementMapper extends BaseMapper<LogisticsFareSupplement> {

	@Select(" SELECT " + "supplement_id,"//
			+ "serial_no,"//
			+ "order_no,"//
			+ "create_time,"//
			+ "supplement_amount,"//
			+ "execute_time,"//
			+ "seller_id,"//
			+ "tb_status "//
			+ " FROM rdf_pl_logistics_fare_supplement WHERE tb_status='normal' and execute_time not null "//
			+ "")
	List<LogisticsFareSupplement> selectBySellerId(Integer sellerId);

}