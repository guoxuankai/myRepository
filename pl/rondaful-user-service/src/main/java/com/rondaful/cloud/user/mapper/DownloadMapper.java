package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.Download;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface DownloadMapper extends BaseMapper<Download> {

    /**
     * 后台分页
     * @param status
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    List<Download> getsPage(@Param("status") Integer status,@Param("userId") Integer userId,@Param("startTime") Date startTime,
                            @Param("endTime") Date endTime,@Param("platformType") Integer platformType);
}