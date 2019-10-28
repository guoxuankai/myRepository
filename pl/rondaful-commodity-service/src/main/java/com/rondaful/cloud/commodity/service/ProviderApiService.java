package com.rondaful.cloud.commodity.service;

import java.util.Map;

import com.rondaful.cloud.commodity.vo.ApiCategoryResponseVo;
import com.rondaful.cloud.commodity.vo.ApiSpuResponse;
import com.rondaful.cloud.common.entity.Page;

public interface ProviderApiService {

	Page<ApiSpuResponse> getSpuList(Map<String,Object> map);
	
	Page<ApiCategoryResponseVo> getCategoryList();
	
}
