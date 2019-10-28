package com.rondaful.cloud.finance.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.finance.entity.AdminAccount;
import com.rondaful.cloud.finance.entity.LogisticsFareSupplement;
import com.rondaful.cloud.finance.entity.OrderRecord;
import com.rondaful.cloud.finance.entity.SellerAccount;
import com.rondaful.cloud.finance.entity.SupplierAccount;
import com.rondaful.cloud.finance.entity.SystemFinanceRecord;
import com.rondaful.cloud.finance.enums.ExamineStatus;
import com.rondaful.cloud.finance.enums.IncomeType;
import com.rondaful.cloud.finance.enums.ResponseCodeEnum;
import com.rondaful.cloud.finance.enums.TradeType;
import com.rondaful.cloud.finance.exception.GlobalException;
import com.rondaful.cloud.finance.mapper.AdminAccountMapper;
import com.rondaful.cloud.finance.mapper.LogisticsFareSupplementMapper;
import com.rondaful.cloud.finance.mapper.OrderRecordMapper;
import com.rondaful.cloud.finance.mapper.SellerAccountMapper;
import com.rondaful.cloud.finance.mapper.SupplierAccountMapper;
import com.rondaful.cloud.finance.mapper.SystemFinanceRecordMapper;

/**
 * 订单相关业务实现
 *
 */
@Service
public class OrderService extends BaseRecordService<OrderRecord> {

	@Autowired
	private OrderRecordMapper mapper;

	@Autowired
	private SellerAccountMapper sellerMapper;

	@Autowired
	private LogisticsFareSupplementMapper supplementMapper;

	@Autowired
	private SystemFinanceRecordMapper recordMapper;

	@Autowired
	private AdminAccountMapper adminMapper;

	@Autowired
	private SupplierAccountMapper supplierMapper;

	// 保存订单,卖家冻结可用
	public boolean request(OrderRecord record) {
		SellerAccount seller = sellerMapper.selectBySellerId(record.getSellerId());
		if (seller == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "该用户账户不存在,请联系客服处理!");
		if (seller.getFreeAmount().compareTo(record.getPayableAmount()) < 0)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "账户余额不足以支付,请充值!");

		boolean success = mapper.insertSelective(record) > 0;
		if (success) {
			seller.setFreeAmount(seller.getFreeAmount().subtract(record.getPayableAmount()));
			seller.setFrozenAmount(seller.getFrozenAmount().add(record.getPayableAmount()));

			boolean _success = sellerMapper.updateByPrimaryKeySelective(seller) > 0;
			if (!_success) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "冻结失败，请稍后再试!");
			}
		}
		return success;
	}

	// 确认发货,卖家冻结转为扣款,补扣物流费,系统账户增加,增加财务报表信息
	public boolean confirm(String orderNo, BigDecimal supplementAmount) {
		OrderRecord record = mapper.selectByOrderNo(orderNo);
		checkStatus(record);

		SellerAccount seller = sellerMapper.selectBySellerId(record.getSellerId());
		AdminAccount admin = adminMapper.selectEnableAdmin();
		SupplierAccount supplier = supplierMapper.selectBySupplierId(record.getSupplierId());

		// 修改订单状态
		record.setActualLogisticsFare(record.getLogisticsFare().add(supplementAmount));
		record.setActualAmount(record.getLogisticsFare().add(supplementAmount));
		record.setFillLogisticsFare(supplementAmount);
		record.setExamineStatus(ExamineStatus.付款成功.name());// 默认付款成功

		BigDecimal occurAmount = record.getPayableAmount();// 实际扣款金额

		boolean supplementFlag = true;
		if (BigDecimal.ZERO.compareTo(supplementAmount) < 0) {// 如果有补扣金额
			if (seller.getFreeAmount().compareTo(supplementAmount) < 0) {
				// 可用金额不足以补扣时,设置已付款补扣失败,保存补扣单
				record.setExamineStatus(ExamineStatus.已付款补扣失败.name());
				LogisticsFareSupplement supplement = new LogisticsFareSupplement(record.getSerialNo(),
						record.getOrderNo(), supplementAmount, record.getSellerId());
				supplementFlag = supplementMapper.insertSelective(supplement) > 0;
			} else {
				seller.setFreeAmount(seller.getFreeAmount().subtract(supplementAmount));
				seller.setTotalAmount(seller.getTotalAmount().subtract(supplementAmount));
				seller.setConsumedAmount(seller.getConsumedAmount().add(supplementAmount));
				occurAmount.add(supplementAmount);
			}
		} else {
			// 当补扣金额为负时,则补充卖家可用和总金额
			seller.setFreeAmount(seller.getFreeAmount().add(supplementAmount.abs()));
			seller.setTotalAmount(seller.getTotalAmount().add(supplementAmount.abs()));
			seller.setConsumedAmount(seller.getConsumedAmount().subtract(supplementAmount.abs()));
			occurAmount.add(supplementAmount);
		}

		// 修改卖家冻结金额
		seller.setFrozenAmount(seller.getFrozenAmount().subtract(record.getPayableAmount()));
		seller.setTotalAmount(seller.getTotalAmount().subtract(record.getPayableAmount()));

		boolean sellerFlag = sellerMapper.updateByPrimaryKeySelective(seller) > 0;

		boolean orderFlag = mapper.updateByPrimaryKey(record) > 0;

		SystemFinanceRecord financeRecord = new SystemFinanceRecord(TradeType.订单支付.name(), record.getSerialNo(),
				record.getActualAmount(), IncomeType.收入.name(), admin.getTotalAmount());
		boolean financeFlag = recordMapper.insertSelective(financeRecord) > 0;

		admin.setFreeAmount(admin.getFreeAmount().add(occurAmount));
		admin.setTotalAmount(admin.getTotalAmount().add(occurAmount));
		admin.setPaidAmount(admin.getPaidAmount().add(occurAmount));

		boolean adminFlag = adminMapper.updateByPrimaryKeySelective(admin) > 0;

		supplier.setUnsettledAmount(supplier.getUnsettledAmount().add(record.getProductAmount()));
		boolean supplierFlag = supplierMapper.updateByPrimaryKeySelective(supplier) > 0;

		if (!supplementFlag || !sellerFlag || !orderFlag || !financeFlag || !adminFlag || !supplierFlag) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "订单确认支付失败!");
		}
		return true;
	}

	private void checkStatus(OrderRecord record) {
		if (record == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "该订单不存在!");
		if (ExamineStatus.冻结中.name().equals(record.getExamineStatus()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "订单状态不在冻结中!");
	}

	// 订单取消,释放冻结金额
	public boolean cancel(String orderNo) {
		OrderRecord record = mapper.selectByOrderNo(orderNo);
		checkStatus(record);
		// 修改订单支付明细状态为已作废
		record.setExamineStatus(ExamineStatus.作废.name());

		boolean success = mapper.updateByPrimaryKey(record) > 0;
		if (success) {
			// 取消冻结金额
			SellerAccount seller = sellerMapper.selectBySellerId(record.getSellerId());
			seller.setFreeAmount(seller.getFreeAmount().add(record.getPayableAmount()));
			seller.setFrozenAmount(seller.getFrozenAmount().subtract(record.getPayableAmount()));

			boolean _success = sellerMapper.updateByPrimaryKeySelective(seller) > 0;
			if (!_success) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "订单取消失败，请稍后再试!");
			}
		}
		return success;
	}

	// 订单退款

}
