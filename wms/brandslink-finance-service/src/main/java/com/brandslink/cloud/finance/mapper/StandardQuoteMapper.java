package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.entity.StandardQuote;
import com.brandslink.cloud.finance.pojo.vo.StandardQuoteVO;

import java.util.List;

public interface StandardQuoteMapper extends BaseMapper<StandardQuote> {

    List<StandardQuote> list(StandardQuoteVO standardQuoteQuery);

    List<Integer> getEffectiveId();

}