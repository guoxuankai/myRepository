package com.brandslink.cloud.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.constant.ConstantAli;
import com.brandslink.cloud.common.constant.CustomerConstant;
import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.entity.CustomerInfoEntity;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.entity.request.CustomerShipperDetailRequestDTO;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.AliSMSUtils;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.PhoneUtils;
import com.brandslink.cloud.common.utils.RedisUtils;
import com.brandslink.cloud.user.dto.request.AddOrUpdateShipperRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerForAuditInfoRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerForBasicInfoRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerSignInRequestDTO;
import com.brandslink.cloud.user.dto.response.*;
import com.brandslink.cloud.user.entity.*;
import com.brandslink.cloud.user.mapper.*;
import com.brandslink.cloud.user.rabbitmq.RabbitConfig;
import com.brandslink.cloud.user.service.ICustomerService;
import com.brandslink.cloud.user.utils.CustomerUtil;
import com.github.pagehelper.PageInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户
 *
 * @ClassName CustomerServiceImpl
 * @Author tianye
 * @Date 2019/7/17 10:08
 * @Version 1.0
 */
@Service
public class CustomerServiceImpl implements ICustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private static final BCryptPasswordEncoder B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Resource
    private GetUserDetailInfoUtil getUserDetailInfoUtil;

    @Resource
    private CustomerInfoMapper mapper;

    @Resource
    private MenuInfoMapper menuInfoMapper;

    @Resource
    private CustomerUserInfoMapper customerUserInfoMapper;

    @Resource
    private CustomerRoleInfoMapper customerRoleInfoMapper;

    @Resource
    private ShipperInfoMapper shipperInfoMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Resource
    private AliSMSUtils aliSMSUtils;

    @Override
    public Page<CustomerInfo> getCustomerList(String customerCode, String customerName, String status, String auditStatus) {
        CustomerInfo info = new CustomerInfo();
        info.setCustomerCode(customerCode);
        info.setChineseName(customerName);
        info.setStatus(status);
        info.setAuditStatus(auditStatus);
        return new Page<>(new PageInfo<>(mapper.page(info)));
    }

    @Override
    public CustomerInfoResponseDTO getCustomer(Integer id) {
        String platformType = getUserDetailInfoUtil.getPlatformType();
        if (StringUtils.equals(platformType, "1")) {
            id = customerUserInfoMapper.selectCustomerIdByPrimaryKey(id);
        }
        CustomerInfoResponseDTO result = new CustomerInfoResponseDTO();
        List<CustomerInfo> customerInfos = mapper.selectCustomerDetailAndWarehouseInfoByPrimaryKey(id);
        BeanUtils.copyProperties(customerInfos.get(0), result);
        result.setWarehouseName(customerInfos.stream().filter(c -> StringUtils.isNotBlank(c.getWarehouseName())).map(CustomerInfo::getWarehouseName).collect(Collectors.joining(",")));
        List<ShipperInfo> shipperInfoList = shipperInfoMapper.selectByCustomerId(id);
        result.setShipperInfoList(shipperInfoList);
        return result;
    }

    @Override
    public void updateCustomerForBasicInfo(CustomerForBasicInfoRequestDTO request) {
        LOGGER.debug("编辑客户基本信息请求体detail：{}", JSON.toJSONString(request));
        String platformType = getUserDetailInfoUtil.getPlatformType();
        if (StringUtils.equals(platformType, "1")) {
            request.setId(customerUserInfoMapper.selectCustomerIdByPrimaryKey(request.getId()));
        }
        PhoneUtils.judgeContactWay(request.getContactWay());
        commonUpdateCustomerInfo(request, request.getId().longValue(), platformType);
        List<RoleInfoResponseDTO.WarehouseDetail> warehouseList = request.getWarehouseList();
        if (CollectionUtils.isNotEmpty(warehouseList)) {
            mapper.deleteWarehouseByCustomerId(request.getId());
            mapper.insertWarehouseByCustomerId(request.getId(), warehouseList);
        }
        sendQueue(request.getCustomerCode(), null, request.getChineseName());
    }

    @Override
    public void updateCustomerForAuditInfo(CustomerForAuditInfoRequestDTO request) {
        LOGGER.debug("编辑客户审核信息请求体detail：{}", JSON.toJSONString(request));
        String platformType = getUserDetailInfoUtil.getPlatformType();
        if (StringUtils.equals(platformType, "1")) {
            request.setId(customerUserInfoMapper.selectCustomerIdByPrimaryKey(request.getId()));
        }
        commonUpdateCustomerInfo(request, request.getId().longValue(), null);
    }

    @Override
    public void commitAuditInfo() {
        Integer id = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo().getCustomerId();
        CustomerInfo customerInfo = new CustomerInfo() {{
            setId(id);
            setAuditStatus("1");
        }};
        commonUpdateCustomerInfo(customerInfo, id.longValue(), null);
    }

    @Override
    public List<CodeAndNameResponseDTO> getShipperCodeAndNameForOMS() {
        Integer customerId = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo().getCustomerId();
        List<ShipperInfo> list = shipperInfoMapper.selectShipperListByCustomerId(customerId);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return list.stream().map(s -> new CodeAndNameResponseDTO() {{
            setDataCode(s.getShipperCode());
            setDataName(s.getShipperName());
        }}).collect(Collectors.toList());
    }

    @Override
    public DeveloperCentreInfoResponseDTO getDeveloperCentreInfo() {
        CustomerInfoEntity infoEntity = getUserDetailInfoUtil.getCustomerDetails().getCustomerInfoEntity();
        return new DeveloperCentreInfoResponseDTO() {{
            setName(infoEntity.getChineseName());
            setCustomerId(infoEntity.getCustomerAppId());
            setToken(infoEntity.getCustomerSecretKey());
        }};
    }

    @Override
    public void updatePhone(String mobile, String authCode) {
        verifyPhone(mobile);
        // 校验短信验证码
        String inputAuthCode = redisUtils.stringGet(CustomerConstant.AUTH_CODE_REDIS_PREFIX + mobile);
        if (!StringUtils.equals(inputAuthCode, authCode)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100716);
        }
        mapper.updatePhone(getUserDetailInfoUtil.getCustomerDetails().getCustomerInfoEntity().getId(), mobile);
    }

    @Override
    public Map<String, String> getHomePageInfo() {
        CustomerInfoEntity customerInfoEntity = getUserDetailInfoUtil.getCustomerDetails().getCustomerInfoEntity();
        return new HashMap<String, String>(2) {{
            put("customerCode", customerInfoEntity.getCustomerCode());
            put("commerce", customerInfoEntity.getCommerce());
        }};
    }

    @Override
    public void updateAudit(Integer id, Integer auditStatus, String auditFailedCause) {
        CustomerInfo customerInfo = mapper.selectByPrimaryKey(id.longValue());
        if (StringUtils.equals(customerInfo.getAuditStatus(), "0")) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100719.getCode(), "客户未提交审核，无法审核!");
        }
        if (StringUtils.isAnyBlank(customerInfo.getLegalRepresentative(), customerInfo.getLegalRepresentativeIdentityCard(),
                customerInfo.getBusinessLicense(), customerInfo.getIdentityCardFront(), customerInfo.getIdentityCardVerso())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100719);
        }
        if (auditStatus == 3 && StringUtils.isBlank(auditFailedCause)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100719.getCode(), "审核不通过时，必须填写不通过原因!");
        }
        CustomerInfo updateCustomer = new CustomerInfo() {{
            setId(id);
            setAuditStatus(String.valueOf(auditStatus));
            if (StringUtils.isNotBlank(auditFailedCause)) {
                setAuditFailedCause(auditFailedCause);
            }
        }};
        mapper.updateByPrimaryKeySelective(updateCustomer);
        // 审核通过后，需要给主账号赋所有权限（主账号角色 -> 如果没有，新建一个主账号角色）
        Integer primaryAccountRoleId = getPrimaryAccountRoleId();
        customerRoleInfoMapper.insertCustomerAccountRole(customerUserInfoMapper.selectPrimaryAccountIdByCustomerId(id), new HashSet<Integer>() {{
            add(primaryAccountRoleId);
        }});
    }

    /**
     * 公共编辑客户信息
     *
     * @param request
     * @param primaryKey
     */
    private void commonUpdateCustomerInfo(Object request, Long primaryKey, String platformType) {
        CustomerInfo customerInfo = new CustomerInfo();
        if (StringUtils.isNotBlank(platformType)) {
            if (StringUtils.equals(platformType, "2")) {
                customerInfo.setLastUpdateTime(new Date());
                customerInfo.setLastUpdateBy("admin-ocms");
            } else if (StringUtils.equals(platformType, "1")) {
                customerInfo.setLastUpdateTime(new Date());
                customerInfo.setLastUpdateBy(getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo().getName());
            }
        }
        BeanUtils.copyProperties(request, customerInfo);
        mapper.updateByPrimaryKeySelective(customerInfo);
        // 更新Redis
        CustomerInfoEntity entity = new CustomerInfoEntity();
        customerInfo = mapper.selectByPrimaryKey(primaryKey);
        BeanUtils.copyProperties(customerInfo, entity);
        redisUtils.set(CustomerConstant.REDIS_PREFIX + customerInfo.getCustomerAppId(), entity);
    }

    @Override
    public void addShipper(AddOrUpdateShipperRequestDTO request) {
        LOGGER.debug("添加货主请求体detail：{}", JSON.toJSONString(request));
        String platformType = getUserDetailInfoUtil.getPlatformType();
        String userName = getUserName(platformType);
        if (StringUtils.equals(platformType, "1")) {
            request.setCustomerId(customerUserInfoMapper.selectCustomerIdByPrimaryKey(request.getCustomerId()));
        }
        Integer customerId = request.getCustomerId();
        ShipperInfo shipperOfCode = shipperInfoMapper.selectByShipperCode(request.getShipperCode(), customerId);
        if (shipperOfCode != null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100709);
        }
        judgeShipperName(request.getShipperName(), customerId, null);
        PhoneUtils.judgeContactWay(request.getContactWay());
        Date date = new Date();
        ShipperInfo shipperInfo = new ShipperInfo(request);
        shipperInfo.setId(null);
        shipperInfo.setCreateBy(userName);
        shipperInfo.setCreateTime(date);
        shipperInfoMapper.insertSelective(shipperInfo);
        mapper.insertCustomerShipper(customerId, shipperInfo.getId());
    }

    @Override
    public void updateShipper(AddOrUpdateShipperRequestDTO request) {
        LOGGER.debug("编辑货主信息请求体detail：{}", JSON.toJSONString(request));
        String platformType = getUserDetailInfoUtil.getPlatformType();
        String userName = getUserName(platformType);
        if (StringUtils.equals(platformType, "1")) {
            request.setCustomerId(customerUserInfoMapper.selectCustomerIdByPrimaryKey(request.getCustomerId()));
        }
        judgeShipperName(request.getShipperName(), request.getCustomerId(), request.getId());
        PhoneUtils.judgeContactWay(request.getContactWay());
        Date date = new Date();
        ShipperInfo shipperInfo = new ShipperInfo() {{
            setLastUpdateBy(userName);
            setLastUpdateTime(date);
        }};
        BeanUtils.copyProperties(request, shipperInfo);
        shipperInfoMapper.updateByPrimaryKeySelective(shipperInfo);
        String customerCode = mapper.selectCustomerCodeByCustomerId(request.getCustomerId());
        sendQueue(customerCode, request.getShipperCode(), request.getShipperName());
    }

    /**
     * 根据登录系统获取当前登录用户名
     *
     * @param platformType
     * @return
     */
    private String getUserName(String platformType) {
        String result = StringUtils.EMPTY;
        if (StringUtils.equals(platformType, "1")) {
            result = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo().getName();
        } else if (StringUtils.equals(platformType, "0")) {
            result = getUserDetailInfoUtil.getUserDetailInfo().getName();
        }
        return result;
    }

    @Override
    public List<CustomerShipperDetailResponseDTO> getCustomerShipperDetail(List<CustomerShipperDetailRequestDTO> list) {
        LOGGER.info("获取客户名称以及货主名称请求体 details：{}", JSON.toJSONString(list));
        List<CustomerShipperDetailResponseDTO> result = new ArrayList<>();
        List<String> customerCodeList = new ArrayList<>();
        Iterator<CustomerShipperDetailRequestDTO> iterator = list.iterator();
        while (iterator.hasNext()) {
            CustomerShipperDetailRequestDTO next = iterator.next();
            if (StringUtils.isBlank(next.getShipperCode())) {
                customerCodeList.add(next.getCustomerCode());
                iterator.remove();
            }
        }
        if (CollectionUtils.isNotEmpty(customerCodeList)) {
            result = mapper.selectCustomerChineseNamesByCustomerCodes(customerCodeList);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            result.addAll(mapper.selectShipperNamesByCustomerCodeAndShipperCodes(list));
        }
        return result;
    }

    @Override
    public List<CodeAndNameResponseDTO> getCustomerCodeAndName() {
        List<CustomerInfo> list = mapper.selectAll();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return list.stream().map(s -> new CodeAndNameResponseDTO() {{
            this.setDataCode(s.getCustomerCode());
            this.setDataName(s.getChineseName());
        }}).collect(Collectors.toList());
    }

    @Override
    public List<CodeAndNameResponseDTO> getShipperCodeAndName(String customerCode) {
        List<ShipperInfo> list = shipperInfoMapper.selectAll(customerCode);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return list.stream().map(s -> new CodeAndNameResponseDTO() {{
            this.setDataCode(s.getShipperCode());
            this.setDataName(s.getShipperName());
        }}).collect(Collectors.toList());
    }

    @Override
    public List<CodeAndNameResponseDTO> getShipperDetail(List<String> shipperCodeList) {
        if (CollectionUtils.isEmpty(shipperCodeList)) {
            return new ArrayList<>(0);
        }
        List<ShipperInfo> list = shipperInfoMapper.selectByShipperCodeList(shipperCodeList);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return list.stream().map(s -> new CodeAndNameResponseDTO() {{
            this.setDataCode(s.getShipperCode());
            this.setDataName(s.getShipperName());
        }}).collect(Collectors.toList());
    }

    @Override
    public List<ShipperInfo> getShipperByCustomerId(Integer customerId) {
        List<ShipperInfo> list = shipperInfoMapper.selectShipperListByCustomerId(customerId);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return list;
    }

    @Override
    public CustomerInfoResponseDTO getCustomerByCustomerCode(String customerCode) {
        CustomerInfoResponseDTO result = new CustomerInfoResponseDTO();
        CustomerInfo customerInfo = mapper.selectByCustomerCode(customerCode);
        if (null != customerInfo) {
            BeanUtils.copyProperties(customerInfo, result);
            List<ShipperInfo> shipperInfoList = shipperInfoMapper.selectByCustomerId(customerInfo.getId());
            if (CollectionUtils.isNotEmpty(shipperInfoList)) {
                result.setShipperInfoList(shipperInfoList);
            }
        }
        return result;
    }

    @Override
    public String insertSignIn(CustomerSignInRequestDTO request) {
        LOGGER.debug("客户注册请求体detail：{}", JSON.toJSONString(request));
        String contactWay = request.getContactWay();
        PhoneUtils.judgeContactWay(contactWay);
        // 校验手机号是否已经注册
        CustomerUserInfo userInfo = customerUserInfoMapper.selectByContactWay(contactWay);
        if (null != userInfo) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100715.getCode(), "手机号：" + contactWay + "，已注册账户，请直接登录!");
        }
        // 校验短信验证码
        String inputAuthCode = request.getAuthCode();
        String authCode = redisUtils.stringGet(CustomerConstant.AUTH_CODE_REDIS_PREFIX + contactWay);
        if (!StringUtils.equals(inputAuthCode, authCode)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100716);
        }
        String password = B_CRYPT_PASSWORD_ENCODER.encode(request.getPassword());
        String name = request.getName();
        CustomerInfo customerInfo = new CustomerInfo() {{
            String customerCode = StringUtils.EMPTY;
            // 判断客户编码是否存在
            boolean flag = true;
            while (flag) {
                customerCode = CustomerUtil.createAccount(null);
                CustomerInfo info = mapper.selectByCustomerCode(customerCode);
                if (null == info) {
                    flag = false;
                }
            }
            setStatus("1");
            setAuditStatus("0");
            setCreateTime(new Date());
            setCustomerCode(customerCode);
            setShortenedChineseName(request.getShortenedName());
            setContacts(name);
            setContactWay(contactWay);
            String customerAppId = CustomerUtil.getCustomerIdByUUId();
            setCustomerAppId(customerAppId);
            String secretKey = DigestUtils.md5Hex(StringUtils.join(JSON.toJSONString(request), "-", System.currentTimeMillis()));
            setCustomerSecretKey(secretKey);
            setCreateBy(name);
        }};
        String account = getAccount();
        customerInfo.setAccount(account);
        mapper.insertSelective(customerInfo);
        createPrimaryAccountInfo(account, customerInfo.getId(), password, contactWay, name);
        return account;
    }

    @Override
    public void getAuthCode(String mobile, Integer type) {
        PhoneUtils.judgeContactWay(mobile);
        String code = CustomerUtil.createRandomNumeric();
        LOGGER.info("获取短信验证码，验证码：{}，手机号：{}，类型：{}", code, mobile, type);
        ConstantAli.SmsType smsType;
        switch (type) {
            case 0:
                smsType = ConstantAli.SmsType.OMS_CHANGE_MOBILE_CODE;
                break;
            case 1:
                smsType = ConstantAli.SmsType.OMS_MOBILE_LOGIN_CODE;
                break;
            case 2:
                smsType = ConstantAli.SmsType.OMS_CUSTOMER_SIGN_IN_CODE;
                break;
            default:
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100005);
        }
        aliSMSUtils.sendSms(mobile, code, smsType);
        redisUtils.stringSet(CustomerConstant.AUTH_CODE_REDIS_PREFIX + mobile, code, UserConstant.REDIS_AUTH_CODE_KEY_TIMEOUT);
    }

    @Override
    public void addCustomer(CustomerForBasicInfoRequestDTO request) {
        LOGGER.debug("添加客户请求体detail：{}", JSON.toJSONString(request));
        CustomerInfo customer = mapper.selectByCustomerCode(request.getCustomerCode());
        if (customer != null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100708);
        }
        String contactWay = request.getContactWay();
        verifyPhone(contactWay);
        Date date = new Date();
        CustomerInfo customerInfo = new CustomerInfo() {{
            setCreateBy("admin-ocms");
            setCreateTime(date);
            setAuditStatus("1");
        }};
        String customerAppId = CustomerUtil.getCustomerIdByUUId();
        customerInfo.setCustomerAppId(customerAppId);
        String secretKey = DigestUtils.md5Hex(StringUtils.join(JSON.toJSONString(request), "-", System.currentTimeMillis()));
        customerInfo.setCustomerSecretKey(secretKey);
        BeanUtils.copyProperties(request, customerInfo);
        String account = getAccount();
        customerInfo.setAccount(account);
        mapper.insertSelective(customerInfo);
        createPrimaryAccountInfo(account, customerInfo.getId(), B_CRYPT_PASSWORD_ENCODER.encode(UserConstant.INITIAL_PASSWORD), contactWay, request.getContacts());
        CustomerInfoEntity entity = new CustomerInfoEntity();
        BeanUtils.copyProperties(customerInfo, entity);
        redisUtils.set(CustomerConstant.REDIS_PREFIX + customerAppId, entity);
        List<RoleInfoResponseDTO.WarehouseDetail> warehouseList = request.getWarehouseList();
        if (CollectionUtils.isNotEmpty(warehouseList)) {
            mapper.insertWarehouseByCustomerId(customerInfo.getId(), warehouseList);
        }
    }

    /**
     * 校验手机号是否可用
     *
     * @param phone
     */
    private void verifyPhone(String phone) {
        PhoneUtils.judgeContactWay(phone);
        // 校验手机号是否已经注册
        CustomerUserInfo userInfo = customerUserInfoMapper.selectByContactWay(phone);
        if (null != userInfo) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100715);
        }
    }

    /**
     * 创建客户主账号
     *
     * @param account
     * @param customerId
     * @param password
     * @param contactWay
     * @param name
     */
    private void createPrimaryAccountInfo(String account, Integer customerId, String password, String contactWay, String name) {
        CustomerUserInfo customerUserInfo = new CustomerUserInfo() {{
            setType(0);
            setEnabled(0);
            setAccount(account);
            setCustomerId(customerId);
            setPassword(password);
            setContactPhone(contactWay);
            setCreateBy(name);
            setName(name);
            setCreateTime(new Date());
        }};
        customerUserInfoMapper.insertSelective(customerUserInfo);
        Integer finalInitializeRoleId = getInitializeRoleId();
        customerRoleInfoMapper.insertCustomerAccountRole(customerUserInfo.getId(), new HashSet<Integer>() {{
            add(finalInitializeRoleId);
        }});
    }

    /**
     * 获取初始化角色id
     *
     * @return
     */
    private Integer getInitializeRoleId() {
        // 主账号默认仅能查看首页（待定）和 基础数据->我的资料
        Integer initializeRoleId = customerRoleInfoMapper.selectInitializeRoleId(0);
        if (initializeRoleId == null) {
            CustomerRoleInfo roleInfo = new CustomerRoleInfo() {{
                setCustomerId(0);
                setRoleName("初始化角色");
                setRoleDescription("客户注册时生成主账号默认的角色(唯一)");
                setCreateTime(new Date());
                setCreateBy("admin");
            }};
            customerRoleInfoMapper.insertSelective(roleInfo);
            initializeRoleId = roleInfo.getId();
        }
        List<Integer> menus = customerRoleInfoMapper.selectMenusByRoleId(initializeRoleId, 0);
        if (CollectionUtils.isEmpty(menus)) {
            // 如果初始化角色没有关联菜单，则关联首页（待定）和 基础数据->我的资料
            List<Integer> homeMenuIds = menuInfoMapper.selectHomeMenuIds(2);
            if (CollectionUtils.isNotEmpty(homeMenuIds)) {
                customerRoleInfoMapper.insertMenusByRoleId(initializeRoleId, homeMenuIds, 0);
            }
        }
        return initializeRoleId;
    }

    /**
     * 获取主账号角色id
     *
     * @return
     */
    private Integer getPrimaryAccountRoleId() {
        // 主账号默认仅能查看首页（待定）和 基础数据->我的资料
        Integer initializeRoleId = customerRoleInfoMapper.selectInitializeRoleId(-1);
        if (initializeRoleId == null) {
            CustomerRoleInfo roleInfo = new CustomerRoleInfo() {{
                setCustomerId(-1);
                setRoleName("主账号角色");
                setRoleDescription("审核通过时创建主账号默认的角色(唯一)");
                setCreateTime(new Date());
                setCreateBy("admin");
            }};
            customerRoleInfoMapper.insertSelective(roleInfo);
            initializeRoleId = roleInfo.getId();
        }
        List<Integer> menus = customerRoleInfoMapper.selectMenusByRoleId(initializeRoleId, 0);
        if (CollectionUtils.isEmpty(menus)) {
            // 如果主账号角色没有关联菜单，则关联oms系统所有菜单（主账号拥有所有菜单权限）
            List<MenuInfo> menuInfoList = menuInfoMapper.selectAll(2);
            if (CollectionUtils.isNotEmpty(menuInfoList)) {
                customerRoleInfoMapper.insertMenusByRoleId(initializeRoleId, menuInfoList.stream().map(MenuInfo::getId).collect(Collectors.toList()), 0);
            }
        }
        return initializeRoleId;
    }

    /**
     * 生成主账号
     *
     * @return
     */
    private String getAccount() {
        //生成主账号
        String account = StringUtils.EMPTY;
        // 判断账号是否存在
        boolean flag = true;
        while (flag) {
            account = CustomerUtil.createAccount("PL");
            CustomerUserInfo info = customerUserInfoMapper.selectByAccount(account);
            if (null == info) {
                flag = false;
            }
        }
        return account;
    }

    /**
     * 发布客户更新广播队列
     *
     * @param customerCode
     * @param shipperCode
     * @param updateName
     */
    private void sendQueue(String customerCode, String shipperCode, String updateName) {
        Map<String, String> map = new HashMap<>();
        map.put("customerCode", customerCode);
        map.put("shipperCode", shipperCode);
        map.put("updateName", updateName);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_CUSTOMER, "", JSON.toJSONString(map));
    }

    /**
     * 校验货主名称是否存在
     *
     * @param shipperName
     * @param customerId
     */
    private void judgeShipperName(String shipperName, Integer customerId, Integer shipperId) {
        ShipperInfo shipperOfName = shipperInfoMapper.selectByShipperName(shipperName, customerId, shipperId);
        if (shipperOfName != null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100710);
        }
    }

    /**
     * 校验客户名称是否存在
     *
     * @param customerName
     * @param id
     */
    private void judgeCustomerName(String customerName, Integer id) {
        CustomerInfo customer = mapper.selectByCustomerName(customerName, id);
        if (customer != null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100711);
        }
    }
}
