package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.entity.StandardQuoteDetail;

import java.util.List;

public interface StandardQuoteDetailMapper extends BaseMapper<StandardQuoteDetail> {

    List<StandardQuoteDetail> getByQuoteId(Integer quoteId);

}