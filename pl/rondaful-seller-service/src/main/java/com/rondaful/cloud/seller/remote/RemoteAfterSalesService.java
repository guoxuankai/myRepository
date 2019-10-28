package com.rondaful.cloud.seller.remote;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.seller.entity.AfterSalesApprovalModel;
import com.rondaful.cloud.seller.vo.AfterSalesApprovalVO;
import com.rondaful.cloud.seller.vo.OrderAfterSalesVo;
import com.rondaful.cloud.seller.vo.ReplenishmentVO;

@FeignClient(name = "rondaful-cms-service", fallback = RemoteAfterSalesService.RemoteAfterSalesServiceImpl.class)
public interface RemoteAfterSalesService {

	/**
	 * 分页查询
	 * 
	 * @param json
	 * @return
	 */
	@GetMapping("/afterSales/getOrderAfterSalesPage")
	public String findAfterSalesPage(@RequestParam("json") JSONObject json);

	/**
	 * 根据ID查询单条记录
	 * 
	 * @param numberingId
	 * @return
	 */
	@GetMapping("/afterSales/getOrderAfterSales/{numberingId}")
	public String findAfterSalesOrderByNumberingId(@PathVariable("numberingId") String numberingId);

	/**
	 * 新增售后退款订单
	 * 
	 * @param oasv
	 * @return
	 */
	@PostMapping("/afterSales/refund/addRefund")
	public String addRefund(@RequestBody OrderAfterSalesVo oasv);

	/**
	 * 新增售后退货订单
	 *
	 * @param oasv
	 * @return
	 */
	@PostMapping("/afterSales/return/addReturn")
	public String addReturn(@RequestBody OrderAfterSalesVo oasv);

	/**
	 * 修改售后退款
	 * 
	 * @param oasv
	 * @return
	 */
	@PutMapping("/afterSales/refund/updateRefund/{numberingId}")
	public String updateRefund(@RequestBody OrderAfterSalesVo oasv, @PathVariable("numberingId") String numberingId);

	/**
	 * 修改售后退货订单
	 *
	 * @param oasv
	 * @return
	 */
	@PutMapping("/afterSales/return/updateReturn/numberingId}")
	public String updateReturn(@RequestBody OrderAfterSalesVo oasv, @PathVariable("numberingId") String numberingId);

	/**
	 * 修改售后退款订单状态
	 * 
	 * @param asam
	 * @param status
	 * @param numberingId
	 * @return
	 */
	@PutMapping("/afterSales/refund/updateRefundStatus/{numberingId}/{status}")
	public String updateRefundStatus(@RequestBody AfterSalesApprovalModel asam, @PathVariable("status") int status, @PathVariable("numberingId") String numberingId);

	/**
	 * 修改售后退货订单状态
	 *
	 * @param asam
	 * @param status
	 * @param numberingId
	 * @return
	 */
	@PutMapping("/afterSales/return/updateReturnStatus/{numberingId}/{status}")
	public String updateReturnStatus(@RequestBody AfterSalesApprovalModel asam, @PathVariable("status") int status, @PathVariable("numberingId") String numberingId);

	@PostMapping("/replenishment/add")
	public Object insert(@RequestBody ReplenishmentVO oasv) throws Exception;

	@PostMapping("/replenishment/update/{numberingId}")
	public Object update(@RequestBody ReplenishmentVO replenishment, @PathVariable("numberingId") String numberingId) throws Exception;

	@GetMapping("/replenishment/getReplenishment/{numberingId}")
	public Object findReplenishmentByNumberingId(@PathVariable("numberingId") String numberId) throws Exception;

	@PostMapping("/replenishment/updateReplenishmentStatus/{numberingId}/{status}")
	public Object updateReplenishmentStatus(@RequestBody AfterSalesApprovalVO asam, @PathVariable("numberingId") String numberingId, @PathVariable("status") Integer status) throws Exception;

	@GetMapping("/replenishment/updateStatus/{status}/{numberingId}")
	public Object updateStatus(@PathVariable("numberingId") String numberingId, @PathVariable("status") String status) throws Exception;

	@PostMapping("/empowerRent/updateByRent")
	public String updateEmpowerRent(@RequestParam("sellerAccount")String sellerAccount,
									@RequestParam("company")String company, @RequestParam("loginAccount")String loginAccount,@RequestParam("platform")String platform);

	@Service
	class RemoteAfterSalesServiceImpl implements RemoteAfterSalesService {

		@Override
		public String findAfterSalesPage(JSONObject json) {
			return null;
		}

		@Override
		public String findAfterSalesOrderByNumberingId(String numberingId) {
			return null;
		}

		@Override
		public String addRefund(OrderAfterSalesVo oasv) {
			return null;
		}

		@Override
		public String addReturn(OrderAfterSalesVo oasv) {
			return null;
		}

		@Override
		public String updateRefund(OrderAfterSalesVo oasv, String numberingId) {
			return null;
		}

		@Override
		public String updateReturn(OrderAfterSalesVo oasv, String numberingId) {
			return null;
		}

		@Override
		public String updateRefundStatus(AfterSalesApprovalModel asam, int status, String numberingId) {
			return null;
		}

		@Override
		public String updateReturnStatus(AfterSalesApprovalModel asam, int status, String numberingId) {
			return null;
		}


		@Override
		public Object insert(ReplenishmentVO oasv) throws Exception {
			return null;
		}

		@Override
		public Object update(ReplenishmentVO replenishment, String numberingId) throws Exception {
			return null;
		}

		@Override
		public Object findReplenishmentByNumberingId(String numberId) throws Exception {
			return null;
		}

		@Override
		public Object updateReplenishmentStatus(AfterSalesApprovalVO asam, String numberingId, Integer status) throws Exception {
			return null;
		}

		@Override
		public Object updateStatus(String numberingId, String status) throws Exception {
			return null;
		}

		@Override
		public String updateEmpowerRent(String sellerAccount, String company, String loginAccount, String platform) {
			return null;
		}
	}

}
