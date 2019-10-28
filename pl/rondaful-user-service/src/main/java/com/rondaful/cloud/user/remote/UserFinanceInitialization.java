package com.rondaful.cloud.user.remote;

import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.user.controller.model.hystrix.UserFananceVo;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.rabbitmq.TestSender;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 远程调用财务接口
 * @author Administrator
 *
 */
@FeignClient(name = "rondaful-finance-service", fallback = UserFinanceInitialization.UserFinanceInitializationImpl.class)
public interface UserFinanceInitialization {

	/**
	 * 	卖家账户初始化
	 * @param sellerId
	 * @param sellerName
	 * @return
	 */
	@PostMapping("/account/seller/init")
	public Object sellerInit(@RequestParam("sellerId")Integer sellerId,//卖家ID
							 @RequestParam("sellerName")String sellerName,//卖家名称
							 @RequestParam("supplyCompanyId")Integer supplyCompanyId,//供应链公司ID
							 @RequestParam("supplyCompanyName")String supplyCompanyName,//供应链公司名称
							 @RequestParam("contracts")String contracts,//联系人
							 @RequestParam("contractNumber")String contractNumber,//联系人电话
							 @RequestParam("country")String country,//国家/地区
							 @RequestParam("province")String province//省/市
							);

	/**
	 * 卖家修改
	 * @param sellerId
	 * @param sellerName
	 * @param supplyCompanyId
	 * @param supplyCompanyName
	 * @param contracts
	 * @param contractNumber
	 * @param country
	 * @param province
	 * @return
	 */
	@PostMapping("/account/seller/update")
	public Object sellerUpdate(@RequestParam("sellerId")Integer sellerId,//卖家ID
							   @RequestParam("sellerName")String sellerName,//卖家名称
							   @RequestParam("supplyCompanyId")Integer supplyCompanyId,//供应链公司ID
							   @RequestParam("supplyCompanyName")String supplyCompanyName,//供应链公司名称
							   @RequestParam("contracts")String contracts,//联系人
							   @RequestParam("contractNumber")String contractNumber,//联系人电话
							   @RequestParam("country")String country,//国家/地区
							   @RequestParam("province")String province//省/市
							);

	/**
	 * 	供应商账户初始化
	 * @param supplierId
	 * @param supplierName
	 * @return
	 */
	@PostMapping("/account/supplier/init")
	public Object supplierInit(@RequestParam("supplierId")Integer supplierId,//供应商ID
							   @RequestParam("supplierName")String supplierName,//供应商名称
							   @RequestParam("supplierAccount")String supplierAccount,//供应商账号
							   @RequestParam("supplyCompanyId")Integer supplyCompanyId,//供应链公司ID
							   @RequestParam("supplyCompanyName")String supplyCompanyName,//供应链公司名称
							   @RequestParam("contracts")String contracts,//联系人
							   @RequestParam("contractNumber")String contractNumber,//联系人电话
							   @RequestParam("country")String country,//国家/地区
							   @RequestParam("province")String province//省/市
							   );

	/**
	 * 供应商修改
	 * @param supplierId
	 * @param supplierName
	 * @param supplierAccount
	 * @param supplyCompanyId
	 * @param supplyCompanyName
	 * @param contracts
	 * @param contractNumber
	 * @param country
	 * @param province
	 * @return
	 */
	@PostMapping("/account/supplier/update")
	public  Object supplierUpdate(@RequestParam("supplierId")Integer supplierId,//供应商ID
								  @RequestParam("supplierName")String supplierName,//供应商名称
								  @RequestParam("supplierAccount")String supplierAccount,//供应商账号
								  @RequestParam("supplyCompanyId")Integer supplyCompanyId,//供应链公司ID
								  @RequestParam("supplyCompanyName")String supplyCompanyName,//供应链公司名称
								  @RequestParam("contracts")String contracts,//联系人
								  @RequestParam("contractNumber")String contractNumber,//联系人电话
								  @RequestParam("country")String country,//国家/地区
								  @RequestParam("province")String province//省/市
								);


	/**
	 * 结算信息修改
	 * @param supplierId
	 * @param settlementCycle
	 * @return
	 */
	@PostMapping("/settlement/info/modify")
	public Object settlementModify(@RequestParam("supplierName") String supplierName,//供应商名称
								   @RequestParam("supplierId")Integer supplierId,//供应商ID
								   @RequestParam("settlementCycle")String settlementCycle,
								   @RequestParam("stageList") String stageList);//结算周期
	
	/**
	 * 	结算周期注册
	 * @param supplierId
	 * @param supplierName
	 * @param settlementCycle
	 * @return
	 */
	@PostMapping("/settlement/info/regist")
	public Object settlementRegist(@RequestParam("supplierId")Integer supplierId,
								   @RequestParam("supplierName")String supplierName,
								   @RequestParam("settlementCycle")String settlementCycle,
								   @RequestParam("stageList") String stageList);

	/**
	 * 	添加供应链公司
	 * @param supplyCompanyId
	 * @param supplyCompanyName
	 * @param contracts
	 * @param contractNumber
	 * @param country
	 * @param province
	 * @return
	 */
	@PostMapping("/supply/account/add")
	public Object addAccount(
			@RequestParam("supplyCompanyId")Integer supplyCompanyId, //供应链公司ID
			@RequestParam("supplyCompanyName")String supplyCompanyName,//供应链公司名称
			@RequestParam("contracts")String contracts, //联系人
			@RequestParam("contractNumber")String contractNumber,//联系人电话
			@RequestParam("country")String country, //国家/地区
			@RequestParam("province")String province);//省/市

	/**
	 * 	停用供应链公司
	 * @param supplyCompanyId
	 * @param newSupplyCompanyId
	 * @return
	 */
	@PostMapping("/supply/account/delete")
	public Object deleteAccount(
			@RequestParam("supplyCompanyId")Integer supplyCompanyId, //停用的供应链公司ID
			@RequestParam("newSupplyCompanyId")Integer newSupplyCompanyId);//替换的供应链公司ID

	/**
	 * 	卖家重新绑定供应链公司
	 * @param sellerId
	 * @param supplyCompanyId
	 * @return
	 */
	@PostMapping("/supply/account/sellerRebind")
	public Object sellerRebind(
			@RequestParam("sellerId")Integer sellerId, //卖家id
			@RequestParam("supplyCompanyId")Integer supplyCompanyId);//供应链公司ID

	/**
	 * 	供应商重新绑定供应链公司
	 * @param supplyCompanyId
	 * @param supplyCompanyId
	 * @return
	 */
	@PostMapping("/supply/account/supplierRebind")
	public Object supplierRebind(
			@RequestParam("supplierId")Integer supplierId, //供应商id
			@RequestParam("supplyCompanyId")Integer supplyCompanyId);//供应链公司ID

	/**
	 * 	供应链公司编辑
	 * @param supplyCompanyId
	 * @param supplyCompanyName
	 * @param contracts
	 * @param contractNumber
	 * @param country
	 * @param province
	 * @return
	 */
	@PostMapping("/supply/account/update")
	public Object updateAccountInfo(
			@RequestParam("supplyCompanyId")Integer supplyCompanyId, //供应链公司ID
			@RequestParam("supplyCompanyName")String supplyCompanyName,//供应链公司名称
			@RequestParam("contracts")String contracts, //联系人
			@RequestParam("contractNumber")String contractNumber,//联系人电话
			@RequestParam("country")String country, //国家/地区
			@RequestParam("province")String province);//省/市


	@PostMapping("account/getSellerCreditInfo")
	Object getSellerCreditInfo(@RequestParam("sellerId")Integer sellerId);

	@PostMapping("/sellerCredit/pageQuery")
	Object pageQuery(@RequestParam("pageNum") Integer pageNum,@RequestParam("pageSize") Integer pageSize,
					 @RequestParam("applyStatus") String applyStatus,@RequestParam("shopType") String shopType,@RequestParam("sellerId") Integer sellerId);


	/**
	 * 断路降级
	 * */
	@Service
	class UserFinanceInitializationImpl implements UserFinanceInitialization{

		private Logger logger = LoggerFactory.getLogger(UserFinanceInitializationImpl.class);

		@Autowired
		private TestSender testSender;

		 //"事件类型  1:卖家初始化  2：卖家修改  3：供应商初始化 4：供应商修改  5：结算注册 6：结算修改")
		@Override
		public Object sellerInit(Integer sellerId, String sellerName, Integer supplyCompanyId, String supplyCompanyName, String contracts, String contractNumber, String country, String province) {
			try {
				UserFananceVo sellerInitVo = new UserFananceVo(sellerId, sellerName, supplyCompanyId, supplyCompanyName, contracts, contractNumber, country, province);
				sellerInitVo.setDataType(1);
				//Integer result = hystrixFinanceMapper.sellerInit(sellerInitVo);//失败记录
				testSender.financeSellerUpdateSend(sellerInitVo);
			} catch (Exception e) {
				logger.error("卖家财务初始化失败记录异常",e.getMessage());
			}
			logger.error("卖家财务初始化失败");
			return null;
		}

		@Override
		public Object sellerUpdate(Integer sellerId, String sellerName, Integer supplyCompanyId, String supplyCompanyName, String contracts, String contractNumber, String country, String province) {
			try {
				UserFananceVo sellerInitVo = new UserFananceVo(sellerId, sellerName, supplyCompanyId, supplyCompanyName, contracts, contractNumber, country, province);
				sellerInitVo.setDataType(2);
//				Integer result = hystrixFinanceMapper.sellerUpdate(sellerInitVo);//失败记录
				testSender.financeSellerUpdateSend(sellerInitVo);
			} catch (Exception e) {
				logger.error("卖家财务修改失败记录异常",e.getMessage());
			}
			logger.error("卖家财务修改失败");
			return null;
		}

		@Override
		public Object supplierInit(Integer supplierId, String supplierName, String supplierAccount, Integer supplyCompanyId, String supplyCompanyName,
								   String contracts, String contractNumber, String country, String province) {
			try {
				UserFananceVo sellerInitVo = new UserFananceVo(supplierId,supplierName,supplierAccount,supplyCompanyId,supplyCompanyName,contracts,contractNumber,country,province);
				sellerInitVo.setDataType(3);
				testSender.financeSellerUpdateSend(sellerInitVo);
				//Integer result = hystrixFinanceMapper.supplierInit(supplierInitVo);
			} catch (Exception e) {
				logger.error("供应商财务初始化记录异常",e.getMessage());
			}
			logger.error("供应商财务初始化失败");
			return null;
		}

		@Override
		public Object supplierUpdate(Integer supplierId, String supplierName, String supplierAccount, Integer supplyCompanyId, String supplyCompanyName, String contracts, String contractNumber, String country, String province) {
			try {
				UserFananceVo sellerInitVo = new UserFananceVo(supplierId,supplierName,supplierAccount,supplyCompanyId,supplyCompanyName,contracts,contractNumber,country,province);
				sellerInitVo.setDataType(4);
				testSender.financeSellerUpdateSend(sellerInitVo);
				//Integer result = hystrixFinanceMapper.supplierUpdate(supplierInitVo);
			} catch (Exception e) {
				logger.error("供应商财务修改记录异常",e.getMessage());
			}
			logger.error("供应商财务修改失败！！！");
			return null;
		}

		/**
		 * 结算信息修改
		 *
		 * @param supplierName
		 * @param supplierId
		 * @param settlementCycle
		 * @param stageList
		 * @return
		 */
		@Override
		public Object settlementModify(String supplierName, Integer supplierId, String settlementCycle, String stageList) {
			return fallback();
		}

		/**
		 * 结算周期注册
		 *
		 * @param supplierId
		 * @param supplierName
		 * @param settlementCycle
		 * @param stageList
		 * @return
		 */
		@Override
		public Object settlementRegist(Integer supplierId, String supplierName, String settlementCycle, String stageList) {
			return fallback();
		}

		@Override
		public Object addAccount(Integer supplyCompanyId, String supplyCompanyName, String contracts,
				String contractNumber, String country, String province) {
			logger.error("财务添加供应链公司失败");
			return false;
		}

		@Override
		public Object deleteAccount(Integer supplyCompanyId, Integer newSupplyCompanyId) {
			logger.error("财务停用供应链公司失败");
			return false;
		}

		@Override
		public Object sellerRebind(Integer sellerId, Integer supplyCompanyId) {
			logger.error("财务卖家重绑供应链公司失败");
			return false;
		}

		@Override
		public Object supplierRebind(Integer supplierId, Integer supplyCompanyId) {
			logger.error("财务供应商绑定供应链公司失败");
			return false;
		}

		@Override
		public Object updateAccountInfo(Integer supplyCompanyId, String supplyCompanyName, String contracts,
				String contractNumber, String country, String province) {
			logger.error("财务编辑供应链公司失败");
			return false;
		}


		@Override
		public Object getSellerCreditInfo(Integer sellerId) {
			return fallback();
		}

		public Object fallback() {
			return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "财务服务异常"));
		}

		@Override
		public Object pageQuery(Integer pageNum, Integer pageSize, String applyStatus, String shopType, Integer sellerId) {
			return fallback();
		}
	}



	
}



