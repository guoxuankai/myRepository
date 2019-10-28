package com.rondaful.cloud.user.remote;

import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: xqq
 * @Date: 2019/5/8
 * @Description:
 */
@FeignClient(name = "rondaful-supplier-service", fallback = RemoteSupplierService.RemoteSupplierServiceImpl.class)
public interface RemoteSupplierService {

    @PostMapping("/provider/basic/getsNameByCode")
    Object getBindName(@RequestParam("codes") String codes);

    @GetMapping("/provider/getBindAccount")
    Object getBindService(@RequestParam("supplyId") Integer supplyId);

    @Service
    class RemoteSupplierServiceImpl implements RemoteSupplierService{

        @Override
        public Object getBindName(String codes) {
            return fallback();
        }

        @Override
        public Object getBindService(Integer supplyId) {
            return fallback();
        }

        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "**服务异常"));
        }
    }


}
