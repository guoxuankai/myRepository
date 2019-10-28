package com.rondaful.cloud.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.finance.entity.RechargeRecord;
import com.rondaful.cloud.finance.entity.SellerAccount;
import com.rondaful.cloud.finance.enums.ExamineStatus;
import com.rondaful.cloud.finance.enums.ResponseCodeEnum;
import com.rondaful.cloud.finance.exception.GlobalException;
import com.rondaful.cloud.finance.mapper.RechargeRecordMapper;
import com.rondaful.cloud.finance.mapper.SellerAccountMapper;

/**
 * 充值业务实现类
 * 
 *
 */
@Service
public class RechargeService extends BaseRecordService<RechargeRecord> {

	@Autowired
	private RechargeRecordMapper mapper;

	@Autowired
	private SellerAccountMapper sellerMapper;

	public boolean examine(RechargeRecord record) {
		boolean success = mapper.updateByPrimaryKeySelective(record) > 0;
		// 审核通过且更新成功后加钱
		if (ExamineStatus.审核通过.name().equals(record.getExamineStatus()) && success) {
			RechargeRecord fullRecord = mapper.selectByPrimaryKey(record.getRechargeId());
			SellerAccount account = sellerMapper.selectBySellerId(fullRecord.getSellerId());
			if (account == null) {
				sellerInit(new SellerAccount(fullRecord.getSellerId(), fullRecord.getSellerName()));
			}
			account.setTotalAmount(account.getTotalAmount().add(record.getRechargeAmount()));
			account.setFreeAmount(account.getFreeAmount().add(record.getRechargeAmount()));
			account.setRechargeAmount(account.getRechargeAmount().add(record.getRechargeAmount()));
			boolean _success = sellerMapper.updateByPrimaryKeySelective(account) > 0;
			if (!_success) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "更新冲突，请稍后再试!");
			}
		}
		return success;
	}

	public boolean sellerInit(SellerAccount seller) {
		return sellerMapper.insert(seller) > 0;// 不允许插入重复数据
	}

}
