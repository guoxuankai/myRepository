package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.entity.ImportFailure;
import org.apache.ibatis.annotations.Param;

public interface ImportFailureMapper extends BaseMapper<ImportFailure> {

    void deleteByIds(@Param("ids") int[] ids);
}