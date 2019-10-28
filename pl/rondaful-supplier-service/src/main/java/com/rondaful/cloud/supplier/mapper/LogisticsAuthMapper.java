package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.supplier.entity.Logistics.LogisticsAuth;

public interface LogisticsAuthMapper {

	void insertLogisticsAuth(LogisticsAuth logisticsAuth);

	LogisticsAuth queryLogisticsAuth(LogisticsAuth logisticsAuth);
}