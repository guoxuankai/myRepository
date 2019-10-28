package com.rondaful.cloud.finance.mapper;

import org.apache.ibatis.annotations.Select;

import com.rondaful.cloud.finance.entity.SupplierAccount;

public interface SupplierAccountMapper extends BaseMapper<SupplierAccount> {

	@Select(" SELECT "//
			+ " supplier_account_id,"//
			+ " unsettled_amount,"//
			+ " settled_amount,"//
			+ " withdrawals_amount,"//
			+ " withdrawing_amount,"//
			+ " frozen_amount,"//
			+ " free_amount,"//
			+ " total_amount,"//
			+ " supplier_id,"//
			+ " supplier_name,"//
			+ " version,"//
			+ " modify_time,"//
			+ " create_time,"//
			+ " tb_status "//
			+ " FROM rdf_pl_supplier_account "//
			+ " WHERE supplier_id=#{supplierId} and tb_status='normal' "//
			+ "")
	SupplierAccount selectBySupplierId(Integer supplierId);

}