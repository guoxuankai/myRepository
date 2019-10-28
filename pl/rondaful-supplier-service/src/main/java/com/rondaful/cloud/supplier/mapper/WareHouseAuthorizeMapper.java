package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.dto.AuthorizeDTO;
import com.rondaful.cloud.supplier.entity.WareHouseAuthorize;
import feign.Param;

import java.util.List;

public interface WareHouseAuthorizeMapper extends BaseMapper<WareHouseAuthorize>{
	
	List<Integer> getServiceProviderId(@Param("companyCode") List<String> companyCode);
	
	AuthorizeDTO selectAuthorizeBywarehouseCode(String warehouseCode);

	List<AuthorizeDTO> getAuthorizeByCompanyCodeList(List<String> list);
	
	List<AuthorizeDTO>  getAuthorizeList();
	
	AuthorizeDTO getAuthorizeByCompanyCode(String CompanyCode);
	
}