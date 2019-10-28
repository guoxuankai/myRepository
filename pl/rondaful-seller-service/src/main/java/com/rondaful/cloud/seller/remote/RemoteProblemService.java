package com.rondaful.cloud.seller.remote;

import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.seller.entity.Problem;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 远程调用服务
 */
@FeignClient(name = "rondaful-cms-service", fallback = RemoteProblemService.RemoteProblemServiceImpl.class)
public interface RemoteProblemService {

    
	/**
	 * 问题模糊查询
	 * @return
	 */
	@GetMapping("/problem/queryProblem")
	String queryProblem(@RequestParam(value="currentPage",defaultValue = "1")String currentPage,
							 @RequestParam(value="pageSize",defaultValue = "10")String pageSize,
							 @RequestParam(value="source",defaultValue = "")String source,
							 @RequestParam(value="type",defaultValue = "")String type,
							 @RequestParam(value="startDate",defaultValue = "")String startDate,
							 @RequestParam(value="endDate",defaultValue = "")String endDate,
							 @RequestParam(value="content",defaultValue = "")String content,
						     @RequestParam(value="title",defaultValue = "")String title,
							 @RequestParam(value="dateType",defaultValue = "")String dateType,
							 @RequestParam(value="belongSys",defaultValue = "")String belongSys);
	
	
	/**
	 * 问题详情查询
	 * @param id
	 * @return
	 */
	@GetMapping("/problem/queryProblemDetail")
	public String queryProblemDetail(@RequestParam("id") Long id);
	

    /**
     * 断路降级
     */
    @Service
    class RemoteProblemServiceImpl implements RemoteProblemService {

        public Object fallback() {
            return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "订单服务异常"));
        }

		public String queryProblem(String currentPage, String pageSize, String source, String type, String startDate,
				String endDate, String content,String title, String dateType,String belongSys) {
			return null;
		}

		public String queryProblemDetail(Long id) {
			return null;
		}

        
    }

}
