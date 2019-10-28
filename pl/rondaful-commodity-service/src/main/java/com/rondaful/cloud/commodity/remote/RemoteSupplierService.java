package com.rondaful.cloud.commodity.remote;


import com.rondaful.cloud.commodity.dto.FreightTrialDto;
import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;

import net.sf.json.JSONObject;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
   * 远程调用供应商服务
 */
@FeignClient(name = "rondaful-supplier-service", fallback = RemoteSupplierService.RemoteSupplierServiceImpl.class)
public interface  RemoteSupplierService {

    @PostMapping("/provider/getBySku")
    public Object getBySku(@RequestBody List<String> skus);
    
    @PostMapping("/provider/getByFirmId")
    public Object getByFirmId(@RequestParam("firmId")Integer firmId);
    
    @GetMapping("/provider/basic/getAuth")
    public Object getAuth(@RequestParam("type")Integer type);
    
    @GetMapping("/provider/basic/getFirmByName")
    public Object getFirmByName(@RequestParam("name")String name);
    
    @GetMapping("/provider/getWarehouseById")
    public Object getWarehouseById(@RequestParam("warehouseId")Integer warehouseId);
    
    @PostMapping("/freight/getFreight")
    public Object getFreight(@RequestBody FreightTrialDto param);
    
    @PostMapping("/provider/bindSeller")
    public Object bindSeller(@RequestBody List<String> skus);


    /**
     * 断路降级
     */
    @Service
    class RemoteSupplierServiceImpl implements RemoteSupplierService {

        @Override
        public Object getBySku(List<String> skus) {
            return fallback();
        }

        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "供应商服务异常"));
        }

		@Override
		public Object getByFirmId(Integer firmId) {
			return fallback();
		}

		@Override
		public Object getAuth(Integer type) {
			return fallback();
		}

		@Override
		public Object getFirmByName(String type) {
			return fallback();
		}

		@Override
		public Object getWarehouseById(Integer warehouseId) {
			return fallback();
		}

		@Override
		public Object getFreight(FreightTrialDto param) {
			return fallback();
		}

		@Override
		public Object bindSeller(List<String> skus) {
			return fallback();
		}
    }
}





