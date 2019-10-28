package com.rondaful.cloud.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.finance.entity.AdminAccount;
import com.rondaful.cloud.finance.entity.SellerAccount;
import com.rondaful.cloud.finance.entity.SupplierAccount;
import com.rondaful.cloud.finance.mapper.AdminAccountMapper;
import com.rondaful.cloud.finance.mapper.SellerAccountMapper;
import com.rondaful.cloud.finance.mapper.SupplierAccountMapper;

/**
 * 账户相关业务实现
 *
 */
@Service
public class AccountService {

	@Autowired
	private AdminAccountMapper adminMapper;

	@Autowired
	private SellerAccountMapper sellerMapper;

	@Autowired
	private SupplierAccountMapper supplierMapper;

	// 初始化创建Account
	public boolean adminInit(AdminAccount admin) {
		return adminMapper.insert(admin) > 0;// 不允许插入重复数据
	}

	public boolean sellerInit(SellerAccount seller) {
		return sellerMapper.insert(seller) > 0;// 不允许插入重复数据
	}

	public boolean supplierInit(SupplierAccount supplier) {
		return supplierMapper.insert(supplier) > 0;// 不允许插入重复数据
	}

	public AdminAccount getAdmin() {
		return adminMapper.selectEnableAdmin();
	}

	public SellerAccount getSeller(Integer id) {
		return sellerMapper.selectBySellerId(id);
	}

	public SupplierAccount getSupplier(Integer id) {
		return supplierMapper.selectBySupplierId(id);
	}

}
