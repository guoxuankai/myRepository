package com.rondaful.cloud.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.finance.entity.SupplierAccount;
import com.rondaful.cloud.finance.entity.WithdrawalRecord;
import com.rondaful.cloud.finance.enums.ExamineStatus;
import com.rondaful.cloud.finance.enums.ResponseCodeEnum;
import com.rondaful.cloud.finance.exception.GlobalException;
import com.rondaful.cloud.finance.mapper.SupplierAccountMapper;
import com.rondaful.cloud.finance.mapper.WithdrawalRecordMapper;

/**
 * 提现业务实现类
 *
 */
@Service
public class WithdrawalService extends BaseRecordService<WithdrawalRecord> {

	@Autowired
	private WithdrawalRecordMapper mapper;

	@Autowired
	private SupplierAccountMapper supplierMapper;

	public boolean remittance(WithdrawalRecord record) {
		boolean success = mapper.updateByPrimaryKeySelective(record) > 0;
		if (success) {// 成功后打钱
			WithdrawalRecord fullRecord = mapper.selectByPrimaryKey(record.getWithdrawalId());
			SupplierAccount account = supplierMapper.selectBySupplierId(fullRecord.getSupplierId());
			if (account == null) {
				supplierInit(new SupplierAccount(fullRecord.getSupplierId(), fullRecord.getSupplierName()));
			}
			account.setTotalAmount(account.getTotalAmount().add(record.getWithdrawalAmount()));
			account.setFreeAmount(account.getFreeAmount().add(record.getWithdrawalAmount()));
			account.setWithdrawingAmount(account.getWithdrawingAmount().subtract(record.getWithdrawalAmount()));
			account.setWithdrawalsAmount(account.getWithdrawalsAmount().add(record.getWithdrawalAmount()));
			boolean _success = supplierMapper.updateByPrimaryKeySelective(account) > 0;
			if (!_success) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "更新冲突，请稍后再试!");
			}
		}
		return success;
	}

	public boolean supplierInit(SupplierAccount supplier) {
		return supplierMapper.insert(supplier) > 0;// 不允许插入重复数据
	}

	@Override
	public boolean request(WithdrawalRecord record) {
		boolean recordFlag = mapper.insertSelective(record) > 0;
		SupplierAccount account = supplierMapper.selectBySupplierId(record.getSupplierId());
		account.setWithdrawingAmount(account.getWithdrawingAmount().add(record.getWithdrawalAmount()));
		boolean supplierFlag = supplierMapper.updateByPrimaryKeySelective(account) > 0;

		if (!recordFlag || !supplierFlag) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "保存失败,请稍后再试!");
		}

		return true;
	}

	@Override
	public boolean examine(WithdrawalRecord record) {
		boolean recordFlag = mapper.updateByPrimaryKeySelective(record) > 0;
		if (ExamineStatus.作废.name().equals(record.getExamineStatus())
				|| ExamineStatus.审核不通过.name().equals(record.getExamineStatus())) {
			SupplierAccount account = supplierMapper.selectBySupplierId(record.getSupplierId());
			account.setWithdrawingAmount(account.getWithdrawingAmount().subtract(record.getWithdrawalAmount()));
			boolean supplierFlag = supplierMapper.updateByPrimaryKeySelective(account) > 0;

			if (!recordFlag || !supplierFlag) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "保存失败,请稍后再试!");
			}
		}
		return recordFlag;
	}

	@Override
	public boolean resubmission(WithdrawalRecord record) {
		boolean recordFlag = mapper.updateByPrimaryKey(record) > 0;
		SupplierAccount account = supplierMapper.selectBySupplierId(record.getSupplierId());
		account.setWithdrawingAmount(account.getWithdrawingAmount().add(record.getWithdrawalAmount()));
		boolean supplierFlag = supplierMapper.updateByPrimaryKeySelective(account) > 0;
		if (!recordFlag || !supplierFlag) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "保存失败,请稍后再试!");
		}

		return true;
	}

}
