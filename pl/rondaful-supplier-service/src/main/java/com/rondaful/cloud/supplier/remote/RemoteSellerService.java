package com.rondaful.cloud.supplier.remote;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rondaful-seller-service", fallback = RemoteSellerService.RemoteSellerServiceImpl.class)
public interface RemoteSellerService {


	@PostMapping("/empower/getEmpowerSearchVO")
	JSONObject getEmpowerSearchVO(@RequestBody JSONObject jsonParam);


	@Service
	class RemoteSellerServiceImpl implements RemoteSellerService {

		@Override
		public JSONObject getEmpowerSearchVO( JSONObject jsonParam) {
			return null;
		}
	}



}
