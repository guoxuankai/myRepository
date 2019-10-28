package com.rondaful.cloud.finance.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.finance.entity.AdminAccount;
import com.rondaful.cloud.finance.entity.OrderRecord;
import com.rondaful.cloud.finance.entity.SettlementRecord;
import com.rondaful.cloud.finance.entity.SettlementRegistInfo;
import com.rondaful.cloud.finance.entity.SupplierAccount;
import com.rondaful.cloud.finance.entity.SystemFinanceRecord;
import com.rondaful.cloud.finance.enums.IncomeType;
import com.rondaful.cloud.finance.enums.ResponseCodeEnum;
import com.rondaful.cloud.finance.enums.SettlementCycle;
import com.rondaful.cloud.finance.enums.TradeType;
import com.rondaful.cloud.finance.exception.GlobalException;
import com.rondaful.cloud.finance.mapper.AdminAccountMapper;
import com.rondaful.cloud.finance.mapper.OrderRecordMapper;
import com.rondaful.cloud.finance.mapper.SettlementRecordMapper;
import com.rondaful.cloud.finance.mapper.SettlementRegistInfoMapper;
import com.rondaful.cloud.finance.mapper.SupplierAccountMapper;
import com.rondaful.cloud.finance.mapper.SystemFinanceRecordMapper;
import com.rondaful.cloud.finance.utils.CalendarUtil;

/**
 * 结算业务实现类
 *
 */
@Service
public class SettlementService extends BaseRecordService<SettlementRecord> {

	private static final Logger logger = LoggerFactory.getLogger(SettlementService.class);

	@Autowired
	private SettlementRecordMapper mapper;

	@Autowired
	private SettlementRegistInfoMapper registMapper;

	@Autowired
	private OrderRecordMapper orderMapper;

	@Autowired
	private SystemFinanceRecordMapper recordMapper;

	@Autowired
	private AdminAccountMapper adminMapper;

	@Autowired
	private SupplierAccountMapper supplierMapper;

	// 供应商结算注册
	public boolean settlementRegist(SettlementRegistInfo registInfo) {
		setNextSettlementTime(registInfo);
		return registMapper.insertSelective(registInfo) > 0;
	}

	private void setNextSettlementTime(SettlementRegistInfo registInfo) {
		SettlementCycle settlementCycle = SettlementCycle.valueOf(registInfo.getSettlementCycle());

		switch (settlementCycle) {
		case 周结:
			registInfo.setNextSettlementTime(CalendarUtil.getNextWeekMonday());
			break;
		case 半月结:
			registInfo.setNextSettlementTime(CalendarUtil.getNext15th());
			break;
		case 月结:
			registInfo.setNextSettlementTime(CalendarUtil.getNextMonth1st());
			break;

		default:
			break;
		}
	}

	// 供应商结算信息修改
	public boolean settlementModify(SettlementRegistInfo registInfo) {
		return registMapper.updateByPrimaryKeySelective(registInfo) > 0;// 根据SupplierID更新
	}

	// 结算
	public boolean settle() {
		// 查询所有到达结算日期的供应商
		List<SettlementRegistInfo> infos = registMapper.selectAllSettleableInfos();

		for (SettlementRegistInfo info : infos) {
			SupplierAccount supplier = supplierMapper.selectBySupplierId(info.getSupplierId());

			AdminAccount admin = adminMapper.selectEnableAdmin();

			List<OrderRecord> orders = orderMapper.selectBySupplierIdForSettle(info.getSupplierId());

			BigDecimal unsettledAmount = supplier.getUnsettledAmount();
			BigDecimal productUnsettledAmount = BigDecimal.ZERO;

			for (OrderRecord order : orders) {
				productUnsettledAmount = productUnsettledAmount.add(order.getProductAmount());
			}

			// 核对未结算金额和订单汇总信息
			if (!(unsettledAmount.compareTo(productUnsettledAmount) == 0)) {
				logger.error("核算金额不正常：" + " [unsettledAmount:" + unsettledAmount + "  productUnsettledAmount:"
						+ productUnsettledAmount + "]");
				return false;
			}

			// 修改供应商账户信息
			supplier.setUnsettledAmount(BigDecimal.ZERO);
			supplier.setFreeAmount(supplier.getFreeAmount().add(unsettledAmount));
			supplier.setTotalAmount(supplier.getTotalAmount().add(unsettledAmount));
			supplier.setSettledAmount(supplier.getSettledAmount().add(unsettledAmount));
			boolean supplierFlag = supplierMapper.updateByPrimaryKeySelective(supplier) > 0;

			// 修改系统账户信息
			admin.setSettledAmount(admin.getSettledAmount().add(unsettledAmount));
			admin.setTotalAmount(admin.getTotalAmount().subtract(unsettledAmount));
			admin.setFreeAmount(admin.getFreeAmount().subtract(unsettledAmount));
			boolean adminFlag = adminMapper.updateByPrimaryKeySelective(admin) > 0;

			// 生成结算单信息
			SettlementRecord settlementRecord = new SettlementRecord(info.getSettlementCycle(), unsettledAmount,
					supplier.getTotalAmount(), supplier.getSupplierName(), supplier.getSupplierId());
			boolean settlementFlag = mapper.insertSelective(settlementRecord) > 0;

			// 生成财务报表信息
			SystemFinanceRecord systemFinanceRecord = new SystemFinanceRecord(TradeType.订单结算.name(),
					settlementRecord.getSettlementNo(), unsettledAmount, IncomeType.支出.name(), admin.getTotalAmount());
			boolean financeFlag = recordMapper.insertSelective(systemFinanceRecord) > 0;

			// 修改订单信息
			boolean orderFlag = orderMapper.updateSettlementStatus(settlementRecord.getSettlementId(),
					orders.stream().map(o -> {
						return o.getOrderId();
					}).collect(Collectors.toList())) > 0;

			// 更新结算注册信息
			info.setLastSettlementTime(new Date());
			setNextSettlementTime(info);
			boolean infoFlag = registMapper.updateByPrimaryKeySelective(info) > 0;

			if (!supplierFlag || !adminFlag || !settlementFlag || !financeFlag || !orderFlag || !infoFlag) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002.getCode(), "结算异常!");
			}
		}

		return true;
	}

}
