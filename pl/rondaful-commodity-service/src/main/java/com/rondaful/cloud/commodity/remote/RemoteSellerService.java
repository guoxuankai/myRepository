package com.rondaful.cloud.commodity.remote;


import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.vo.EmpowerRequestVo;
import com.rondaful.cloud.common.entity.Result;

import net.sf.json.JSONObject;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;



@FeignClient(name = "rondaful-seller-service", fallback = RemoteSellerService.RemoteSellerServiceImpl.class)
public interface RemoteSellerService {


    @PostMapping("/empower/getEmpowerSearchVO")
    Object getEmpowerSearchVO(@RequestBody EmpowerRequestVo vo);


    /**
     	* 断路降级
     */
    @Service
    class RemoteSellerServiceImpl implements RemoteSellerService {

        public String fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "卖家服务异常")).toString();
        }

		@Override
		public Object getEmpowerSearchVO(EmpowerRequestVo vo) {
			return fallback();
		}

    }
}





