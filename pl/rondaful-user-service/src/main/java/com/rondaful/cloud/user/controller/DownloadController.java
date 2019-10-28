package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.download.QueryDownloadDTO;
import com.rondaful.cloud.user.model.request.download.QueryDownPageReq;
import com.rondaful.cloud.user.service.IDownloadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xqq
 * @Date: 2019/9/5
 * @Description:
 */
@Api(description = "下载中心相关接口")
@RestController
@RequestMapping("download/")
public class DownloadController extends BaseController {

    @Autowired
    private IDownloadService downloadService;

    @ApiOperation(value = "分页查询下载列表")
    @GetMapping("getsPage")
    public PageDTO getsPage(QueryDownPageReq req){
        UserDTO userDTO=super.userToken.getUserDTO();
        QueryDownloadDTO dto=new QueryDownloadDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setUserId(userDTO.getUserId());
        dto.setPlatformType(userDTO.getPlatformType());
        dto.setLanguageType(super.request.getHeader("i18n"));
        return this.downloadService.getsPage(dto);
    }
}
