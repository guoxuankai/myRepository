package com.rondaful.cloud.user.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
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
import com.rondaful.cloud.user.model.response.provider.ProviderUserDTO;
import com.rondaful.cloud.user.rabbitmq.RabbitConfig;
import com.rondaful.cloud.user.remote.EmpowerService;
import com.rondaful.cloud.user.remote.UserFinanceInitialization;
import com.rondaful.cloud.user.service.*;
import org.apache.commons.collections.CollectionUtils;
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
 * @Date: 2019/4/28
 * @Description:
 */
@Service("sellerUserServiceImpl")
public class SellerUserServiceImpl implements ISellerUserService {
    private final Logger logger = LoggerFactory.getLogger(SellerUserServiceImpl.class);
    private static String STRING_SPLIT="#";

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private NewSellerUserMapper userMapper;
    @Autowired
    private SellerCompanyInfoMapper companyInfoMapper;
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
    @Autowired
    private UserFinanceInitialization initialization;
    @Autowired
    private ISupplyChainUserService chainUserService;
    @Autowired
    private EmpowerService empowerService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增总卖家账号
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(SellerUserDTO dto) {
        logger.info("新增卖家账号:dto={}",dto.toString());
        if (this.getByName(dto.getLoginName())!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.login.name.exist");
        }
        if (this.getByPhone(dto.getPhone())!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.phone.exist");
        }
        NewSellerUser sellerUser=new NewSellerUser();
        BeanUtils.copyProperties(dto,sellerUser);
        sellerUser.setCreateBy(dto.getCreateBy());
        sellerUser.setCreateDate(new Date());
        sellerUser.setUpdateDate(sellerUser.getCreateDate());
        sellerUser.setVersion(1);
        //sellerUser.setPassWord(MD5.md5Password(dto.getPassWord()));
        if (CollectionUtils.isNotEmpty(dto.getJobs())){
            StringBuffer sb=new StringBuffer();
            for (String jobName:dto.getJobs()) {
                sb.append(jobName);
                sb.append(STRING_SPLIT);
            }
            sellerUser.setJobName(sb.toString());
        }
        this.userMapper.insert(sellerUser);
        if (dto.getTopUserId()==0){
            DepartmentDTO departmentDTO=new DepartmentDTO();
            departmentDTO.setCreateBy(dto.getCreateBy());
            departmentDTO.setAttribution(sellerUser.getId());
            departmentDTO.setParentId(0);
            departmentDTO.setDepartmentName("top");
            departmentDTO.setPlatform(UserEnum.platformType.SELLER.getPlatformType().byteValue());
            this.departmentService.add(departmentDTO);
        }else {
            this.insertBind(dto.getBinds(),dto.getRoleIds(),sellerUser.getId());
        }

        this.insertBind(dto.getBinds(),dto.getRoleIds(),sellerUser.getId());
        return sellerUser.getId();
    }

    /**
     * 编辑用户
     *
     * @param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(SellerUserDTO dto1,SellerCompanyDTO dto2) {
        SellerUserDetailDTO detailDTO=this.getById(dto1.getId());
        if (detailDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        if (StringUtils.isNotEmpty(dto1.getPhone())&&!dto1.getPhone().equals(detailDTO.getPhone())){
            if (this.getByPhone(dto1.getPhone())!=null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.phone.exist");
            }
        }
        NewSellerUser userDO=new NewSellerUser();
        BeanUtils.copyProperties(dto1,userDO);
        userDO.setUpdateDate(new Date());
        if (StringUtils.isNotEmpty(dto1.getPassWord())){
            userDO.setPassWord(dto1.getPassWord());
        }
        Object resp=null;
        if (detailDTO.getTopUserId()>0){
            this.userOrgMapper.deleteByUserId(userDO.getId(),UserEnum.platformType.SELLER.getPlatformType());
            this.userRoleMapper.deleteByUserId(userDO.getId(),UserEnum.platformType.SELLER.getPlatformType());
            this.insertBind(dto1.getBinds(),dto1.getRoleIds(),detailDTO.getId());
            if (CollectionUtils.isNotEmpty(dto1.getJobs())){
                StringBuffer sb=new StringBuffer();
                for (String jobName:dto1.getJobs()) {
                    sb.append(jobName);
                    sb.append(STRING_SPLIT);
                }
                userDO.setJobName(sb.toString());
            }
        }else {
            SellerCompanyInfo companyDO=this.companyInfoMapper.getByUserId(dto1.getId());
            String companyName=null;
            if (companyDO==null){
                companyDO=new SellerCompanyInfo();
                BeanUtils.copyProperties(dto2,companyDO);
                companyDO.setUserId(dto1.getId());
                this.companyInfoMapper.insert(companyDO);
                if (StringUtils.isNotEmpty(userDO.getSupplyChainCompany())){
                    SupplyChainUserDTO chainUserDTO=this.chainUserService.get(Integer.valueOf(userDO.getSupplyChainCompany()));
                    companyName=chainUserDTO==null?null:chainUserDTO.getCompanyName();
                }
            }else {
                SellerCompanyInfo companyInfo=new SellerCompanyInfo();
                BeanUtils.copyProperties(dto2,companyInfo);
                companyInfo.setUserId(dto1.getId());
                companyInfo.setId(companyDO.getId());
                this.companyInfoMapper.updateByPrimaryKey(companyInfo);
                if (StringUtils.isNotEmpty(userDO.getSupplyChainCompany())){
                    SupplyChainUserDTO chainUserDTO=this.chainUserService.get(Integer.valueOf(userDO.getSupplyChainCompany()));
                    companyName=chainUserDTO==null?null:chainUserDTO.getCompanyName();
                }
            }
            if (StringUtils.isNotEmpty(dto1.getSupplyChainCompany())){
                if (UserStatusEnum.NO_ACTIVATE.getStatus().equals(detailDTO.getStatus())){
                    resp=this.initialization.sellerInit(dto1.getId(),dto1.getLoginName(),Integer.valueOf(dto1.getSupplyChainCompany()),companyName,
                            dto1.getUserName(),dto1.getPhone(),dto2.getRegArea(),dto2.getRegAddress());
                }else {
                    resp=this.initialization.sellerUpdate(dto1.getId(),dto1.getLoginName(),Integer.valueOf(dto1.getSupplyChainCompany()),companyName,
                            dto1.getUserName(),dto1.getPhone(),dto2.getRegArea(),dto2.getRegAddress());
                }
                JSONObject respJson=JSONObject.parseObject(JSONObject.toJSONString(resp));
                if (!respJson.getBoolean("success")){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),respJson.getString("msg"));
                }
            }
            if (!UserStatusEnum.ACTIVATE.getStatus().equals(detailDTO.getStatus())){
                userDO.setStatus(UserStatusEnum.NO_AUDIT.getStatus());
                userDO.setCreateDate(new Date());
            }
        }
        this.userMapper.updateByPrimaryKeySelective(userDO);
        this.clearCache(detailDTO.getId(),detailDTO.getLoginName());
        if (detailDTO.getTopUserId()<1&&UserStatusEnum.NO_AUDIT.getStatus().equals(userDO.getStatus())){
            this.sendCms();
        }
        return 1;
    }

    /**
     * @param userId
     * @param passWord
     * @param supplyChainCompany
     * @param updateBy
     * @return
     */
    @Override
    public Integer updateDetail(Integer userId, String passWord, String supplyChainCompany, String updateBy) {
        SellerUserDetailDTO detailDTO=this.getById(userId);
        if (detailDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        NewSellerUser userDO=new NewSellerUser();
        if (StringUtils.isNotEmpty(passWord)){
            userDO.setPassWord(passWord);
        }
        userDO.setId(userId);
        //userDO.setUpdateBy(updateBy);
        //userDO.setUpdateDate(new Date());
        userDO.setSupplyChainCompany(supplyChainCompany);
        userDO.setCountry(detailDTO.getCountry());
        userDO.setProvince(detailDTO.getProvince());
        userDO.setCity(detailDTO.getCity());
        this.userMapper.updateByPrimaryKeySelective(userDO);
        if (StringUtils.isNotEmpty(supplyChainCompany)&&UserStatusEnum.ACTIVATE.getStatus().equals(detailDTO.getStatus())){
            this.initialization.sellerRebind(userId,Integer.valueOf(supplyChainCompany));
        }
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
    public SellerUserDetailDTO getByName(String name) {
        String key=QUERY_SELLER_USER_MANAGE_NAME+name;
        ValueOperations operations=this.redisTemplate.opsForValue();
        SellerUserDetailDTO dto=(SellerUserDetailDTO)operations.get(key);
        if (dto==null){
            NewSellerUser userDO=this.userMapper.getByName(name);
            if (userDO==null){
                return null;
            }

            dto=new SellerUserDetailDTO();
            BeanUtils.copyProperties(userDO,dto);
            List<UserOrg> list=this.userOrgMapper.getAccount(userDO.getId(),UserEnum.platformType.SELLER.getPlatformType());
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
                dto.setBinds(binds);
            }
            SellerCompanyInfo sellerCompanyInfo=this.companyInfoMapper.getByUserId(dto.getId());
            if (sellerCompanyInfo!=null){
                dto.setSellerType(sellerCompanyInfo.getSellerType());
            }
            operations.set(key,dto,60L, TimeUnit.DAYS);
        }
        List<Integer> roleIds=this.userRoleMapper.getsByUser(dto.getId(),UserEnum.platformType.SELLER.getPlatformType());
        if (CollectionUtils.isNotEmpty(roleIds)){
            dto.setMenuIds(this.roleService.getsMenu(roleIds));
        }
        return dto;
    }

    /**
     * 根据id获取用户详情
     *
     * @param userId
     * @return
     */
    @Override
    public SellerUserDetailDTO getById(Integer userId) {
        String key=QUERY_SELLER_USER_MANAGE_ID+userId;
        ValueOperations operations=this.redisTemplate.opsForValue();
        SellerUserDetailDTO result=(SellerUserDetailDTO)operations.get(key);
        if (result==null){
            result=new SellerUserDetailDTO();
            SellerCompanyInfo companyInfo=this.companyInfoMapper.getByUserId(userId);
            if (companyInfo!=null){
                BeanUtils.copyProperties(companyInfo,result);
            }
            NewSellerUser user=this.userMapper.selectByPrimaryKey(userId.longValue());
            if (user==null){
                return null;
            }
            if (user.getTopUserId()>0){
                companyInfo=this.companyInfoMapper.getByUserId(user.getTopUserId());
                if (companyInfo!=null){
                    BeanUtils.copyProperties(companyInfo,result);
                }
            }
            BeanUtils.copyProperties(user,result);
            if (StringUtils.isNotEmpty(user.getJobName())){
                result.setJobs(Arrays.asList(user.getJobName().split(STRING_SPLIT)));
            }
            List<UserOrg> orgs=this.userOrgMapper.getAccount(user.getId(),UserEnum.platformType.SELLER.getPlatformType());
            if (CollectionUtils.isNotEmpty(orgs)){
                List<UserOrgDTO> orgDTOS=new ArrayList<>();
                List<String> codes=new ArrayList<>();
                for (UserOrg org:orgs) {
                    codes.add(org.getBindCode());
                }
                orgDTOS.add(new UserOrgDTO(4,codes));
                result.setBinds(orgDTOS);
            }
            if (StringUtils.isNotEmpty(user.getSupplyChainCompany())){
                result.setSupplyChainCompanyName(this.chainUserService.get(Integer.valueOf(user.getSupplyChainCompany())).getCompanyName());
            }else if (user.getTopUserId()!=0){
                SellerUserDetailDTO detailDTO=this.getById(user.getTopUserId());
                result.setCompanyName(detailDTO.getCompanyName());
                result.setSupplyChainCompany(detailDTO.getSupplyChainCompany());
                result.setSupplyChainCompanyName(detailDTO.getSupplyChainCompanyName());

            }
            result.setPassWord(null);
            operations.set(key,result,60L,TimeUnit.DAYS);
        }
        return result;
    }


    /**
     * 获取子账号详情
     *
     * @param userId
     * @return
     */
    @Override
    public SellerUserChildDetail getChildById(Integer userId,String languageType) {
        SellerUserDetailDTO detailDTO=this.getById(userId);
        if (detailDTO==null){
            return null;
        }
        SellerUserChildDetail result=new SellerUserChildDetail();
        BeanUtils.copyProperties(detailDTO,result);

        result.setArea(this.getArea(detailDTO.getCountry(),detailDTO.getProvince(),detailDTO.getCity(),languageType));
        List<Integer> roleIds=this.userRoleMapper.getsByUser(userId,UserEnum.platformType.SELLER.getPlatformType());
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
     * 后台分页查询接口
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<BackSellerPageDTO> getBackPage(QueryBackSellerDTO dto) {
        logger.info("后台分页查询接口:dto={}",dto.toString());
        QuerySellerPageDO queryDO=new QuerySellerPageDO();
        BeanUtils.copyProperties(dto,queryDO);

        List<Integer> status=new ArrayList<>();
        if (dto.getQueryType()==null){
            status.add(UserStatusEnum.ACTIVATE.getStatus());
            status.add(UserStatusEnum.DISABLE.getStatus());
        }else {
            if (dto.getStatus()==null){
                status.add(UserStatusEnum.AUDIT_FILE.getStatus());
                status.add(UserStatusEnum.NO_AUDIT.getStatus());
                status.add(UserStatusEnum.NO_ACTIVATE.getStatus());
            }else {
                status.add(dto.getStatus());
            }
        }
        queryDO.setStatus(status);
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<NewSellerUser> list=this.userMapper.getBackPage(queryDO);
        PageInfo<NewSellerUser> pageInfo=new PageInfo<>(list);
        PageDTO<BackSellerPageDTO> result=new PageDTO<>(pageInfo.getTotal(),dto.getCurrentPage().longValue());
        if (pageInfo.getTotal()<1){
            return result;
        }
        List<BackSellerPageDTO> data=new ArrayList<>();
        for (NewSellerUser sellerUser:pageInfo.getList()) {
            BackSellerPageDTO dto1=new BackSellerPageDTO();
            BeanUtils.copyProperties(sellerUser,dto1);
            if (StringUtils.isNotEmpty(sellerUser.getSupplyChainCompany())){
                dto1.setSupplyChainCompanyName(this.chainUserService.get(Integer.valueOf(sellerUser.getSupplyChainCompany())).getCompanyName());
            }
            dto1.setSellerType(this.getById(sellerUser.getId()).getSellerType());
            StoreAccountDTO accountDTO=new StoreAccountDTO();
            if (UserStatusEnum.ACTIVATE.getStatus().equals(sellerUser.getStatus())){
                Object resp=this.initialization.getSellerCreditInfo(sellerUser.getId());
                if (resp!=null){
                    JSONObject respJson= JSONObject.parseObject(JSONObject.toJSONString(resp));
                    if (respJson.getBoolean("success")){
                        for (int i = 0; i < respJson.getJSONArray("data").size(); i++) {
                            JSONObject rdf=JSONObject.parseObject(JSONObject.toJSONString(respJson.getJSONArray("data").get(i)));
                            if ("PERSONAL".equals(rdf.getString("shopType"))){
                                accountDTO.setPersonal(rdf.getString("creditLimit"));
                            }else if ("RENT".equals(rdf.getString("shopType"))){
                                accountDTO.setRent(rdf.getString("creditLimit"));
                            }
                            accountDTO.setApplyStatus(("Credited".equals(accountDTO.getApplyStatus())?"Credited":rdf.getString("applyStatus")));
                            accountDTO.setRemark(rdf.getString("remark"));
                        }
                    }else {
                        logger.error("");
                    }
                }
            }
            dto1.setStoreAccount(accountDTO);
            dto1.setCompanyCame(this.getById(sellerUser.getId()).getCompanyName());
            data.add(dto1);
        }
        result.setList(data);
        return result;
    }


    /**
     * 根据授信状态查询
     *
     * @param status
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Override
    public PageDTO<BackSellerPageDTO> getsCreditPage(String status, Integer currentPage, Integer pageSize) {
        Object object=this.initialization.pageQuery(currentPage,pageSize,status,null,null);
        if (object==null){
            return new PageDTO(0L,currentPage.longValue());
        }
        JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
        if (jsonObject.getJSONObject("data")==null){
            return new PageDTO(0L,currentPage.longValue());
        }
        PageDTO<BackSellerPageDTO> result=new PageDTO(jsonObject.getJSONObject("data").getLongValue("total"),currentPage.longValue());
        List<BackSellerPageDTO> data=new ArrayList<>();
        for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("list").size(); i++) {
            JSONObject dataJson=jsonObject.getJSONObject("data").getJSONArray("list").getJSONObject(i);
            BackSellerPageDTO dto=new BackSellerPageDTO();
            SellerUserDetailDTO detailDTO=this.getById(dataJson.getInteger("sellerId"));
            if (detailDTO==null){
                continue;
            }
            BeanUtils.copyProperties(detailDTO,dto);
            StoreAccountDTO accountDTO=new StoreAccountDTO();
            accountDTO.setRemark(dataJson.getString("remark"));
            Object resp=this.initialization.getSellerCreditInfo(detailDTO.getId());
            if (resp!=null){
                JSONObject respJson= JSONObject.parseObject(JSONObject.toJSONString(resp));
                if (respJson.getBoolean("success")){
                    for (int  j= 0; j< respJson.getJSONArray("data").size(); j++) {
                        JSONObject rdf=JSONObject.parseObject(JSONObject.toJSONString(respJson.getJSONArray("data").get(j)));
                        if ("PERSONAL".equals(rdf.getString("shopType"))){
                            accountDTO.setPersonal(rdf.getString("creditLimit"));
                        }else if ("RENT".equals(rdf.getString("shopType"))){
                            accountDTO.setRent(rdf.getString("creditLimit"));
                        }
                        accountDTO.setApplyStatus(("Credited".equals(accountDTO.getApplyStatus())?"Credited":rdf.getString("applyStatus")));
                        accountDTO.setRemark(rdf.getString("remark"));
                    }
                }else {
                    logger.error("");
                }
            }
            dto.setStoreAccount(accountDTO);
            data.add(dto);
        }
        result.setList(data);
        return result;
    }

    /**
     * 获取所有一级账号
     *
     * @return
     */
    @Override
    public BindAccountDTO getsParent(List<Integer> userIds,Integer type) {
        BindAccountDTO result=new BindAccountDTO();
        result.setType(UserEnum.platformType.SELLER.getPlatformType());
        List<NewSellerUser> list=this.userMapper.getTop(userIds,type);
        List<BindAccountDetailDTO> data=new ArrayList<>();
        for (NewSellerUser user:list) {
            data.add(new BindAccountDetailDTO(user.getId().toString(),user.getLoginName()));
            /*if (UserStatusEnum.ACTIVATE.getStatus().equals(user.getStatus())){

            }*/
        }

        result.setList(data);
        return result;
    }

    /**
     * 根据平台查询对应卖家下的子账号
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<PithyUserDTO> getChildPage(QueryChildDTO dto) {
        List<Integer> userIds=this.userRoleMapper.getsUserByRole(dto.getRoleId(),UserEnum.platformType.SELLER.getPlatformType());
        if (dto.getRoleId()!=null&&CollectionUtils.isEmpty(userIds)){
            return new PageDTO<>(0L,1L);
        }
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<NewSellerUser> list=this.userMapper.getChildPage(dto.getLoginName(),dto.getUserName(),dto.getDepartmentId(),dto.getJobs(),
                dto.getStartTime(),dto.getEndTime(),userIds,dto.getTopUserId());

        PageInfo<NewSellerUser> pageInfo=new PageInfo<>(list);
        PageDTO<PithyUserDTO> result=new PageDTO<>(pageInfo.getTotal(),dto.getCurrentPage().longValue());
        if (pageInfo.getTotal()<1){
            return result;
        }
        List<PithyUserDTO> data=new ArrayList<>();
        for (NewSellerUser user:pageInfo.getList()) {
            PithyUserDTO dto1=new PithyUserDTO();
            BeanUtils.copyProperties(user,dto1);
            DepartmentDTO departmentDTO= this.departmentService.get(user.getDepartmentId());
            dto1.setOrgs(departmentDTO==null?null:departmentDTO.getDepartmentName());
            if (StringUtils.isNotEmpty(user.getJobName())){
                String[] job=user.getJobName().split(STRING_SPLIT);
                dto1.setJobNames(Arrays.asList(job));
            }
            List<Integer> roles=this.userRoleMapper.getsByUser(user.getId(),UserEnum.platformType.SELLER.getPlatformType());
            if (CollectionUtils.isNotEmpty(roles)){
                dto1.setRoles(this.roleService.getsName(roles));
            }
            data.add(dto1);
        }
        result.setList(data);
        return result;
    }

    /**
     * 根据id批量获取用户
     *
     * @param userIds
     * @return
     */
    @Override
    public List<NewSellerUser> getsName(List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)){
            return null;
        }
        return this.userMapper.getsName(userIds);
    }

    /**
     * 获取子账号名（临时调用）
     *
     * @param userId
     * @return
     */
    @Deprecated
    @Override
    public List<NewSellerUser> getsChildName(Integer userId) {
        SellerUserDetailDTO dto=this.getById(userId);
        if (dto.getTopUserId()>0){
            return new ArrayList<>(0);
        }
        return this.userMapper.getsChildName(userId);
    }

    /**
     * @param binds
     * @param userId
     * @param updateBy
     * @return
     */
    @Override
    public Integer bindEmp(List<UserOrgDTO> binds, Integer userId, String updateBy) {
        SellerUserDetailDTO detailDTO=this.getById(userId);
        this.userOrgMapper.deleteByUserId(userId,UserEnum.platformType.SELLER.getPlatformType());
        this.insertBind(binds,null,userId);
        this.clearCache(detailDTO.getId(),detailDTO.getLoginName());
        return 1;
    }

    /**
     * 删除账号
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer delete(Integer userId) {
        SellerUserDetailDTO oldDTO=this.getById(userId);
        if (oldDTO==null){
            return 0;
        }
        this.userOrgMapper.deleteByUserId(userId,UserEnum.platformType.SELLER.getPlatformType());
        this.userRoleMapper.deleteByUserId(userId,UserEnum.platformType.SELLER.getPlatformType());
        this.userMapper.deleteByPrimaryKey(userId.longValue());
        this.companyInfoMapper.deleteByUserId(userId);
        this.departmentService.delete(userId);
        this.clearCache(oldDTO.getId(),oldDTO.getLoginName());
        return 1;
    }

    /**
     * 修改头像
     *
     * @param userId
     * @param imgPath
     * @return
     */
    @Override
    public Integer updateHeadImg(Integer userId, String imgPath) {
        SellerUserDetailDTO detailDTO=this.getById(userId);
        NewSellerUser userDO=new NewSellerUser();
        userDO.setId(userId);
        //userDO.setUpdateBy(userDO.getLoginName());
        //userDO.setUpdateDate(new Date());
        userDO.setCountry(detailDTO.getCountry());
        userDO.setProvince(detailDTO.getProvince());
        userDO.setCity(detailDTO.getCity());
        userDO.setHeadImg(imgPath);
        this.userMapper.updateByPrimaryKeySelective(userDO);
        this.clearCache(userId,detailDTO.getLoginName());
        return 1;
    }

    /**
     * 修改手机号
     *
     * @param phone
     * @param userId
     * @return
     */
    @Override
    public Integer updatePhone(String phone, Integer userId,String email) {
        SellerUserDetailDTO detailDTO=this.getById(userId);
        if (this.getByPhone(phone)!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.phone.exist");
        }else if (this.getByEmail(email)!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.email.exist");
        }
        NewSellerUser userDO=new NewSellerUser();
        userDO.setId(userId);
        //userDO.setUpdateBy(userDO.getLoginName());
        //userDO.setUpdateDate(new Date());
        userDO.setCountry(detailDTO.getCountry());
        userDO.setProvince(detailDTO.getProvince());
        userDO.setCity(detailDTO.getCity());
        userDO.setPhone(phone);
        userDO.setEmail(email);
        this.userMapper.updateByPrimaryKeySelective(userDO);
        this.clearCache(userId,detailDTO.getLoginName());
        return 1;
    }

    /**
     * 修改状态
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateStatus(SellerUserDTO dto,String mainCategory) {
        SellerUserDetailDTO detailDTO=this.getById(dto.getId());
        if (detailDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        NewSellerUser userDO=new NewSellerUser();
        BeanUtils.copyProperties(dto,userDO);
        userDO.setUpdateDate(new Date());
        userDO.setCountry(detailDTO.getCountry());
        userDO.setProvince(detailDTO.getProvince());
        userDO.setCity(detailDTO.getCity());
        if (UserStatusEnum.ACTIVATE.getStatus().equals(dto.getStatus())){
            userDO.setAuthDate(userDO.getUpdateDate());
            userDO.setAuthBy(userDO.getUpdateBy());
            if (dto.getSupplyChainCompany()==null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.params");
            }
            SupplyChainUserDTO chainUserDTO=this.chainUserService.get(Integer.valueOf(userDO.getSupplyChainCompany()));
            String companyName=(chainUserDTO==null)?null:chainUserDTO.getCompanyName();
            JSONObject respJson=new JSONObject();
            if (StringUtils.isEmpty(detailDTO.getSupplyChainCompany())){
                Object resp=this.initialization.sellerInit(detailDTO.getId(),detailDTO.getLoginName(),Integer.valueOf(userDO.getSupplyChainCompany()),companyName,
                        detailDTO.getUserName(),StringUtils.isEmpty(detailDTO.getPhone())?" ":detailDTO.getPhone(),StringUtils.isEmpty(detailDTO.getRegArea())?" ":detailDTO.getRegArea(),StringUtils.isEmpty(detailDTO.getRegAddress())?" ":detailDTO.getRegAddress());
                respJson=JSONObject.parseObject(JSONObject.toJSONString(resp));
            }else {
                Object resp=this.initialization.sellerUpdate(detailDTO.getId(),detailDTO.getLoginName(),Integer.valueOf(userDO.getSupplyChainCompany()),companyName,
                        detailDTO.getUserName(),StringUtils.isEmpty(detailDTO.getPhone())?" ":detailDTO.getPhone(),StringUtils.isEmpty(detailDTO.getRegArea())?" ":detailDTO.getRegArea(),StringUtils.isEmpty(detailDTO.getRegAddress())?" ":detailDTO.getRegAddress());
                respJson=JSONObject.parseObject(JSONObject.toJSONString(resp));
                if (!respJson.getBoolean("success")){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),respJson.getString("msg"));
                }
            }
            if (!respJson.getBoolean("success")){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),respJson.getString("msg"));
            }
            this.empowerService.insertObjectOthers(detailDTO.getLoginName(),detailDTO.getId());
        }else if (UserStatusEnum.AUDIT_FILE.getStatus().equals(dto.getStatus())){
            userDO.setAuthDate(userDO.getUpdateDate());
            userDO.setAuthBy(userDO.getUpdateBy());
        }
        Integer result=this.userMapper.updateByPrimaryKeySelective(userDO);
        if (StringUtils.isNotEmpty(mainCategory)){
            this.companyInfoMapper.updateMainCategory(dto.getId(),mainCategory);
        }
        if (result>0){
            this.clearCache(detailDTO.getId(),detailDTO.getLoginName());
            this.sendCms();
        }
        return result;
    }

    /**
     * 清楚缓存
     * @param id
     * @param name
     */
    private void clearCache(Integer id,String name){
        String key=QUERY_SELLER_USER_MANAGE_ID+id;
        this.redisTemplate.delete(key);
        key=QUERY_SELLER_USER_MANAGE_NAME+name;
        this.redisTemplate.delete(key);
    }

    /**
     * 根据手机号查询
     * @param phone
     * @return
     */
    @Override
    public SellerUserDetailDTO getByPhone(String phone){
        NewSellerUser user=this.userMapper.getByPhone(phone);
        if (user==null){
            return null;
        }
        return this.getByName(user.getLoginName());
    }

    /**
     * 根据邮件查询用户
     *
     * @param email
     * @return
     */
    @Override
    @Deprecated
    public SellerUserDetailDTO getByEmail(String email) {
        NewSellerUser user=this.userMapper.getByEmail(email);
        if (user==null){
            return null;
        }
        return this.getByName(user.getLoginName());
    }

    /**
     * 个人中心资料修改
     *
     * @param dto
     * @return
     */
    @Override
    public Integer updateCenter(SellerUserDTO dto) {
        SellerUserDetailDTO oldUser=this.getById(dto.getId());
        if (StringUtils.isNotEmpty(dto.getPhone())&&!dto.getPhone().equals(oldUser.getPhone())&&this.getByPhone(dto.getPhone())!=null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.phone.exist");
        }
        NewSellerUser sellerUser=new NewSellerUser();
        BeanUtils.copyProperties(dto,sellerUser,"updateBy","updateDate");
        //sellerUser.setUpdateDate(new Date());
        sellerUser.setCountry(oldUser.getCountry());
        sellerUser.setProvince(oldUser.getProvince());
        sellerUser.setCity(oldUser.getCity());
        if (CollectionUtils.isNotEmpty(dto.getJobs())){
            StringBuffer sb=new StringBuffer();
            for (String jobName:dto.getJobs()) {
                sb.append(jobName);
                sb.append(STRING_SPLIT);
            }
            sellerUser.setJobName(sb.toString());
        }
        Integer result=this.userMapper.updateByPrimaryKeySelective(sellerUser);
        if (result>0){
            this.clearCache(oldUser.getId(),oldUser.getLoginName());
        }
        return result;
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
     * 子账号绑定店铺
     *
     * @param userId
     * @param empowerId
     * @return
     */
    @Override
    public Integer bindStore(Integer userId, Integer empowerId) {
        SellerUserDetailDTO oldDTO=this.getById(userId);
        if (oldDTO==null||oldDTO.getTopUserId()==0){
            return 0;
        }
        UserOrg userOrg=new UserOrg();
        userOrg.setUserPlatform(UserEnum.platformType.SELLER.getPlatformType().byteValue());
        userOrg.setBindType(userOrg.getUserPlatform());
        userOrg.setUserId(userId);
        userOrg.setBindCode(empowerId.toString());
        if (this.userOrgMapper.insert(userOrg)>0){
            this.clearCache(oldDTO.getId(),oldDTO.getLoginName());
        }
        return 1;
    }


    /**
     * 获取数据库当前最大的id
     *
     * @return
     */
    @Override
    public Integer getMaxId() {
        return this.userMapper.getMaxId();
    }

    /**
     * 根据状态获取一级账号总数
     *
     * @param status
     * @return
     */
    @Override
    public Integer getSizeByStatus(Integer status) {
        return this.userMapper.getSizeByStatus(status);
    }

    /**
     * 导出
     *
     * @param currentPage
     * @param status
     * @return
     */
    @Override
    public List<Object> export(Integer currentPage, Integer userId,Date startDate,Date endDate) {
        List<Object> result=new ArrayList<>();
        PageHelper.startPage(currentPage,100);
        List<NewSellerUser> list=this.userMapper.getChildPage(null,null,null,null,startDate,endDate,null,userId);
        PageInfo<NewSellerUser> pageInfo=new PageInfo<>(list);
        if (CollectionUtils.isEmpty(pageInfo.getList())){
            return null;
        }
        pageInfo.getList().forEach(info->{
            SellerUserExportDTO exportDTO=new SellerUserExportDTO();
            exportDTO.setName(info.getLoginName());
            exportDTO.setEmail(info.getEmail());
            exportDTO.setPhone(info.getPhone());
            List<Integer> roleIds=this.userRoleMapper.getsByUser(info.getId(),UserEnum.platformType.SELLER.getPlatformType());
            StringBuilder sbRole=new StringBuilder();
            if (CollectionUtils.isNotEmpty(roleIds)){
                List<BindRoleDTO> roleDTOS=this.roleService.getsName(roleIds);
                roleDTOS.forEach(roleDTO->{
                    sbRole.append(roleDTO.getName());
                    sbRole.append(",");
                });
            }
            exportDTO.setRole(sbRole.toString());
            DepartmentDTO departmentDTO=this.departmentService.get(info.getDepartmentId());
            exportDTO.setDepartment(departmentDTO==null?null:departmentDTO.getDepartmentName());
            exportDTO.setJob(StringUtils.isEmpty(info.getJobName())?null:info.getJobName().replaceAll("#",","));
            exportDTO.setCreateDate(info.getCreateDate());
            List<UserOrg> list1=this.userOrgMapper.getAccount(info.getId(),UserEnum.platformType.SELLER.getPlatformType());
            if (CollectionUtils.isNotEmpty(list1)){
                List<Integer> emIds=new ArrayList<>();
                list1.forEach(userOrg -> {
                    emIds.add(Integer.valueOf(userOrg.getBindCode()));
                });

                Object object=this.empowerService.getsEmpName(JSONObject.toJSONString(emIds));
                JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
                if (jsonObject.getJSONArray("data")!=null){
                    JSONArray data=jsonObject.getJSONArray("data");
                    for (int i = 0; i <data.size() ; i++) {
                        Integer type=data.getJSONObject(i).getInteger("type");
                        StringBuilder esb=new StringBuilder();
                        for (int j = 0; j < data.getJSONObject(i).getJSONArray("binds").size(); j++) {
                            esb.append(data.getJSONObject(i).getJSONArray("binds").getJSONObject(j).getString("name"));
                            esb.append(";");
                        }
                        switch (type){
                            case 1:
                                exportDTO.setEbay(esb.toString());
                                break;
                            case 2:
                                exportDTO.setAliexpress(esb.toString());
                                break;
                            case 3:
                                exportDTO.setAliexpress(esb.toString());
                                break;
                            case 4:
                                exportDTO.setOther(esb.toString());
                                break;
                            default:
                                continue;
                        }
                    }
                }
            }
            result.add(exportDTO);
        });
        return result;
    }


    /**
     * 分页获取所有卖家账号
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Override
    public PageDTO<ProviderUserDTO> getPageUser(Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage,pageSize);
        List<NewSellerUser> list=this.userMapper.getsName(null);
        PageInfo<NewSellerUser> pageInfo=new PageInfo<>(list);
        PageDTO<ProviderUserDTO> result=new PageDTO<>(pageInfo.getTotal(),currentPage.longValue());
        if (CollectionUtils.isEmpty(pageInfo.getList())){
            return result;
        }
        List<ProviderUserDTO> data=new ArrayList<>(pageSize);
        pageInfo.getList().forEach(info -> {
            ProviderUserDTO dto=new ProviderUserDTO();
            BeanUtils.copyProperties(info,dto);
            dto.setUserId(info.getId());
            data.add(dto);
        });
        result.setList(data);
        return result;
    }

    /**
     * 根据店铺id获取绑定 子账号信息
     *
     * @param storeId
     * @return
     */
    @Override
    public List<ProviderUserDTO> getsUserByStore(Integer storeId) {
        List<Integer> list=this.userOrgMapper.getsByBindCode(UserEnum.platformType.SELLER.getPlatformType(),null,storeId.toString());
        List<NewSellerUser> users=this.userMapper.getsName(JSONArray.parseArray(JSONObject.toJSONString(list),String.class));
        List<ProviderUserDTO> result=new ArrayList<>();
        if (CollectionUtils.isEmpty(users)){
            return result;
        }
        users.forEach( user -> {
            ProviderUserDTO dto=new ProviderUserDTO();
            BeanUtils.copyProperties(user,dto);
            dto.setUserId(user.getId());
            result.add(dto);
        });
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
                    userOrg.setUserPlatform(UserEnum.platformType.SELLER.getPlatformType().byteValue());
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
                role.setPlatformType(UserEnum.platformType.SELLER.getPlatformType().byteValue());
                role.setUserId(userId);
                roleList.add(role);
            }
            this.userRoleMapper.insertBatch(roleList);
        }
        return 1;
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
     * 后台发送卖家入住审核消息
     */
    private void sendCms(){
        JSONObject params=new JSONObject();
        params.put("identify","SELLER_SETTLED");
        params.put("userName","admin");
        params.put("belongSys",UserEnum.platformType.CMS.getPlatformType());
        params.put("num",this.userMapper.getSizeByStatus(UserStatusEnum.NO_AUDIT.getStatus()));
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        this.rabbitTemplate.convertAndSend(RabbitConfig.USER_EXCHAGE, RabbitConfig.CMS_MESSAGE_QUEUE_KEY, params.toString(), correlationId);
    }


}
