package com.rondaful.cloud.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.user.entity.Download;
import com.rondaful.cloud.user.enums.UserStatusEnum;
import com.rondaful.cloud.user.mapper.DownloadMapper;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.download.DownloadDTO;
import com.rondaful.cloud.user.model.dto.download.QueryDownloadDTO;
import com.rondaful.cloud.user.service.IDownloadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/9/4
 * @Description:
 */
@Service("downloadServiceImpl")
public class DownloadServiceImpl implements IDownloadService {

    @Autowired
    private DownloadMapper mapper;

    /**
     * 新建任务
     *
     * @param dto
     * @return
     */
    @Override
    public Integer insert(DownloadDTO dto) {
        Download download=new Download();
        BeanUtils.copyProperties(dto,download);
        download.setStartTime(new Date());
        download.setStatus(UserStatusEnum.NO_ACTIVATE.getStatus());
        download.setVersion(1);
        this.mapper.insert(download);
        return download.getId();
    }

    /**
     * 修改状态
     *
     * @param id
     * @param url
     * @param status
     * @return
     */
    @Override
    public Integer updateStatus(Integer id, String url, Integer status) {
        Download download=new Download();
        download.setId(id);
        download.setUrl(url);
        download.setStatus(status);
        download.setEndTime(new Date());
        return this.mapper.updateByPrimaryKeySelective(download);
    }

    /**
     * 分页查询
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<DownloadDTO> getsPage(QueryDownloadDTO dto) {
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<Download> list= this.mapper.getsPage(dto.getStatus(),dto.getUserId(),dto.getStartTime(),dto.getEndTime(),dto.getPlatformType());
        PageInfo<Download> pageInfo=new PageInfo<>(list);
        PageDTO<DownloadDTO> result=new PageDTO<>(pageInfo.getTotal(),dto.getCurrentPage().longValue());
        List<DownloadDTO> data=new ArrayList<>();
        pageInfo.getList().forEach( page -> {
            DownloadDTO downloadDTO=new  DownloadDTO();
            BeanUtils.copyProperties(page,downloadDTO);
            downloadDTO.setJob(StringUtils.isEmpty(dto.getLanguageType())? page.getJob():Utils.translation(page.getJob()));
            data.add(downloadDTO);
        });
        result.setList(data);
        return result;
    }
}
