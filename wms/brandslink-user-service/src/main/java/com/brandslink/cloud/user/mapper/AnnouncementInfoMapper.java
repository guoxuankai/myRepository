package com.brandslink.cloud.user.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.user.dto.request.GetAnnouncementListRequestDTO;
import com.brandslink.cloud.user.dto.response.AnnouncementInfoResponseDTO;
import com.brandslink.cloud.user.entity.AnnouncementInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnnouncementInfoMapper extends BaseMapper<AnnouncementInfo> {

    /**
     * 获取公告列表
     *
     * @param request
     * @return
     */
    List<AnnouncementInfoResponseDTO> getAnnouncementList(GetAnnouncementListRequestDTO request);

    /**
     * 根据用户id查询所有已读公告id
     *
     * @param id
     * @return
     */
    List<Integer> selectReadByUserId(Integer id);

    /**
     * 根据公告id删除用户公告关联表
     *
     * @param id
     */
    void deleteReadByAnnouncementId(Integer id);

    /**
     * 添加用户公告已读关联表
     *
     * @param userId
     * @param id
     */
    void insertReadByUserIdAndAnnouncementId(@Param("userId") Integer userId, @Param("id") Integer id);

    /**
     * 根据账号id和公告id查询关联信息
     *
     * @param userId
     * @param id
     * @return
     */
    Integer selectReadByUserIdAndAnnouncementId(@Param("userId") Integer userId, @Param("id") Integer id);
}