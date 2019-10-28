package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.download.DownloadDTO;
import com.rondaful.cloud.user.model.dto.download.QueryDownloadDTO;

/**
 * @Author: xqq
 * @Date: 2019/9/4
 * @Description:
 */
public interface IDownloadService {

    /**
     * 新建任务
     * @param dto
     * @return
     */
    Integer insert(DownloadDTO dto);

    /**
     * 修改状态
     * @param id
     * @param url
     * @param status
     * @return
     */
    Integer updateStatus(Integer id,String url,Integer status);

    /**
     * 分页查询
     * @param dto
     * @return
     */
    PageDTO<DownloadDTO> getsPage(QueryDownloadDTO dto);

}
