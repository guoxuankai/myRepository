package com.rondaful.cloud.supplier.remote;


import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.reomte.user.AppDTO;
import com.rondaful.cloud.supplier.model.dto.reomte.user.FeignUserDTO;
import com.rondaful.cloud.supplier.model.dto.reomte.user.LogisticsInfo;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 远程调用用户服务
 * */
@FeignClient(name = "rondaful-user-service", fallback = RemoteUserService.RemoteUserServiceImpl.class)
public interface RemoteUserService {

    @GetMapping("/provider/getUserByToken")
    Object getUserByToken(@RequestParam("token") String token);
    
    @PostMapping("/provider/getChildAccount")
	String getChildAccount(@RequestParam("userId")Integer userId,@RequestParam("userName")String userName,@RequestParam("platformType")String platformType);

    @GetMapping("/provider/getSupplyById")
    Object getSupplyById(@RequestParam("supplyId") Integer supplyId);

    @GetMapping("/provider/getSupplierList")
    Object getSupplierList(@RequestParam("userIds") List<Integer> userIds,@RequestParam("platformType") String platformType);

    @GetMapping("/provider/getSupplyChinByUserIdOrUsername")
    @Deprecated
    Object getUser(@RequestParam("userid") Integer userid,@RequestParam("username") String username,@RequestParam("platformType") Integer platformType);

    @GetMapping("/area/code/getCountry")
    String getCountry(@RequestParam("level")Integer level);

    @GetMapping("/provider/getNameByCode")
    FeignResult<String> getNameByCode(@RequestParam("code") String code,@RequestParam("languageType") String languageType,@RequestParam("level") Integer level);

    @GetMapping("/provider/getAppAll")
    FeignResult<List<AppDTO>> getAppAll();

    @PostMapping("/provider/insertDown")
    FeignResult<Integer> insertDown(@RequestParam("job") String job,@RequestParam("userId") Integer userId,@RequestParam("topUserId") Integer topUserId,@RequestParam("platformType") Integer platformType);

    @PostMapping("/provider/updateDownStatus")
    FeignResult updateDownStatus(@RequestParam("id") Integer id,@RequestParam("url") String url,@RequestParam("status") Integer status);

    @GetMapping("/area/code/getArea")
    FeignResult getArea(@RequestParam("countryName") String countryName,@RequestParam("countryCode") String countryCode);

    @GetMapping("/provider/getSupplyChinByUserIdOrUsername")
    FeignResult<FeignUserDTO> getNewUser(@RequestParam("userid") Integer userid, @RequestParam("username") String username, @RequestParam("platformType") Integer platformType);

    @GetMapping("/provider/getLogSupplyId")
    FeignResult<LogisticsInfo> getLogSupplyId(@RequestParam("id") Integer id);

    /**
             * 断路降级
     * */
    @Service
    class RemoteUserServiceImpl implements RemoteUserService {

        public FeignResult error(){
            return new FeignResult(false,ResponseCodeEnum.RETURN_CODE_100500.getCode(), "用户服务异常");
        }


        @Override
        public Object getUserByToken(String token) {
            return fallback();
        }
        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "用户服务异常"));
        }

		@Override
		public String getChildAccount(Integer userId, String userName, String platformType) {
			// TODO Auto-generated method stub
			return null;
		}

        @Override
        public Object getSupplyById(Integer supplyId) {
            return fallback();
        }

        @Override
        public Object getSupplierList(List<Integer> userIds, String platformType) {
            return fallback();
        }

        @Override
        public Object getUser(Integer userid, String username, Integer platformType) {
            return fallback();
        }

        @Override
        public FeignResult getNameByCode(String code, String languageType,Integer level) {
            return error();
        }

        @Override
        public String getCountry(Integer level) { return null; }

        @Override
        public FeignResult getAppAll() {
            return error();
        }

        @Override
        public FeignResult insertDown(String job, Integer userId, Integer topUserId, Integer platformType) {
            return error();
        }

        @Override
        public FeignResult updateDownStatus(Integer id, String url, Integer status) {
            return error();
        }

        @Override
        public FeignResult getArea(String countryName, String countryCode) {
            return error();
        }

        @Override
        public FeignResult getNewUser(Integer userid, String username, Integer platformType) {
            return error();
        }

        @Override
        public FeignResult<LogisticsInfo> getLogSupplyId(Integer id) {
            return error();
        }
    }
}