package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishModel;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.rabbitmq.AliexpressSender;
import com.rondaful.cloud.seller.service.IAliexpressBaseService;
import com.rondaful.cloud.seller.service.IAliexpressCategoryService;
import com.rondaful.cloud.seller.service.IAliexpressPublishListingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基础数据controller
 * @author chenhan
 *
 */
@Api(description = "AliexpressApi初始化数据")
@RestController
@RequestMapping("/aliexpress/api")
public class AliexpressApiController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(AliexpressApiController.class);
	private final static  String apiType="1";
	@Autowired
	private IAliexpressCategoryService aliexpressCategoryService;
	@Autowired
	private IAliexpressBaseService aliexpressBaseService;
	@Autowired
	private IAliexpressPublishListingService aliexpressPublishListingService;
	@Autowired
	private AliexpressConfig config;
	@Autowired
	private AliexpressSender aliexpressSender;
	@AspectContrLog(descrption = "获取速卖通级联分类基础信息",actionType = SysLogActionType.QUERY)
	@PostMapping("/getCategoryValue")
	public String getAliexpressCategoryByList(Long categoryId){
		this.getAliexpressCategoryLists(categoryId);
		return "true";
	}
	private void getAliexpressCategoryLists(Long categoryId){
		String url = config.getAliexpressUrl()+"/api/aliexpress/getCategory";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("apiType",AliexpressApiController.apiType);
		paramsMap.put("categoryId",categoryId+"");
		String body=HttpUtil.post(url,paramsMap);
		System.out.println(body);
		aliexpressCategoryService.insertAliexpressCategory(body,categoryId);

		List<AliexpressCategory> list = aliexpressBaseService.getAliexpressCategoryByList(categoryId);
		for (AliexpressCategory category:list){
			if(!category.getIsleaf()) {
				this.getAliexpressCategoryLists(category.getCategoryId());
			}
		}
	}

	@AspectContrLog(descrption = "获取速卖通级联分类是否必要填写尺寸",actionType = SysLogActionType.QUERY)
	@PostMapping("/sizemodelsrequiredforpostcat")
	public String sizemodelsrequiredforpostcat(){
		List<AliexpressCategory> list = aliexpressBaseService.getAliexpressCategoryByList(null);
		String url = config.getAliexpressUrl()+"/api/aliexpress/sizemodelsrequiredforpostcat";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("apiType",AliexpressApiController.apiType);
		for (AliexpressCategory category:list){
			paramsMap.put("categoryId",category.getCategoryId().toString());

			String body= HttpUtil.post(url,paramsMap);
			System.out.println(body);
            JSONObject jsonObject = JSONObject.parseObject(body);
            if(jsonObject!=null){
                JSONObject jsonObject1 = JSONObject.parseObject(jsonObject.get("aliexpress_category_redefining_sizemodelsrequiredforpostcat_response").toString());
                if(jsonObject1!=null){
                    Boolean result = Boolean.valueOf(jsonObject1.get("result").toString());
                    if(result!=null){
                        AliexpressCategory categoryUpdate=new AliexpressCategory();
                        categoryUpdate.setId(category.getId());
                        categoryUpdate.setSizeis(result);
                        aliexpressCategoryService.updateByPrimaryKeySelective(categoryUpdate);
                    }
                }
            }
		}
		return "true";
	}
	//获取属性
	@PostMapping("/getCategoryAttribute")
	public String getCategoryAttributeByList(Integer apiType){
		String url =config.getAliexpressUrl()+"/api/aliexpress/getallchildattributesresult";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("apiType",AliexpressApiController.apiType);
		List<AliexpressCategory> list = aliexpressBaseService.getAliexpressCategoryByList(null);

		List<List<AliexpressCategory>> lists = this.averageAssign(list, 10);
		for (List<AliexpressCategory> category : lists) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					//同步速卖通属性
					try {
						insertAliexpressCategory(category);
					} catch (Exception e) {
						logger.error("多线程开启异常",e);
					}
				}
			}).start();
		}

//		int i=1;
//		for (AliexpressCategory category:list){
//			paramsMap.put("categoryId",category.getCategoryId().toString());
//			//paramsMap.put("childattributesId","200000440=200392145");
//			if(category.getIsleaf()) {
//				String body= HttpUtil.post(url,paramsMap);
//				System.out.println(body);
//				aliexpressCategoryService.insertAliexpressCategoryAttribute(body, category.getCategoryId(), null);
//			}
//			System.out.println(i);
//			i++;
//		}
		return "true";
	}

	public void insertAliexpressCategory(List<AliexpressCategory> listAliexpressCategory){
		String url =config.getAliexpressUrl()+"/api/aliexpress/getallchildattributesresult";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("apiType",AliexpressApiController.apiType);
		for (AliexpressCategory category:listAliexpressCategory) {
			if(category.getIsleaf()) {
				paramsMap.put("categoryId",category.getCategoryId().toString());
				String body= HttpUtil.post(url,paramsMap);
				System.out.println(body);
				aliexpressCategoryService.insertAliexpressCategoryAttribute(body, category.getCategoryId(), null);
			}
		}
	}
	/*
	 * 将List集合平均分成几等份
	 * */
	private <T> List<List<T>> averageAssign(List<T> source, int n){
		List<List<T>> result=new ArrayList<List<T>>();
		int remaider=source.size()%n;  //(先计算出余数)
		int number=source.size()/n;  //然后是商
		int offset=0;//偏移量
		for(int i=0;i<n;i++){
			List<T> value=null;
			if(remaider>0){
				value=source.subList(i*number+offset, (i+1)*number+offset+1);
				remaider--;
				offset++;
			}else{
				value=source.subList(i*number+offset, (i+1)*number+offset);
			}
			result.add(value);
		}
		return result;
	}

//    @PostMapping("/getCategoryAttributeUser")
//    public String getCategoryAttributeUserList(Integer apiType,String categoryId){
//        String url = config.getAliexpressUrl()+"/api/aliexpress/getchildattributesresultbypostcateidandpath";
//        Map<String, String> paramsMap = Maps.newHashMap();
//        paramsMap.put("apiType",AliexpressApiController.apiType);
//		paramsMap.put("categoryId",categoryId);
//		paramsMap.put("sessionKey","50002000927s1tt7iBrarpfg0n1kjtZVtFBbES51bb2d2efkSyFHlxK3fAWD1Vo9T3P");
//
//        String body= HttpUtil.post(url,paramsMap);
//        System.out.println(body);
//		aliexpressCategoryService.insertAliexpressCategoryAttributeBrand(body,Long.valueOf(categoryId),1367L,0L);
//        return "true";
//    }
//
//
//	@PostMapping("/saveProduct")
//	public String saveProduct(Integer apiType,Long id){
//		AliexpressPublishModel model = aliexpressPublishListingService.getPublishModelById(id,2);
//		String url = config.getAliexpressUrl()+"/api/aliexpress/saveProduct";
//		Map<String, String> paramsMap = Maps.newHashMap();
//		paramsMap.put("apiType",AliexpressApiController.apiType);
//		String sessionKey="6102822904e82dd8ec02ae59deda511172d4dc89e9d44252057195533";
//		paramsMap.put("sessionKey",sessionKey);
//		paramsMap.put("jsonStr", JSONObject.toJSONString(model, SerializerFeature.WriteMapNullValue));
//		String body= HttpUtil.post(url,paramsMap);
//		System.out.println(body);
////		aliexpressSender.send(model);
//
//		return "true";
//	}
//
//
//	@PostMapping("/insertAliexpressPromiseTemplate")
//	public String insertAliexpressPromiseTemplate(Integer apiType,Long id){
//
//		String url = config.getAliexpressUrl()+"/api/aliexpress/querypromisetemplatebyid";
//		Map<String, String> paramsMap = Maps.newHashMap();
//		paramsMap.put("apiType",AliexpressApiController.apiType);
//		String sessionKey="50002000927s1tt7iBrarpfg0n1kjtZVtFBbES51bb2d2efkSyFHlxK3fAWD1Vo9T3P";
//		paramsMap.put("sessionKey",sessionKey);
//
//		String body= HttpUtil.post(url,paramsMap);
//		System.out.println(body);
//		aliexpressCategoryService.insertAliexpressPromiseTemplate(body,1367L,"MJPT@qq.com");
//		return "true";
//	}
//
//	@PostMapping("/insertAliexpressGroup")
//	public String insertAliexpressGroup(Integer apiType,Long id){
//
//		String url = config.getAliexpressUrl()+"/api/aliexpress/getUserGroups";
//		Map<String, String> paramsMap = Maps.newHashMap();
//		paramsMap.put("apiType",AliexpressApiController.apiType);
//		String sessionKey="50002000927s1tt7iBrarpfg0n1kjtZVtFBbES51bb2d2efkSyFHlxK3fAWD1Vo9T3P";
//		paramsMap.put("sessionKey",sessionKey);
//
//		String body= HttpUtil.post(url,paramsMap);
//		System.out.println(body);
//
//		aliexpressCategoryService.insertAliexpressGroup(body,1367L,"MJPT@qq.com");
//		return "true";
//	}
//	@PostMapping("/insertAliexpressFreightTemplate")
//	public String insertAliexpressFreightTemplate(Integer apiType,Long id){
//
//		String url = config.getAliexpressUrl()+"/api/aliexpress/listfreighttemplate";
//		Map<String, String> paramsMap = Maps.newHashMap();
//		paramsMap.put("apiType",AliexpressApiController.apiType);
//		String sessionKey="50002000927s1tt7iBrarpfg0n1kjtZVtFBbES51bb2d2efkSyFHlxK3fAWD1Vo9T3P";
//		paramsMap.put("sessionKey",sessionKey);
//		String body= HttpUtil.post(url,paramsMap);
//		System.out.println(body);
//		aliexpressCategoryService.insertAliexpressFreightTemplate(body,1367L,"MJPT@qq.com");
//		return "true";
//	}
//



}


