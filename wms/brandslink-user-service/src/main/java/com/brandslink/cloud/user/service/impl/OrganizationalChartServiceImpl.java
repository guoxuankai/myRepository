package com.brandslink.cloud.user.service.impl;

import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.user.dto.response.OrganizationalChartInfoResponseDTO;
import com.brandslink.cloud.user.entity.OrganizationalChartInfo;
import com.brandslink.cloud.user.mapper.OrganizationalChartInfoMapper;
import com.brandslink.cloud.user.mapper.UserInfoMapper;
import com.brandslink.cloud.user.service.IOrganizationalChartService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 组织架构
 *
 * @ClassName IOrganizationalChartServiceImpl
 * @Author tianye
 * @Date 2019/6/5 20:01
 * @Version 1.0
 */
@Service
public class OrganizationalChartServiceImpl implements IOrganizationalChartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationalChartServiceImpl.class);

    @Resource
    private OrganizationalChartInfoMapper mapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private GetUserDetailInfoUtil getUserDetailInfoUtil;

    @Override
    public List<OrganizationalChartInfoResponseDTO> getTree() {
        // 获取组织架构所有信息
        List<OrganizationalChartInfo> infoList = mapper.selectAll();
        List<OrganizationalChartInfoResponseDTO> dtoList = new ArrayList<>();
        List<OrganizationalChartInfoResponseDTO> result = new ArrayList<>();
        infoList.forEach(i -> {
            OrganizationalChartInfoResponseDTO dto = new OrganizationalChartInfoResponseDTO();
            BeanUtils.copyProperties(i, dto);
            dtoList.add(dto);
        });
        // 获取所有公司列表
        Iterator<OrganizationalChartInfoResponseDTO> iterator = dtoList.iterator();
        while (iterator.hasNext()) {
            OrganizationalChartInfoResponseDTO next = iterator.next();
            if (next.getParentId() == 0) {
                result.add(next);
                iterator.remove();
            }
        }
        // 排序公司列表
        result.sort(Comparator.comparing(OrganizationalChartInfoResponseDTO::getSeq));
        // 递归构造公司部门列表树结构
        if (CollectionUtils.isNotEmpty(dtoList) && CollectionUtils.isNotEmpty(result)) {
            result.forEach(r -> sortedList(r, dtoList));
        }
        return result;
    }

    @Override
    public void addSubNode(Integer parentId, String name, String position) {
        verifyNameAndPosition(parentId, name, position);
        LOGGER.info("添加下级parentId:{},name:{},position:{}", parentId, name, position);
        OrganizationalChartInfo parentInfo = mapper.selectByPrimaryKey(parentId.longValue());
        UserDetailInfo detailInfo = getUserDetailInfoUtil.getUserDetailInfo();
        OrganizationalChartInfo insertInfo = new OrganizationalChartInfo(parentId, name, detailInfo.getName(), position);
        // 设置节点等级，父级节点等级+1
        Byte level = parentInfo.getLevel();
        level++;
        insertInfo.setLevel(level);
        // 查询子级节点列表中最大的顺序号，如果为空则设置为0，否则+1
        Byte maxSeq = mapper.selectMaxSeqByParentId(parentId);
        if (null == maxSeq) {
            maxSeq = 0;
        } else {
            maxSeq++;
        }
        insertInfo.setSeq(maxSeq);
        mapper.insertSelective(insertInfo);
    }

    @Override
    public void updateNodeDetail(Integer id, String name, String position) {
        verifyNameAndPosition(id, name, position);
        UserDetailInfo detailInfo = getUserDetailInfoUtil.getUserDetailInfo();
        OrganizationalChartInfo info = mapper.selectByPrimaryKey(id.longValue());
        info.setName(name);
        info.setPosition(position);
        info.setLastUpdateBy(detailInfo.getName());
        info.setLastUpdateTime(new Date());
        mapper.updateByPrimaryKeySelective(info);
        if (info.getParentId() == 0) {
            // 修改所有账号对应所属公司名称
            userInfoMapper.updateCompanyNameByCompanyId(id, name);
        } else {
            // 修改所有账号对应所有部门名称
            userInfoMapper.updateDepartmentNameByDepartmentId(id, name);
        }
    }

    @Override
    public void addTopNode(String name) {
        verifyNameAndPosition(0, name, null);
        UserDetailInfo detailInfo = getUserDetailInfoUtil.getUserDetailInfo();
        OrganizationalChartInfo insertInfo = new OrganizationalChartInfo(0, name, detailInfo.getName(), null);
        insertInfo.setName(name);
        insertInfo.setLevel(Byte.valueOf("0"));
        // 同级节点列表中最大的顺序号，如果为空则设置为0，否则+1
        Byte maxSeq = mapper.selectMaxSeqByParentId(0);
        if (null == maxSeq) {
            maxSeq = 0;
        } else {
            maxSeq++;
        }
        insertInfo.setSeq(maxSeq);
        mapper.insertSelective(insertInfo);
    }

    @Override
    public void deleteNode(Integer id) {
        // 如果删除的节点为父节点，则不能删除
        Byte maxSeq = mapper.selectMaxSeqByParentId(id);
        if (null != maxSeq) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100005);
        }
        // 如果删除的节点还有关联的账号，则不能删除
        Integer count = mapper.selectUserCountByCompanyId(id);
        if (null != count && count > 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100006);
        }
        count = mapper.selectUserCountByDepartmentId(id);
        if (null != count && count > 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100006);
        }
        mapper.deleteByPrimaryKey(id.longValue());
    }

    /**
     * 构造树列表
     *
     * @param dto
     * @param dtoList
     */
    private void sortedList(OrganizationalChartInfoResponseDTO dto, List<OrganizationalChartInfoResponseDTO> dtoList) {

        List<OrganizationalChartInfoResponseDTO> list = new ArrayList<>();
        // 获取父节点
        Iterator<OrganizationalChartInfoResponseDTO> iterator = dtoList.iterator();
        while (iterator.hasNext()) {
            OrganizationalChartInfoResponseDTO next = iterator.next();
            if (next.getParentId().equals(dto.getId())) {
                list.add(next);
                iterator.remove();
            }
        }
        // 排序当前节点，储存当前节点的子节点列表
        if (CollectionUtils.isNotEmpty(list)) {
            list.sort(Comparator.comparing(OrganizationalChartInfoResponseDTO::getSeq));
            dto.setChildren(list);
        }
        // 递归查询所有子节点列表
        if (CollectionUtils.isNotEmpty(dtoList) && CollectionUtils.isNotEmpty(list)) {
            list.forEach(l -> sortedList(l, dtoList));
        }
    }

    /**
     * 验证公司/部门和职位名称是否重复
     *
     * @param parentId
     * @param name
     * @param position
     */
    private void verifyNameAndPosition(Integer parentId, String name, String position) {
        if (StringUtils.isNotBlank(name)) {
            // 判断部门名称是否重复
            Integer count = mapper.selectNameByParentId(name, parentId);
            if (null != count && count > 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100008);
            }
        }
        if (StringUtils.isNotBlank(position)) {
            // 判断职位名称是否重复
            List<String> list = Arrays.asList(position.split(","));
            Set<String> setList = new HashSet<>(list);
            if (list.size() > setList.size()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100010);
            }
        }
    }

}
