package com.brandslink.cloud.user.controller;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.user.dto.request.AddOrUpdateAnnouncementListRequestDTO;
import com.brandslink.cloud.user.dto.request.GetAnnouncementListRequestDTO;
import com.brandslink.cloud.user.dto.response.AnnouncementInfoResponseDTO;
import com.brandslink.cloud.user.entity.AnnouncementInfo;
import com.brandslink.cloud.user.service.IAnnouncementInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 公告
 *
 * @ClassName AnnouncementInfoController
 * @Author tianye
 * @Date 2019/7/26 15:11
 * @Version 1.0
 */
@RestController
@Api("公告相关接口")
@RequestMapping(value = "/announcement")
public class AnnouncementInfoController {

    @Resource
    private IAnnouncementInfoService announcementInfoService;

    @ApiOperation("获取公告列表")
    @PostMapping("/getAnnouncementList")
    @RequestRequire(require = "page, row", parameter = GetAnnouncementListRequestDTO.class)
    public Page<AnnouncementInfoResponseDTO> getAnnouncementList(@RequestBody GetAnnouncementListRequestDTO request) {
        return announcementInfoService.getAnnouncementList(request);
    }

    @ApiOperation("查询公告详细信息")
    @GetMapping("/getAnnouncementDetail")
    public AnnouncementInfo getAnnouncementDetail(@ApiParam(name = "id", value = "公告id", required = true) @RequestParam("id") Integer id) {
        return announcementInfoService.getAnnouncementDetail(id);
    }

    @ApiOperation("删除公告")
    @GetMapping("/deleteAnnouncement")
    public void deleteAnnouncement(@ApiParam(name = "id", value = "公告id", required = true) @RequestParam("id") Integer id) {
        announcementInfoService.deleteAnnouncement(id);
    }

    @ApiOperation("添加公告信息")
    @PostMapping("/addAnnouncement")
    public void getAnnouncementList(@RequestBody AddOrUpdateAnnouncementListRequestDTO request) {
        announcementInfoService.addAnnouncement(request.getTitle(), request.getContent());
    }

    @ApiOperation("修改公告信息")
    @PostMapping("/updateAnnouncement")
    public void updateAnnouncement(@RequestBody AddOrUpdateAnnouncementListRequestDTO request) {
        announcementInfoService.updateAnnouncement(request.getId(), request.getTitle(), request.getContent());
    }

    @ApiOperation("更新公告是否已读")
    @GetMapping("/updateIsRead")
    public void updateIsRead(@ApiParam(name = "id", value = "公告id", required = true) @RequestParam("id") Integer id) {
        announcementInfoService.updateIsRead(id);
    }

}
