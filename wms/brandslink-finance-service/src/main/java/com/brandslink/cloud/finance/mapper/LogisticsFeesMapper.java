package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.entity.LogisticsFees;
import com.brandslink.cloud.finance.pojo.vo.LogisticsFeesVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogisticsFeesMapper extends BaseMapper<LogisticsFees> {
    List<LogisticsFees> list(LogisticsFeesVO logisticsFeesQuery);

    void updateStatus(@Param("ids") int[] ids, @Param("status")int status);
}