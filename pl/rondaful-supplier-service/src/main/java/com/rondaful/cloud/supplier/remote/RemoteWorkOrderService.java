package com.rondaful.cloud.supplier.remote;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.supplier.entity.WorkOrder;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;

import net.sf.json.JSONObject;


/**
 * 远程调用服务
 */
@FeignClient(name = "rondaful-cms-service", fallback = RemoteWorkOrderService.RemoteWorkOrderServiceImpl.class)
public interface RemoteWorkOrderService {

	@PostMapping("/workOrder/insertObject")
	public String insertObject (@RequestBody WorkOrder workOrder);
	
	@GetMapping("/workOrder/searchObjectById")
	public String searchObjectById(@RequestParam("id") Integer id);
	
	@PostMapping("/workOrder/findAll")
	public String findAll(@RequestBody WorkOrder workOrder,
			               @RequestParam("page")String page, 
			               @RequestParam("row")String row);
	
	

    /**
     * 断路降级
     */
    @Service
    class RemoteWorkOrderServiceImpl implements RemoteWorkOrderService {

        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "问题与反馈服务异常"));
        }

		public String insertObject(WorkOrder workOrder) {
			return null;
		}

		public String searchObjectById(Integer id) {
			return null;
		}

		
		public String findAll(WorkOrder workOrder, String page, String row) {
			return null;
		}

		
        
    }

}
