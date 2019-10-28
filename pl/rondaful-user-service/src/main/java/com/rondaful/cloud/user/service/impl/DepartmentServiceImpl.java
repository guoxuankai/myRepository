package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.entity.Department;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.DepartmentMapper;
import com.rondaful.cloud.user.mapper.ManageUserMapper;
import com.rondaful.cloud.user.mapper.NewSellerUserMapper;
import com.rondaful.cloud.user.mapper.NewSupplierUserMapper;
import com.rondaful.cloud.user.model.dto.dep.DepartmentDTO;
import com.rondaful.cloud.user.model.dto.dep.DepartmentTreeDTO;
import com.rondaful.cloud.user.service.IDepartmentService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xqq
 * @Date: 2019/4/24
 * @Description:
 */
@Service("departmentServiceImpl")
public class DepartmentServiceImpl implements IDepartmentService {
    private Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private static final String POSITION_SPLIT="#";
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DepartmentMapper mapper;
    @Autowired
    private ManageUserMapper manageUserMapper;
    @Autowired
    private NewSellerUserMapper sellerUserMapper;
    @Autowired
    private NewSupplierUserMapper supplierUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(DepartmentDTO dto) {
        logger.info("添加组织:dto={}",dto.toString());
        Department department=new Department();
        BeanUtils.copyProperties(dto,department);
        if (dto.getParentId()!=0){
            Department departmentDO = this.mapper.selectByPrimaryKey(dto.getParentId().longValue());
            if (departmentDO==null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"department.parent.not.exist");
            }
            if (dto.getPlatform()!=null&&!departmentDO.getPlatform().equals(dto.getPlatform())){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"department.platform.different");
            }
            department.setAttribution(departmentDO.getAttribution());
            department.setPlatform(departmentDO.getPlatform());
            department.setLevel(departmentDO.getLevel()+1);
        }else {
            department.setLevel(0);
        }
        department.setPositionName(this.toDbPosName(dto.getPositionNames()));
        department.setCreatTime(new Date());
        department.setUpdateTime(department.getCreatTime());
        department.setUpdateBy(department.getCreateBy());
        department.setVersion(1);
        Integer result=this.mapper.insert(department);
        if (result>0){
            this.clearCache(dto.getPlatform(),dto.getAttribution());
        }
        return result;
    }

    @Override
    public Integer delete(Integer departmentId) {
        Department department=this.mapper.selectByPrimaryKey(departmentId.longValue());
        if (department==null){
            return 0;
        }
        if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(department.getPlatform().intValue())){
            if (this.supplierUserMapper.getsByDep(departmentId)>0){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"department.bind.user");
            }
        }else if (UserEnum.platformType.SELLER.getPlatformType().equals(department.getPlatform().intValue())){
            if (this.sellerUserMapper.getsByDep(departmentId)>0){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"department.bind.user");
            }
        }else if(UserEnum.platformType.CMS.getPlatformType().equals(department.getPlatform().intValue())){
            if (this.manageUserMapper.getsByDep(departmentId)>0){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"department.bind.user");
            }
        }
        if (CollectionUtils.isNotEmpty(this.mapper.getByParentId(departmentId))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"department.exist.child");
        }
        return this.mapper.deleteByPrimaryKey(department.getId().longValue());
    }

    @Override
    public Integer update(Integer departmentId, List<String> positionNames, String updateBy,String departmentName) {
        logger.info("修改组织:departmentId={}",departmentId);
        Department departmentDO = this.mapper.selectByPrimaryKey(departmentId.longValue());
        if (departmentDO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"department.parent.not.exist");
        }
        Department updateDO=new Department();
        updateDO.setDepartmentName(departmentName);
        updateDO.setUpdateBy(updateBy);
        updateDO.setUpdateTime(new Date());
        updateDO.setPositionName(this.toDbPosName(positionNames));
        updateDO.setId(departmentId);
        Integer result=this.mapper.updateByPrimaryKeySelective(updateDO);
        this.clearCache(departmentDO.getPlatform(),departmentDO.getAttribution());
        return result;
    }

    /**
     * 根据平台和归属人获取组织树
     *
     * @param platform
     * @param attribution
     * @return
     */
    @Override
    public List<DepartmentTreeDTO> getTree(Byte platform, Integer attribution) {
        logger.info("platform={},attribution={}",platform,attribution);
        String key=DEPARTMENT_KEY+platform+attribution;
        this.redisTemplate.delete(key);
        ListOperations listOperations = this.redisTemplate.opsForList();
        List<DepartmentTreeDTO> result=listOperations.range(key,0,-1);
        if (CollectionUtils.isEmpty(result)){
            List<Department> listDO=this.mapper.getByPlatform(platform,attribution);
            if (CollectionUtils.isEmpty(listDO)){
                return result;
            }
            List<DepartmentTreeDTO> list=new ArrayList<>();
            for (Department department:listDO) {
                DepartmentTreeDTO treeDTO=new DepartmentTreeDTO();
                BeanUtils.copyProperties(department,treeDTO);
                treeDTO.setPositionNames(this.toWebPosName(department.getPositionName()));
                list.add(treeDTO);
            }
            list.forEach(m -> {
                if (m.getParentId() == 0){
                    result.add(m);
                }
                list.forEach(mm -> {
                    if (mm.getParentId().equals(m.getId())) {
                        if (CollectionUtils.isEmpty(m.getChildList())) {
                            m.setChildList(new ArrayList<>());
                        }
                        m.getChildList().add(mm);
                    }
                });
            });
            listOperations.leftPushAll(key,result);
            this.redisTemplate.expire(key,10L,TimeUnit.DAYS);
        }
        return result;
    }

    /**
     * 根据id获取详情
     *
     * @param id
     * @return
     */
    @Override
    public DepartmentDTO get(Integer id) {
        if (id==null){
            return null;
        }
        Department department=this.mapper.selectByPrimaryKey(id.longValue());
        if (department==null){
            return new DepartmentDTO();
        }
        DepartmentDTO result=new DepartmentDTO();
        BeanUtils.copyProperties(department,result);
        return result;
    }

    /**
     * 清楚缓存
     * @param platform
     * @param attribution
     */
    private void clearCache(Byte platform, Integer attribution){
        if (UserEnum.platformType.CMS.getPlatformType().equals(platform)){
            attribution=null;
        }
        String key=DEPARTMENT_KEY+platform+attribution;
        this.redisTemplate.delete(key);
    }


    /**
     * 将职位名转为字符串存db
     * @param positionNames
     * @return
     */
    private String toDbPosName(List<String> positionNames){
        if (CollectionUtils.isEmpty(positionNames)){
            return null;
        }
        StringBuffer sb=new StringBuffer();
        for (String name:positionNames) {
            sb.append(name);
            sb.append(POSITION_SPLIT);
        }
        return sb.toString();
    }



    /**
     * 职位字符串转成职位数组
     * @param positionNames
     * @return
     */
    private List<String> toWebPosName(String positionNames){
        if (StringUtils.isEmpty(positionNames)){
            return null;
        }
        String[] position=positionNames.split(POSITION_SPLIT);
        return Arrays.asList(position);
    }

}
