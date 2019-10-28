package com.rondaful.cloud.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.entity.ManageUser;
import com.rondaful.cloud.user.entity.NewUserRole;
import com.rondaful.cloud.user.entity.UserOrg;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.ManageUserMapper;
import com.rondaful.cloud.user.mapper.NewUserRoleMapper;
import com.rondaful.cloud.user.mapper.UserOrgMapper;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.dep.DepartmentDTO;
import com.rondaful.cloud.user.model.dto.role.BindRoleDTO;
import com.rondaful.cloud.user.model.dto.user.*;
import com.rondaful.cloud.user.service.IAreaCodeService;
import com.rondaful.cloud.user.service.IDepartmentService;
import com.rondaful.cloud.user.service.IManageUserService;
import com.rondaful.cloud.user.service.INewRoleService;
import com.rondaful.cloud.user.utils.MD5;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xqq
 * @Date: 2019/4/26
 * @Description:
 */
@Service("manageUserServiceImpl")
public class ManageUserServiceImpl implements IManageUserService {
    private final Logger logger = LoggerFactory.getLogger(ManageUserServiceImpl.class);
    private static String STRING_SPLIT="#";

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ManageUserMapper userMapper;
    @Autowired
    private UserOrgMapper userOrgMapper;
    @Autowired
    private IAreaCodeService areaCodeService;
    @Autowired
    private NewUserRoleMapper userRoleMapper;
    @Autowired
    private INewRoleService roleService;
    @Autowired
    private IDepartmentService departmentService;

    /**
     * 新增总后台账号
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(ManageUserDTO dto) {
        logger.info("新增总后台账号:dto={}",dto.toString());
        ManageUserDTO user=this.getByName(dto.getLoginName());
        if (user!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.login.name.exist");
        }
        if (this.getByPhone(dto.getPhone())!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.phone.exist");
        }
        ManageUser userDO=new ManageUser();
        BeanUtils.copyProperties(dto,userDO);
        userDO.setCreateDate(new Date());
        userDO.setUpdateDate(new Date());
        userDO.setStatus(1);
        userDO.setUpdateBy(dto.getCreateBy());
        if (CollectionUtils.isNotEmpty(dto.getJobNames())){
            StringBuffer sb=new StringBuffer();
            for (String jobName:dto.getJobNames()) {
                sb.append(jobName);
                sb.append(STRING_SPLIT);
            }
            userDO.setJobName(sb.toString());
        }
        //userDO.setPassWord(MD5.md5Password(dto.getPassWord()));
        this.userMapper.insert(userDO);
        this.insertBind(dto.getBinds(),dto.getRoles(),userDO.getId());
        return 1;
    }

    /**
     * 编辑用户
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(ManageUserDTO dto) {
        ManageUserDetailDTO user=this.getById(dto.getId(),null);
        if (user==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        if (!user.getPhone().equals(dto.getPhone())){
            if (this.getByPhone(dto.getPhone())!=null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.phone.exist");
            }
        }
        ManageUser userDO=new ManageUser();
        BeanUtils.copyProperties(dto,userDO);
        userDO.setUpdateDate(new Date());
        userDO.setUpdateBy(dto.getUpdateBy());
        if (CollectionUtils.isNotEmpty(dto.getJobNames())){
            StringBuffer sb=new StringBuffer();
            for (String jobName:dto.getJobNames()) {
                sb.append(jobName);
                sb.append(STRING_SPLIT);
            }
            userDO.setJobName(sb.toString());
        }
        if (StringUtils.isNotEmpty(dto.getPassWord())){
            userDO.setPassWord(dto.getPassWord());
        }
        this.userMapper.updateByPrimaryKeySelective(userDO);
        this.userOrgMapper.deleteByUserId(userDO.getId(),UserEnum.platformType.CMS.getPlatformType());
        this.userRoleMapper.deleteByUserId(userDO.getId(),UserEnum.platformType.CMS.getPlatformType());
        this.insertBind(dto.getBinds(),dto.getRoles(),dto.getId());
        this.clearCache(user.getLoginName(),userDO.getId());
        return 1;
    }



    /**
     * 根据用户名查询账号
     * @param name
     * @return
     */
    @Override
    public ManageUserDTO getByName(String name){
        logger.info("根据用名获取用户信息:name={}",name);
        String key=QUERY_USER_MANAGE_NAME+name;
        ValueOperations operations=this.redisTemplate.opsForValue();
        ManageUserDTO userDTO=(ManageUserDTO)operations.get(key);
        if (userDTO==null){
            ManageUser userDO=this.userMapper.selectByUserName(name);
            if (userDO==null){
                return null;
            }
            userDTO=new ManageUserDTO();
            BeanUtils.copyProperties(userDO,userDTO);
            /*此处目前所有账号都是admin

            if (userDO.getParentId()==0){
                userDTO.setTopUserId(userDO.getParentId());
            }else {
                userDTO.setTopUserId(1);
            }*/
            userDTO.setTopUserId(0);
            this.userOrgMapper.getAccount(userDO.getId(),UserEnum.platformType.CMS.getPlatformType());

            userDTO.setPlatformType(2);
            userDTO.setBinds(this.getBinds(userDO.getId()));
            operations.set(key,userDTO);
            this.redisTemplate.expire(key,90L, TimeUnit.DAYS);
        }
        List<Integer> roleIds = this.userRoleMapper.getsByUser(userDTO.getId(),UserEnum.platformType.CMS.getPlatformType());
        if (CollectionUtils.isNotEmpty(roleIds)){
            List<Integer> menusIds=this.roleService.getsMenu(roleIds);
            userDTO.setMenuIds(menusIds);
        }
        return userDTO;
    }

    /**
     * 分页查询账号
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<PithyUserDTO> getPage(QuerManagePageDTO dto) {
        logger.info("分页查询账号:dt0={}",dto.toString());
        List<Integer> roleIds=this.userRoleMapper.getsUserByRole(dto.getRoleId(),2);
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<ManageUser> list=this.userMapper.getPage(dto.getLoginName(),dto.getUserName(),dto.getDepartmentId(),dto.getJobs(),
                dto.getStartTime(),dto.getEndTime(),roleIds);
        PageInfo<ManageUser> pageInfo=new PageInfo<>(list);
        PageDTO<PithyUserDTO> result=new PageDTO<>(pageInfo.getTotal(),dto.getCurrentPage().longValue());
        if (pageInfo.getTotal()<1){
            return result;
        }
        List<PithyUserDTO> dtos=new ArrayList<>();
        for (ManageUser user:pageInfo.getList()) {
            PithyUserDTO dto1=new PithyUserDTO();
            BeanUtils.copyProperties(user,dto1);
            DepartmentDTO departmentDTO= this.departmentService.get(user.getDepartmentId());
            dto1.setOrgs(departmentDTO==null?null:departmentDTO.getDepartmentName());
            if (StringUtils.isNotEmpty(user.getJobName())){
                String[] job=user.getJobName().split(STRING_SPLIT);
                dto1.setJobNames(Arrays.asList(job));
            }
            List<Integer> roles=this.userRoleMapper.getsByUser(user.getId(),2);
            if (CollectionUtils.isNotEmpty(roles)){
                dto1.setRoles(this.roleService.getsName(roles));
            }
            dtos.add(dto1);
        }
        result.setList(dtos);
        return result;
    }

    /**
     * 根据id获取详细信息
     *
     * @param userId
     * @return
     */
    @Override
    public ManageUserDetailDTO getById(Integer userId,String languageType) {
        String key=QUERY_USER_MANAGE_ID+userId;
        ValueOperations operations=this.redisTemplate.opsForValue();
        ManageUserDetailDTO result=(ManageUserDetailDTO)operations.get(key);
        if (result!=null){
            return result;
        }
        ManageUser userDO=this.userMapper.selectByPrimaryKey(userId.longValue());
        if (userDO==null){
            return null;
        }
        result=new ManageUserDetailDTO();
        BeanUtils.copyProperties(userDO,result);
        result.setArea(this.getArea(userDO.getCountry(),userDO.getProvince(),userDO.getCity(),languageType));
        result.setBinds(this.getBinds(userId));
        if (StringUtils.isNotEmpty(userDO.getJobName())){
            result.setJobs(Arrays.asList(userDO.getJobName().split(STRING_SPLIT)));
        }
        List<Integer> roleIds=this.userRoleMapper.getsByUser(userId,UserEnum.platformType.CMS.getPlatformType());
        if (CollectionUtils.isNotEmpty(roleIds)){
            List<BindAccountDetailDTO> role=new ArrayList<>();
            List<BindRoleDTO> list=this.roleService.getsName(roleIds);
            for (BindRoleDTO dto:list) {
                role.add(new BindAccountDetailDTO(dto.getId().toString(),dto.getName()));
            }
            result.setRoleAll(role);
        }

        if (userDO.getDepartmentId()!=null&&userDO.getDepartmentId()>0){
            List<BindAccountDetailDTO> list=new ArrayList<>();
            result.setDepartment(this.getDepartment(userDO.getDepartmentId(),list));
        }
        operations.set(key,result,90L,TimeUnit.DAYS);
        return result;
    }

    /**
     * 根据id删除用户
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer delete(Integer userId) {
        ManageUserDetailDTO user=this.getById(userId,null);
        if (user==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        this.userMapper.deleteByPrimaryKey(userId.longValue());
        this.userOrgMapper.deleteByUserId(userId,UserEnum.platformType.CMS.getPlatformType());
        this.userRoleMapper.deleteByUserId(userId,UserEnum.platformType.CMS.getPlatformType());
        this.clearCache(user.getLoginName(),userId);
        return 1;
    }

    /**
     * 绑定用户
     *
     * @param binds
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer bindAccount(List<UserOrgDTO> binds, Integer userId) {
        ManageUserDetailDTO user=this.getById(userId,null);
        if (user==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        this.userOrgMapper.deleteByUserId(userId,UserEnum.platformType.CMS.getPlatformType());
        this.insertBind(binds,null,userId);
        this.clearCache(user.getLoginName(),userId);
        return 1;
    }

    /**
     * 获取所有管理后台的用户名
     *
     * @return
     */
    @Override
    public List<String> getAllName() {
        return this.userMapper.getAllName();
    }

    /**
     * 修改密码
     *
     * @param userId
     * @param passWord
     * @return
     */
    @Override
    public Integer updatePassWord(Integer userId, String passWord) {
        ManageUserDetailDTO dto=this.getById(userId,null);
        if (dto==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        ManageUser manageUser=new ManageUser();
        manageUser.setPassWord(passWord);
        manageUser.setId(dto.getId());
        manageUser.setCountry(dto.getCountry());
        manageUser.setProvince(dto.getProvince());
        manageUser.setCity(dto.getCity());
        Integer result=this.userMapper.updateByPrimaryKeySelective(manageUser);
        if (result>0){
            this.clearCache(dto.getLoginName(),dto.getId());
        }
        return result;
    }

    /**
     * 后台添加一级用户是将其绑定到名下
     *
     * @param cmsId
     * @param bingType
     * @param userId
     * @return
     */
    @Override
    @Async
    public Integer addBind(Integer cmsId, Integer bingType, Integer userId) {
        ManageUserDetailDTO userDTO=this.getById(cmsId,null);
        if (userDTO.getParentId()==0){
            return 0;
        }
        UserOrg userOrg=new UserOrg();
        userOrg.setUserPlatform(UserEnum.platformType.CMS.getPlatformType().byteValue());
        userOrg.setBindType(bingType.byteValue());
        userOrg.setUserId(cmsId);
        userOrg.setBindCode(userId.toString());
        Integer result=this.userOrgMapper.insert(userOrg);
        if (result>0){
            this.clearCache(userDTO.getLoginName(),userDTO.getId());
        }
        return result;
    }

    /**
     * 获取子账号名称
     *
     * @param userId
     * @return
     */
    @Override
    public List<ManageUser> getsChildName(Integer userId) {
        ManageUserDetailDTO userDetailDTO=this.getById(userId,null);
        if (userDetailDTO.getParentId()>0){
            return null;
        }
        return this.userMapper.getsChildName();
    }

    private ManageUser getByPhone(String phone){
        return this.userMapper.getByPhone(phone);
    }

    /**
     * 获取组织
     * @param id
     * @param list
     * @return
     */
    private List<BindAccountDetailDTO> getDepartment(Integer id,List<BindAccountDetailDTO> list){
        DepartmentDTO departmentDTO=this.departmentService.get(id);
        list.add(new BindAccountDetailDTO(id.toString(),departmentDTO.getDepartmentName()));
        if (departmentDTO.getLevel()!=null&&departmentDTO.getLevel()!=0){
            this.getDepartment(departmentDTO.getParentId(),list);
        }
        return list;
    }

    /**
     * 查询所有区域名
     * @param country
     * @param province
     * @param city
     * @return
     */
    private List<BindAccountDetailDTO> getArea(Integer country,Integer province,Integer city,String languageType){
        List<BindAccountDetailDTO> area=new ArrayList<>(3);
        if (country!=null&&country>0){
            area.add(new BindAccountDetailDTO(country.toString(),this.areaCodeService.getName(country,languageType)));
        }
        if (province!=null&&province>0){
            area.add(new BindAccountDetailDTO(province.toString(),this.areaCodeService.getName(province,languageType)));
        }
        if (city!=null&&city>0){
            area.add(new BindAccountDetailDTO(city.toString(),this.areaCodeService.getName(city,languageType)));
        }
        return area;
    }

    /**
     * 获取与关联账号的绑定关系
     * @param id
     * @return
     */
    private List<UserOrgDTO> getBinds(Integer id){
        List<UserOrg> accounts=this.userOrgMapper.getAccount(id,UserEnum.platformType.CMS.getPlatformType());
        List<UserOrgDTO> binds=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accounts)){
            Map<Integer,List<String>> map=new HashMap<>();
            for (UserOrg org: accounts) {
                if (!map.containsKey(org.getBindType().intValue())){
                    map.put(org.getBindType().intValue(),new ArrayList<>());
                }
                map.get(org.getBindType().intValue()).add(org.getBindCode());
            }
            for (Map.Entry<Integer,List<String>> entry:map.entrySet()) {
                UserOrgDTO dto2=new UserOrgDTO();
                dto2.setBindType(entry.getKey());
                dto2.setBindCode(entry.getValue());
                binds.add(dto2);
            }
        }
        return binds;
    }




    /**
     * 清楚缓存
     * @param name
     * @param id
     */
    private void clearCache(String name,Integer id){
        String key=QUERY_USER_MANAGE_NAME+name;
        this.redisTemplate.delete(key);
        key=QUERY_USER_MANAGE_ID+id;
        this.redisTemplate.delete(key);
    }

    /**
     * 插入绑定关系
     * @param binds
     * @param roles
     * @param userId
     * @return
     */
    private Integer insertBind(List<UserOrgDTO> binds,List<Integer> roles,Integer userId){
        if (CollectionUtils.isNotEmpty(binds)){
            List<UserOrg> orgsDO=new ArrayList<>();
            for (UserOrgDTO orgDTO:binds) {
                for (String code:orgDTO.getBindCode()) {
                    UserOrg userOrg=new UserOrg();
                    userOrg.setBindCode(code);
                    userOrg.setBindType(orgDTO.getBindType().byteValue());
                    userOrg.setUserId(userId);
                    userOrg.setUserPlatform(UserEnum.platformType.CMS.getPlatformType().byteValue());
                    orgsDO.add(userOrg);
                }

            }
            this.userOrgMapper.insertBatch(orgsDO);
        }
        if (CollectionUtils.isNotEmpty(roles)){
            List<NewUserRole> roleList=new ArrayList<>();
            for (Integer roleId:roles) {
                NewUserRole role=new NewUserRole();
                role.setRoleId(roleId);
                role.setPlatformType(UserEnum.platformType.CMS.getPlatformType().byteValue());
                role.setUserId(userId);
                roleList.add(role);
            }
            this.userRoleMapper.insertBatch(roleList);
        }
        return 1;
    }
}
