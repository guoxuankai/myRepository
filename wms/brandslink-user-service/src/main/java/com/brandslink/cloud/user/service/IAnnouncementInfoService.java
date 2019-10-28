package com.brandslink.cloud.user.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.user.dto.request.GetAnnouncementListRequestDTO;
import com.brandslink.cloud.user.dto.response.AnnouncementInfoResponseDTO;
import com.brandslink.cloud.user.entity.AnnouncementInfo;

/**
 * 公告
 *
 * @ClassName IAnnouncementInfoService
 * @Author tianye
 * @Date 2019/7/26 15:59
 * @Version 1.0
 */
public interface IAnnouncementInfoService {

    /**
     * 获取公告列表信息
     *
     * @param request
     * @return
     */
    Page<AnnouncementInfoResponseDTO> getAnnouncementList(GetAnnouncementListRequestDTO request);

    /**
     * 查询公告详细信息
     *
     * @param id
     * @return
     */
    AnnouncementInfo getAnnouncementDetail(Integer id);

    /**
     * 删除公告信息
     *
     * @param id
     */
    void deleteAnnouncement(Integer id);

    /**
     * 添加公告
     *
     * @param title
     * @param content
     */
    void addAnnouncement(String title, String content);

    /**
     * 修改公告信息
     *
     * @param id
     * @param title
     * @param content
     */
    void updateAnnouncement(Integer id, String title, String content);

    /**
     * 更新公告是否已读
     *
     * @param id
     */
    void updateIsRead(Integer id);
}
