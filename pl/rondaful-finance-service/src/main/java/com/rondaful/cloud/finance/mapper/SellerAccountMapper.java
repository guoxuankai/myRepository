package com.rondaful.cloud.finance.mapper;

import org.apache.ibatis.annotations.Select;

import com.rondaful.cloud.finance.entity.SellerAccount;

public interface SellerAccountMapper extends BaseMapper<SellerAccount> {

	@Select(" SELECT "//
			+ " seller_account_id,"//
			+ " recharge_amount,"//
			+ " consumed_amount,"//
			+ " frozen_amount,"//
			+ " free_amount,"//
			+ " total_amount,"//
			+ " seller_id,"//
			+ " seller_name,"//
			+ " create_time,"//
			+ " modify_time,"//
			+ " version,"//
			+ " create_time,"//
			+ " tb_status "//
			+ " FROM rdf_pl_seller_account "//
			+ " WHERE seller_id=#{sellerId} and tb_status='normal' "//
			+ "")
	SellerAccount selectBySellerId(Integer sellerId);
	
	
	

}