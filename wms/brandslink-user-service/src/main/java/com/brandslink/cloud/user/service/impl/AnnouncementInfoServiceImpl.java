package com.brandslink.cloud.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.user.dto.request.GetAnnouncementListRequestDTO;
import com.brandslink.cloud.user.dto.response.AnnouncementInfoResponseDTO;
import com.brandslink.cloud.user.entity.AnnouncementInfo;
import com.brandslink.cloud.user.mapper.AnnouncementInfoMapper;
import com.brandslink.cloud.user.service.IAnnouncementInfoService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 公告
 *
 * @ClassName AnnouncementInfoServiceImpl
 * @Author tianye
 * @Date 2019/7/26 16:00
 * @Version 1.0
 */
@Service
public class AnnouncementInfoServiceImpl implements IAnnouncementInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementInfoServiceImpl.class);

    @Resource
    private AnnouncementInfoMapper mapper;

    @Resource
    private GetUserDetailInfoUtil getUserDetailInfoUtil;

    @Override
    public Page<AnnouncementInfoResponseDTO> getAnnouncementList(GetAnnouncementListRequestDTO request) {
        LOGGER.info("根据模糊条件查询公告列表信息request：{}", JSON.toJSONString(request));
        Page.builder(request.getPage(), request.getRow());
        List<AnnouncementInfoResponseDTO> list = mapper.getAnnouncementList(request);
        List<Integer> readList = mapper.selectReadByUserId(getUserDetailInfoUtil.getUserDetailInfo().getId());
        for (AnnouncementInfoResponseDTO dto : list) {
            if (readList.contains(dto.getId())) {
                dto.setRead("1");
            } else {
                dto.setRead("0");
            }
        }
        return new Page<>(new PageInfo<>(list));
    }

    @Override
    public AnnouncementInfo getAnnouncementDetail(Integer id) {
        return mapper.selectByPrimaryKey(id.longValue());
    }

    @Override
    public void deleteAnnouncement(Integer id) {
        mapper.deleteByPrimaryKey(id.longValue());
        mapper.deleteReadByAnnouncementId(id);
    }

    @Override
    public void addAnnouncement(String title, String content) {
        LOGGER.info("添加公告：{}", content);
        judgeContent(content);
        Date date = new Date();
        String userName = getUserDetailInfoUtil.getUserDetailInfo().getName();
        AnnouncementInfo announcementInfo = new AnnouncementInfo();
        announcementInfo.setTitle(title);
        announcementInfo.setContent(content);
        announcementInfo.setCreateBy(userName);
        announcementInfo.setCreateTime(date);
        mapper.insertSelective(announcementInfo);
    }

    @Override
    public void updateAnnouncement(Integer id, String title, String content) {
        judgeContent(content);
        Date date = new Date();
        String userName = getUserDetailInfoUtil.getUserDetailInfo().getName();
        AnnouncementInfo announcementInfo = new AnnouncementInfo();
        announcementInfo.setId(id);
        announcementInfo.setTitle(title);
        announcementInfo.setContent(content);
        announcementInfo.setLastUpdateBy(userName);
        announcementInfo.setLastUpdateTime(date);
        mapper.updateByPrimaryKeySelective(announcementInfo);
        mapper.deleteReadByAnnouncementId(id);
    }

    @Override
    public void updateIsRead(Integer id) {
        // 判断请求的公告是否已经被当前用户已读
        Integer userId = getUserDetailInfoUtil.getUserDetailInfo().getId();
        Integer count = mapper.selectReadByUserIdAndAnnouncementId(userId, id);
        if (null == count) {
            mapper.insertReadByUserIdAndAnnouncementId(userId, id);
        }
    }

    /**
     * 校验公告内容长度
     *
     * @param content
     */
    private void judgeContent(String content) {
        if (StringUtils.isNotBlank(content) && content.length() > 10000) {
            String newContent = delHtmlTags(content);
            if (newContent.length() > 10000) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100712);
            }
        }
    }

    /**
     * 去除html代码中含有的标签
     *
     * @param htmlStr
     * @return
     */
    private String delHtmlTags(String htmlStr) {
        //定义script的正则表达式，去除js可以防止注入
        String scriptRegex = "<script[^>]*?>[\\s\\S]*?</script>";
        //定义style的正则表达式，去除style样式，防止css代码过多时只截取到css样式代码
        String styleRegex = "<style[^>]*?>[\\s\\S]*?</style>";
        //定义HTML标签的正则表达式，去除标签，只提取文字内容
        String htmlRegex = "<[^>]+>";
        //定义空格,回车,换行符,制表符
        String spaceRegex = "\\s*|\t|\r|\n";

        // 过滤script标签
        htmlStr = htmlStr.replaceAll(scriptRegex, "");
        // 过滤style标签
        htmlStr = htmlStr.replaceAll(styleRegex, "");
        // 过滤html标签
        htmlStr = htmlStr.replaceAll(htmlRegex, "");
        // 过滤空格等
        htmlStr = htmlStr.replaceAll(spaceRegex, "");
        return htmlStr.trim(); // 返回文本字符串
    }

}
