package com.rondaful.cloud.user.remote;

import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: xqq
 * @Date: 2019/5/6
 * @Description:
 */
@FeignClient(name = "rondaful-seller-service", fallback = EmpowerService.EmpowerServiceImpl.class)
public interface EmpowerService {

    @PostMapping("/Authorization/getEmpNameByIds")
    Object getsEmpName(@RequestParam("empIds") String empIds);

    @PostMapping("/empower/insertEmpowerOthers")
    Object insertObjectOthers(@RequestParam("account") String account,@RequestParam("userId") Integer userId);

    @Service
    class EmpowerServiceImpl implements EmpowerService{
        @Override
        public Object getsEmpName(String empIds) {
            return fallback();
        }

        @Override
        public Object insertObjectOthers(String account,Integer userId) {
            return fallback();
        }

        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "**服务异常"));
        }
    }
}
