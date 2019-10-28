package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.model.vo.sysOrderList.SysOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.jsoup.Connection;

/**
 * @Author: luozheng
 * @BelongsPackage:com.rondaful.cloud.order.mapper
 * @Date: 2019-07-22 17:07:30
 * @FileName:
 * @Description:
 */
@Mapper
public interface SysOrderListMapper extends BaseMapper<SysOrderVo> {
}
