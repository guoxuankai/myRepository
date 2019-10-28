package com.rondaful.cloud.finance.mapper;

import org.apache.ibatis.annotations.Select;

import com.rondaful.cloud.finance.entity.AdminAccount;

public interface AdminAccountMapper extends BaseMapper<AdminAccount> {

	@Select(" SELECT " + "admin_account_id,"//
			+ "paid_amount,"//
			+ "settled_amount,"//
			+ "refunded_amount,"//
			+ "frozen_amount,"//
			+ "free_amount,"//
			+ "total_amount,"//
			+ "create_time,"//
			+ "modify_time,"//
			+ "version,"//
			+ "tb_status "//
			+ " FROM rdf_pl_admin_account WHERE tb_status='normal' limit 1 "//
			+ "")
	AdminAccount selectEnableAdmin();

}