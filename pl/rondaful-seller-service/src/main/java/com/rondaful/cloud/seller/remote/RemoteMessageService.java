package com.rondaful.cloud.seller.remote;


import com.rondaful.cloud.seller.entity.MessageSearchTerm;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 远程调用后台服务
 * */
@FeignClient(name = "rondaful-cms-service", fallback = RemoteMessageService.RemoteTestServiceImpl.class)
public interface RemoteMessageService {

    @GetMapping("/message/queryMessageList")
    public String queryMessageList(@RequestParam(value="currentPage",defaultValue = "1") String currentPage, 
							@RequestParam(value="pageSize",defaultValue = "10")String pageSize,
							@RequestParam(value="languageSys",defaultValue = "0")String languageSys,
							@RequestParam(value="startDate",defaultValue = "")String startDate,
							@RequestParam(value="endDate",defaultValue = "")String endDate,
							@RequestParam(value="title",defaultValue = "")String title,
							@RequestParam(value="type",defaultValue = "")String type,
							@RequestParam(value="status",defaultValue = "")String status,
							@RequestParam(value="userName",defaultValue = "")String userName,
							@RequestParam(value="belongSys",defaultValue = "")String belongSys,
							@RequestParam(value="querySys",defaultValue = "1")String querySys);

    @GetMapping("/message/queryNoticeDetail")
    public String queryMessageDetail(@RequestParam(value="id")String id,@RequestParam(value="receiveSys")String receiveSys,
    								 @RequestParam(value="languageSys")String languageSys);

	@PostMapping("/message/updateMessageStatusById")
	public String updateMessageStatusById(MessageSearchTerm param);


    @GetMapping("/message/queryMessageCount")
    public String queryMessageCount(@RequestParam("userName")String userName,@RequestParam("belongSys")String belongSys);

    /**
     * 断路降级
     * */
    @Service
    class RemoteTestServiceImpl implements RemoteMessageService {

		@Override
		public String queryMessageList(@RequestParam(value="currentPage",defaultValue = "1") String currentPage, 
				@RequestParam(value="pageSize",defaultValue = "10")String pageSize,
				@RequestParam(value="languageSys",defaultValue = "0")String languageSys,
				@RequestParam(value="startDate",defaultValue = "")String startDate,
				@RequestParam(value="endDate",defaultValue = "")String endDate,
				@RequestParam(value="title",defaultValue = "")String title,
				@RequestParam(value="type",defaultValue = "")String type,
				@RequestParam(value="status",defaultValue = "")String status,
				@RequestParam(value="userId",defaultValue = "")String userId,
				@RequestParam(value="belongSys",defaultValue = "")String belongSys,String querySys) {
			return null;
		}

		@Override
		public String queryMessageDetail(String id,String receiveSys,String languageSys) {
			return null;
		}

		@Override
		public String updateMessageStatusById(MessageSearchTerm param) {
			return null;
		}


		@Override
		public String queryMessageCount(String userName, String belongSys) {
			return null;
		}

     
    }
}





