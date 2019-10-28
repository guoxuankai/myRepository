package com.rondaful.cloud.user.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.MD5;
import com.rondaful.cloud.user.entity.*;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.enums.UserStatusEnum;
import com.rondaful.cloud.user.mapper.*;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.dep.DepartmentDTO;
import com.rondaful.cloud.user.model.dto.role.BindRoleDTO;
import com.rondaful.cloud.user.model.dto.user.*;
import com.rondaful.cloud.user.rabbitmq.RabbitConfig;
import com.rondaful.cloud.user.remote.UserFinanceInitialization;
import com.rondaful.cloud.user.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xqq
 * @Date: 2019/5/2
 * @Description:
 */
@Service("supplierServiceImpl")
public class NewSupplierServiceImpl implements INewSupplierService {
    private final Logger logger = LoggerFactory.getLogger(NewSupplierServiceImpl.class);
    private static String STRING_SPLIT="#";

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private NewSupplierUserMapper userMapper;
    @Autowired
    private NewSupplierCompanyinfoMapper companyInfoMapper;
    @Autowired
    private UserOrgMapper userOrgMapper;
    @Autowired
    private NewUserRoleMapper userRoleMapper;
    @Autowired
    private INewRoleService roleService;
    @Autowired
    private IDepartmentService departmentService;
    @Autowired
    private IAreaCodeService areaCodeService;
    /*@Autowired
    private ManageMapper manageMapper;*/
    @Autowired
    private UserFinanceInitialization initialization;

    @Autowired
    private ISupplyChainUserService chainUserService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private IManageUserService manageUserService;

    /**
     * 新增供应商
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addUser(SupplierUserDTO dto) {
        if (this.getByName(dto.getLoginName())!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.login.name.exist");
        }
        if (this.getByPhone(dto.getPhone())!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.phone.exist");
        }
        NewSupplierUser userDO=new NewSupplierUser();
        BeanUtils.copyProperties(dto,userDO);
        userDO.setCreateDate(new Date());
        userDO.setUpdateBy(userDO.getCreateBy());
        userDO.setUpdateDate(userDO.getCreateDate());
        userDO.setStatus(dto.getTopUserId()==0? UserStatusEnum.NO_ACTIVATE.getStatus():UserStatusEnum.ACTIVATE.getStatus());
        //userDO.setPassWord(dto.getPassWord());
        if (CollectionUtils.isNotEmpty(dto.getJobs())){
            StringBuffer sb=new StringBuffer();
            for (String jobName:dto.getJobs()) {
                sb.append(jobName);
                sb.append(STRING_SPLIT);
            }
            userDO.setJobName(sb.toString());
        }
        this.userMapper.insert(userDO);
        if (dto.getTopUserId()==0){
            DepartmentDTO departmentDTO=new DepartmentDTO();
            departmentDTO.setCreateBy(dto.getCreateBy());
            departmentDTO.setAttribution(userDO.getId());
            departmentDTO.setParentId(0);
            departmentDTO.setDepartmentName("top");
            departmentDTO.setPlatform(UserEnum.platformType.SUPPLIER.getPlatformType().byteValue());
            this.departmentService.add(departmentDTO);

            NewSupplierCompanyinfo companyinfo=new NewSupplierCompanyinfo();
            companyinfo.setUserId(userDO.getId());
            companyinfo.setCompanyName(userDO.getCompanyNameUser());
            this.companyInfoMapper.insert(companyinfo);
        }else {
            this.insertBind(dto.getBinds(),dto.getRoleIds(),userDO.getId());
        }
        return userDO.getId();
    }

    /**
     * 一级管供应链编辑
     *
     * @param dto1
     * @param dto2
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(SupplierUserDTO dto1, SupplierCompanyDTO dto2) {
        NewSupplierUser userDO=this.userMapper.selectByPrimaryKey(dto1.getId().longValue());
        if (userDO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        if (StringUtils.isNotEmpty(dto1.getPhone())&&!dto1.getPhone().equals(userDO.getPhone())){
            if (this.getByPhone(dto1.getPhone())!=null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.phone.exist");
            }
        }
        NewSupplierUser user=new NewSupplierUser();
        BeanUtils.copyProperties(dto1,user);
        if (StringUtils.isEmpty(dto1.getClosedCircle())){
            dto1.setClosedCircle(userDO.getClosedCircle());
        }
        if (userDO.getTopUserId()>1){
            this.userOrgMapper.deleteByUserId(userDO.getId(),UserEnum.platformType.SUPPLIER.getPlatformType());
            this.userRoleMapper.deleteByUserId(userDO.getId(),UserEnum.platformType.SUPPLIER.getPlatformType());
            this.insertBind(dto1.getBinds(),dto1.getRoleIds(),userDO.getId());
        }else {
            if (UserStatusEnum.ACTIVATE.getStatus().equals(userDO.getStatus())){
                user.setStatus(userDO.getStatus());
                /*
                配合脑残产品修改
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.account.active.not.update");
                */
            }else if (UserStatusEnum.NO_AUDIT.getStatus().equals(userDO.getStatus())){
                user.setStatus(dto1.getStatus());
            }else {
                user.setStatus(UserStatusEnum.NO_AUDIT.getStatus());
            }
            NewSupplierCompanyinfo companyinfo=new NewSupplierCompanyinfo();
            BeanUtils.copyProperties(dto2,companyinfo);
            companyinfo.setUserId(userDO.getId());
            companyinfo.setCompanyName(StringUtils.isNotEmpty(dto1.getCompanyNameUser())?dto1.getCompanyNameUser():userDO.getCompanyNameUser());
            this.companyInfoMapper.updateByPrimaryKeySelective(companyinfo);
            Object resp=null;
            String companyName=null;
            if (!UserStatusEnum.AUDIT_FILE.getStatus().equals(userDO.getStatus())){
                if (StringUtils.isNotEmpty(userDO.getSupplyChainCompany())){
                    SupplyChainUserDTO userDTO=this.chainUserService.get(Integer.valueOf(userDO.getSupplyChainCompany()));
                    companyName=(userDTO==null)?null:userDTO.getCompanyName();
                }
                SettlementSupplierDTO settlementSupplierDTO=this.settlementCycle(dto1.getClosedCircle());
                if (UserStatusEnum.NO_ACTIVATE.getStatus().equals(userDO.getStatus())){
                    resp=this.initialization.supplierInit(dto1.getId(),dto1.getCompanyNameUser(),userDO.getLoginName(),
                            Integer.valueOf(dto1.getSupplyChainCompany()),companyName,dto1.getUserName(),
                            dto1.getPhone(),companyinfo.getRegArea(),companyinfo.getRegAddress());
                    this.initialization.settlementRegist(dto1.getId(),dto1.getCompanyNameUser(),settlementSupplierDTO.getType(),settlementSupplierDTO.getStageList());
                }else {
                    resp=this.initialization.supplierUpdate(dto1.getId(),dto2.getCompanyName(),userDO.getLoginName(),Integer.valueOf(dto1.getSupplyChainCompany()),companyName,dto1.getUserName(),
                            dto1.getPhone(),companyinfo.getRegArea(),companyinfo.getRegAddress());
                    this.initialization.settlementModify(dto1.getCompanyNameUser(),dto1.getId(),settlementSupplierDTO.getType(),settlementSupplierDTO.getStageList());
                }
                JSONObject respJson=JSONObject.parseObject(JSONObject.toJSONString(resp));
                if (!respJson.getBoolean("success")){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),respJson.getString("msg"));
                }
            }
        }
        user.setId(dto1.getId());
        if (StringUtils.isNotEmpty(dto1.getPassWord())){
            user.setPassWord(dto1.getPassWord());
        }
        if (CollectionUtils.isNotEmpty(dto1.getJobs())){
            StringBuffer sb=new StringBuffer();
            for (String jobName:dto1.getJobs()) {
                sb.append(jobName);
                sb.append(STRING_SPLIT);
            }
            user.setJobName(sb.toString());
        }
        user.setUpdateDate(new Date());
        this.userMapper.updateByPrimaryKeySelective(user);
        this.clearCache(userDO.getId(),userDO.getLoginName());
        if (userDO.getTopUserId()<1&&(UserStatusEnum.NO_AUDIT.getStatus().equals(user.getStatus())||UserStatusEnum.AUDIT_FILE.getStatus().equals(user.getStatus()))){
            this.sendCms(user.getId());
        }
        return 1;
    }

    /**
     * 根据id获取详情
     *
     * @param userId
     * @return
     */
    @Override
    public SupplierUserDetailDTO getById(Integer userId) {
        ValueOperations operations=this.redisTemplate.opsForValue();
        String key=SUPPLIER_ID_CACHE+ userId;
        SupplierUserDetailDTO result=(SupplierUserDetailDTO)operations.get(key);
        if (result!=null){
            return result;
        }
        result=new SupplierUserDetailDTO();
        NewSupplierUser supplierUser=this.userMapper.selectByPrimaryKey(userId.longValue());
        if (supplierUser==null){
            return result;
        }
        NewSupplierCompanyinfo companyinfoDo=this.companyInfoMapper.selectByUserId(supplierUser.getTopUserId()==0?supplierUser.getId():supplierUser.getTopUserId());
        if (companyinfoDo!=null){
            BeanUtils.copyProperties(companyinfoDo,result);
        }
        BeanUtils.copyProperties(supplierUser,result);
        result.setId(supplierUser.getId());
        if (StringUtils.isNotEmpty(supplierUser.getJobName())){
            result.setJobs(Arrays.asList(supplierUser.getJobName().split(STRING_SPLIT)));
        }
        List<UserOrg> list=this.userOrgMapper.getAccount(userId,UserEnum.platformType.SUPPLIER.getPlatformType());
        if (CollectionUtils.isNotEmpty(list)){
            List<String> code=new ArrayList<>(list.size());
            for (UserOrg userOrg:list) {
                code.add(userOrg.getBindCode());
            }
            UserOrgDTO orgDTO=new UserOrgDTO();
            orgDTO.setBindType(4);
            orgDTO.setBindCode(code);
            List<UserOrgDTO> binds=new ArrayList<>(1);
            binds.add(orgDTO);
            result.setBinds(binds);
        }
        if (StringUtils.isNotEmpty(supplierUser.getSupplyChainCompany())){
            result.setSupplyChainCompanyName(this.chainUserService.get(Integer.valueOf(supplierUser.getSupplyChainCompany())).getCompanyName());
        }else if (supplierUser.getTopUserId()!=0){
            SupplierUserDetailDTO detailDTO=this.getById(supplierUser.getTopUserId());
            result.setSupplyChainCompany(detailDTO.getSupplyChainCompany());
            result.setSupplyChainCompanyName(detailDTO.getSupplyChainCompanyName());
            result.setCompanyName(detailDTO.getCompanyName());
            result.setCompanyNameUser(detailDTO.getCompanyNameUser());
        }
        operations.set(key,result,90L, TimeUnit.DAYS);
        return result;
    }

    /**
     * 分页获取后台一级账号
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<BackSupplierDTO> getPageBack(QueryBackSupplierDTO dto) {

        if (CollectionUtils.isEmpty(dto.getUserIds())){
            dto.setUserIds(new ArrayList<>());
        }
        if (dto.getSupplierId()!=null){
            dto.getUserIds().add(dto.getSupplierId());
        }
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<NewSupplierUser> list=this.userMapper.getPageBack(dto.getStatus(),dto.getUserIds(),dto.getSupplierName(),dto.getSupplyChainCompany(),dto.getDateType(),dto.getStartTime(),dto.getEndTime());
        PageInfo<NewSupplierUser> pageInfo=new PageInfo<>(list);
        PageDTO<BackSupplierDTO> result=new PageDTO<>(pageInfo.getTotal(),dto.getCurrentPage().longValue());
        if (pageInfo.getTotal()<1){
            return result;
        }
        List<BackSupplierDTO> data=new ArrayList<>();
        for (NewSupplierUser supplierUser:list) {
            BackSupplierDTO dto1=new BackSupplierDTO();
            BeanUtils.copyProperties(supplierUser,dto1);
            if (StringUtils.isNotEmpty(supplierUser.getSupplyChainCompany())){
                dto1.setSupplyChainCompanyName(this.chainUserService.get(Integer.valueOf(supplierUser.getSupplyChainCompany())).getCompanyName());
            }
            data.add(dto1);
        }
        result.setList(data);
        return result;
    }

    /**
     * 修改密码  公司名之类的
     *
     * @param userId
     * @param passWord
     * @param supplyChainCompany
     * @param updateBy
     * @return
     */
    @Override
    public Integer updateDetail(Integer userId, String passWord, String supplyChainCompany, String updateBy) {
        SupplierUserDetailDTO supplierUser=this.getById(userId);
        if (supplierUser==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        NewSupplierUser userDO=new NewSupplierUser();
        if (StringUtils.isNotEmpty(passWord)){
            userDO.setPassWord(passWord);
        }
        if (StringUtils.isNotEmpty(supplyChainCompany)){
            userDO.setSupplyChainCompany(supplyChainCompany);
        }
        userDO.setId(supplierUser.getId());
        userDO.setUpdateDate(new Date());
        userDO.setUpdateBy(updateBy);
        this.userMapper.updateByPrimaryKeySelective(userDO);
        this.clearCache(supplierUser.getId(),supplierUser.getLoginName());
        return 1;
    }

    /**
     * 查询子账号列表
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<PithyUserDTO> getChildPage(QueryChildDTO dto) {
        List<Integer> userIds=new ArrayList<>();
        if (dto.getRoleId()!=null){
            userIds=this.userRoleMapper.getsUserByRole(dto.getRoleId(),UserEnum.platformType.SUPPLIER.getPlatformType());
            if (CollectionUtils.isEmpty(userIds)){
                return new PageDTO<PithyUserDTO>(0L,1L);
            }
        }
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<NewSupplierUser> list=this.userMapper.getChildPage(dto.getLoginName(),dto.getUserName(),dto.getDepartmentId(),dto.getJobs(),dto.getStartTime(),dto.getEndTime(),userIds,dto.getTopUserId());
        PageInfo<NewSupplierUser> pageInfo=new PageInfo<>(list);
        PageDTO<PithyUserDTO> result=new PageDTO<>(pageInfo.getTotal(),dto.getCurrentPage().longValue());
        if (pageInfo.getTotal()<1){
            return result;
        }
        List<PithyUserDTO> data=new ArrayList<>();
        for (NewSupplierUser user:pageInfo.getList()) {
            PithyUserDTO userDTO=new PithyUserDTO();
            BeanUtils.copyProperties(user,userDTO);
            DepartmentDTO departmentDTO= this.departmentService.get(user.getDepartmentId());
            userDTO.setOrgs(departmentDTO==null?null:departmentDTO.getDepartmentName());
            if (StringUtils.isNotEmpty(user.getJobName())){
                String[] job=user.getJobName().split(STRING_SPLIT);
                userDTO.setJobNames(Arrays.asList(job));
            }
            List<Integer> roles=this.userRoleMapper.getsByUser(user.getId(),UserEnum.platformType.SUPPLIER.getPlatformType());
            if (CollectionUtils.isNotEmpty(roles)){
                userDTO.setRoles(this.roleService.getsName(roles));
            }
            data.add(userDTO);
        }
        result.setList(data);
        return result;
    }

    /**
     * 根据用户id删除账号
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer delete(Integer userId) {
        SupplierUserDetailDTO detailDTO=this.getById(userId);
        if (detailDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        this.userMapper.deleteByPrimaryKey(userId.longValue());
        this.userOrgMapper.deleteByUserId(userId,UserEnum.platformType.SUPPLIER.getPlatformType());
        this.userRoleMapper.deleteByUserId(userId,UserEnum.platformType.SUPPLIER.getPlatformType());
        this.clearCache(detailDTO.getId(),detailDTO.getLoginName());
        return 1;
    }


    /**
     * 根据名字查询用户
     *
     * @param name
     * @return
     */
    @Override
    public SupplierUserDetailDTO getByName(String name) {
        ValueOperations operations=this.redisTemplate.opsForValue();
        String key=SUPPLIER_NAME_CACHE+name;
        SupplierUserDetailDTO result=(SupplierUserDetailDTO)operations.get(key);
        result=null;
        if (result==null){
            result=new SupplierUserDetailDTO();
            NewSupplierUser userDO=this.userMapper.getByName(name);
            if (userDO==null){
                return null;
            }

            BeanUtils.copyProperties(userDO,result);
            result.setId(userDO.getId());
            List<UserOrg> list=this.userOrgMapper.getAccount(userDO.getId(),UserEnum.platformType.SUPPLIER.getPlatformType());
            if (CollectionUtils.isNotEmpty(list)){
                List<String> code=new ArrayList<>(list.size());
                for (UserOrg userOrg:list) {
                    code.add(userOrg.getBindCode());
                }
                UserOrgDTO orgDTO=new UserOrgDTO();
                orgDTO.setBindType(4);
                orgDTO.setBindCode(code);
                List<UserOrgDTO> binds=new ArrayList<>(1);
                binds.add(orgDTO);
                result.setBinds(binds);
            }
            result.setRoleIds(this.userRoleMapper.getsByUser(userDO.getId(),UserEnum.platformType.SUPPLIER.getPlatformType()));
            operations.set(key,result,90L,TimeUnit.DAYS);
        }
        if (CollectionUtils.isNotEmpty(result.getRoleIds())){
            result.setMenuIds(this.roleService.getsMenu(result.getRoleIds()));
        }
        return result;
    }

    /**
     * 获取所有一级账号
     *
     * @param userIds
     * @return
     */
    @Override
    public BindAccountDTO getTopUser(List<Integer> userIds) {
        BindAccountDTO result=new BindAccountDTO();
        List<NewSupplierUser> list=this.userMapper.getTopUser(userIds);
        if (CollectionUtils.isEmpty(list)){
            return result;
        }
        result.setType(UserEnum.platformType.SUPPLIER.getPlatformType());
        List<BindAccountDetailDTO> data=new ArrayList<>();
        for (NewSupplierUser userDO:list) {
            if (UserStatusEnum.ACTIVATE.getStatus().equals(userDO.getStatus())){
                data.add(new BindAccountDetailDTO(userDO.getId().toString(),userDO.getLoginName()));
            }
        }
        result.setList(data);
        return result;
    }

    /**
     * 根据主账号获取所有子集账号名(仅临时调用)
     *
     * @param userId
     * @return
     */
    @Override
    public List<NewSupplierUser> getChildName(Integer userId) {
        SupplierUserDetailDTO dto=this.getById(userId);
        if (dto.getTopUserId()>0){
            return new ArrayList<>(0);
        }
        return this.userMapper.getChildName(userId);
    }

    /**
     * 根据id获取子账号详情
     *
     * @param userId
     * @return
     */
    @Override
    public SupplierUserChiletDetail getChildById(Integer userId,String languageType) {
        SupplierUserChiletDetail result=new SupplierUserChiletDetail();
        SupplierUserDetailDTO detailDTO=this.getById(userId);
        if (detailDTO==null){
            return result;
        }
        BeanUtils.copyProperties(detailDTO,result);

        result.setArea(this.getArea(detailDTO.getCountry(),detailDTO.getProvince(),detailDTO.getCity(),languageType));
        List<Integer> roleIds=this.userRoleMapper.getsByUser(userId,UserEnum.platformType.SUPPLIER.getPlatformType());
        if (CollectionUtils.isNotEmpty(roleIds)){
            List<BindAccountDetailDTO> role=new ArrayList<>();
            List<BindRoleDTO> list=this.roleService.getsName(roleIds);
            for (BindRoleDTO dto:list) {
                role.add(new BindAccountDetailDTO(dto.getId().toString(),dto.getName()));
            }
            result.setRoleAll(role);
        }
        if (detailDTO.getDepartmentId()!=null&&detailDTO.getDepartmentId()>0){
            List<BindAccountDetailDTO> list=new ArrayList<>();
            result.setDepartment(this.getDepartment(detailDTO.getDepartmentId(),list));
        }
        return result;
    }

    /**
     * 处理绑定关系
     * @param binds
     * @param roles
     * @param userId
     * @return
     */
    private Integer insertBind(List<UserOrgDTO> binds, List<Integer> roles, Integer userId){
        if (CollectionUtils.isNotEmpty(binds)){
            List<UserOrg> orgsDO=new ArrayList<>();
            for (UserOrgDTO orgDTO:binds) {
                for (String code:orgDTO.getBindCode()) {
                    UserOrg userOrg=new UserOrg();
                    userOrg.setBindCode(code);
                    userOrg.setBindType(orgDTO.getBindType().byteValue());
                    userOrg.setUserId(userId);
                    userOrg.setUserPlatform(UserEnum.platformType.SUPPLIER.getPlatformType().byteValue());
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
                role.setPlatformType(UserEnum.platformType.SUPPLIER.getPlatformType().byteValue());
                role.setUserId(userId);
                roleList.add(role);
            }
            this.userRoleMapper.insertBatch(roleList);
        }
        return 1;
    }

    /**
     * 店铺绑定
     *
     * @param userId
     * @param binds
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer bindHouse(Integer userId, List<UserOrgDTO> binds) {
        SupplierUserDetailDTO detailDTO=this.getById(userId);
        this.userOrgMapper.deleteByUserId(userId,UserEnum.platformType.SUPPLIER.getPlatformType());
        this.insertBind(binds,null,userId);
        this.clearCache(detailDTO.getId(),detailDTO.getLoginName());
        return 1;
    }


    /**
     * 修改用户图片
     *
     * @param imgPath
     * @param userId
     * @param updateBy
     * @return
     */
    @Override
    public Integer updateHeadImg(String imgPath, Integer userId, String updateBy) {
        SupplierUserDetailDTO detailDTO=this.getById(userId);
        NewSupplierUser userDO=new NewSupplierUser();
        userDO.setUpdateBy(updateBy);
        userDO.setUpdateDate(new Date());
        userDO.setId(detailDTO.getId());
        userDO.setCountry(detailDTO.getCountry());
        userDO.setProvince(detailDTO.getProvince());
        userDO.setCity(detailDTO.getCity());
        userDO.setImageSite(imgPath);
        this.userMapper.updateByPrimaryKeySelective(userDO);
        this.clearCache(detailDTO.getId(),detailDTO.getLoginName());
        return 1;
    }

    /**
     * 修改手机号
     *
     * @param userId
     * @param phone
     * @param updateBy
     * @return
     */
    @Override
    public Integer updatePhone(Integer userId, String phone, String updateBy,String email) {
        SupplierUserDetailDTO detailDTO=this.getById(userId);
        if (this.getByPhone(phone)!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.phone.exist");
        }
        NewSupplierUser userDO=new NewSupplierUser();
        userDO.setUpdateBy(updateBy);
        userDO.setUpdateDate(new Date());
        userDO.setId(detailDTO.getId());
        userDO.setCountry(detailDTO.getCountry());
        userDO.setProvince(detailDTO.getProvince());
        userDO.setCity(detailDTO.getCity());
        userDO.setPhone(phone);
        userDO.setEmail(email);
        this.userMapper.updateByPrimaryKeySelective(userDO);
        this.clearCache(detailDTO.getId(),detailDTO.getLoginName());
        return 1;
    }

    /**
     * 获取有效的供应商公司名
     *
     * @return
     */
    @Override
    public List<BindAccountDetailDTO> getsActiveSupp(List<Integer> userIds) {
        List<NewSupplierUser> list=this.userMapper.getsByStatus(UserStatusEnum.ACTIVATE.getStatus());
        if (CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        List<BindAccountDetailDTO> result=new ArrayList<>(list.size());
        for (NewSupplierUser user:list) {
            if (CollectionUtils.isNotEmpty(userIds)&&!userIds.contains(user.getId())){
                continue;
            }
            result.add(new BindAccountDetailDTO(user.getId().toString(),user.getCompanyNameUser()));
        }
        return result;
    }

    /**
     * 根据手机号获取信息
     * @param phone
     * @return
     */
    @Override
    @Deprecated
    public NewSupplierUser getByPhone(String phone){
        if (StringUtils.isEmpty(phone)){
            return null;
        }
        return this.userMapper.getByPhone(phone);
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email
     * @return
     */
    @Override
    @Deprecated
    public NewSupplierUser getByEmail(String email) {
        if (StringUtils.isEmpty(email)){
            return null;
        }
        return this.userMapper.getByEmail(email);
    }

    /**
     * 根据供应链公司id获取绑定总数
     *
     * @param supplyChainId
     * @return
     */
    @Override
    public Integer getTotalAccount(Integer supplyChainId) {
        return this.userMapper.getTotalAccount(supplyChainId.toString());
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
     * 获取组织
     * @param id
     * @param list
     * @return
     */
    private List<BindAccountDetailDTO> getDepartment(Integer id,List<BindAccountDetailDTO> list){
        DepartmentDTO departmentDTO=this.departmentService.get(id);
        list.add(new BindAccountDetailDTO(id.toString(),departmentDTO.getDepartmentName()));
        if (departmentDTO.getLevel()!=0){
            this.getDepartment(departmentDTO.getParentId(),list);
        }
        return list;
    }

    /**
     * 清楚缓存
     * @param userId
     * @param loginName
     */
    private void clearCache(Integer userId,String loginName){
        String key=SUPPLIER_NAME_CACHE+loginName;
        this.redisTemplate.delete(key);
        key=SUPPLIER_ID_CACHE+userId;
        this.redisTemplate.delete(key);
    }

    private SettlementSupplierDTO settlementCycle(String closedCircle){
        SettlementSupplierDTO result=new SettlementSupplierDTO();
        List<String> list=JSONArray.parseArray(closedCircle,String.class);
        String settlementCycle=null;
        if (list.size()==1){
            switch (list.get(0)){
                case "1":
                    settlementCycle="周结";
                    break;
                case "2":
                    settlementCycle="半月结";
                    break;
                case "3":
                    settlementCycle="月结";
                    break;
                case "4":
                    settlementCycle="实时结算";
                    break;
                default:
            }
        }else {
            settlementCycle="账期结算";
            List<Double> doubles=JSONArray.parseArray(closedCircle,Double.class);
            result.setStageList(JSONObject.toJSONString(doubles));
        }
        result.setType(settlementCycle);
        return result;
    }

    /**
     * 后台发送卖家入住审核消息
     */
    private void sendCms(Integer id){
        List<Integer> userIds=this.userOrgMapper.getsByBindCode(UserEnum.platformType.CMS.getPlatformType(),UserEnum.platformType.SUPPLIER.getPlatformType(),id.toString());
        JSONObject params=new JSONObject();
        params.put("identify","SUPPLIER_ACTIVATE_ACCOUNT");
        params.put("belongSys",UserEnum.platformType.CMS.getPlatformType());
        userIds.forEach( userId -> {
            ManageUserDetailDTO manageUserDetailDTO=this.manageUserService.getById(userId,null);
            params.put("userName",manageUserDetailDTO.getLoginName());
            List<UserOrgDTO> binds=manageUserDetailDTO.getBinds();
            for (UserOrgDTO orgDTO:binds) {
                if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(orgDTO.getBindType())&&CollectionUtils.isNotEmpty(orgDTO.getBindCode())){
                    List<NewSupplierUser> users=  this.userMapper.getPageBack(UserStatusEnum.NO_AUDIT.getStatus(),JSONArray.parseArray(JSONObject.toJSONString(orgDTO.getBindCode()),Integer.class),null,null,null,null,null);
                    params.put("num",users.size());
                    break;
                }
            }
            if (params.containsKey("num")&&params.getInteger("num")>0){
                CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
                this.rabbitTemplate.convertAndSend(RabbitConfig.USER_EXCHAGE, RabbitConfig.CMS_MESSAGE_QUEUE_KEY, params.toString(), correlationId);
            }
        });


    }

}
