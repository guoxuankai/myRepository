package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.supplier.dto.*;
import com.rondaful.cloud.supplier.entity.*;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.mapper.VatDetailInfoMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseAddresserInfoMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseWarrantDetailMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseWarrantMapper;
import com.rondaful.cloud.supplier.service.IWareHouseService;
import com.rondaful.cloud.supplier.service.IWarehouseWarrantService;
import com.rondaful.cloud.supplier.vo.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 入库单
 *
 * @ClassName WarehouseWarrantServiceImpl
 * @Author tianye
 * @Date 2019/4/25 18:58
 * @Version 1.0
 */
@Service
public class WarehouseWarrantServiceImpl implements IWarehouseWarrantService {

    private final static Logger log = LoggerFactory.getLogger(WarehouseWarrantServiceImpl.class);

    /**
     * 日期格式
     */
    private final static String FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 文件类型
     */
    private final static String FILE_TYPE = "type";
    /**
     * base64转码
     */
    private final static String FILE_BASE64 = "base64";
    /**
     * 谷仓创建入库单service
     */
    private final static String SERVICE_NAME_CREATE_GRN = "createGRN";
    /**
     * 谷仓编辑入库单service
     */
    private final static String SERVICE_NAME_MODIFY_GRN = "modifyGRN";
    /**
     * 谷仓获取中转仓库service
     */
    private final static String SERVICE_NAME_TRANSFER_WAREHOUSE = "getTransferWarehouse";
    /**
     * 谷仓获取中转服务方式service
     */
    private final static String SERVICE_NAME_SM_CODE = "getSmCode";
    /**
     * 谷仓获取入库单列表service
     */
    private final static String SERVICE_NAME_GRN_LIST = "getGRNList";
    /**
     * 谷仓废弃入库单service
     */
    private final static String SERVICE_NAME_DEL_GRN = "delGRN";
    /**
     * 谷仓获取进出口商列表service
     */
    private static final String SERVICE_NAME_GET_VAT_LIST = "getVatList";
    /**
     * 谷仓打印箱唛service
     */
    private static final String SERVICE_NAME_DEL_ASN_BOX = "printGcReceivingBox";
    /**
     * 谷仓打印sku标签service
     */
    private static final String SERVICE_NAME_PRINT_SKU = "printSku";
    /**
     * 谷仓不传参数时传的属性
     */
    private final static String GRANARY_REQUEST = "{}";
    /**
     * 调用谷仓接口失败标识
     */
    private final static String GRANARY_FLAG_FAILURE = "Failure";

    @Value("${wsdl.url}")
    private String url;

    @Autowired
    private WarehouseWarrantMapper warehouseWarrantMapper;

    @Autowired
    private WarehouseWarrantDetailMapper warrantDetailMapper;

    @Autowired
    private WarehouseAddresserInfoMapper addresserInfoMapper;

    @Autowired
    private VatDetailInfoMapper vatDetailInfoMapper;

    @Autowired
    GranaryUtils granaryUtils;

    @Autowired
    private IWareHouseService wareHouseService;

    @Override
    public Page<WarehouseWarrantResponse> getWarehouseWarrantListBySelective(WarehouseWarrantRequest request) {
        WareHouseSearchVO vo = new WareHouseSearchVO();
        vo.setWareHouseStatus(1);
        List<WareHouseServiceProviderDTO> serviceProviderList = wareHouseService.getServiceProviderList(vo, true);
        // 获取所有可用目的仓代码
        List<String> wareHouseCodeList = new ArrayList<>();
        serviceProviderList.get(0).getAuthorizeList().forEach(s -> wareHouseCodeList.addAll(s.getWareHouseDetailDTOList().stream().map(WareHouseDetailDTO::getWareHouseCode).collect(Collectors.toList())));
        if (CollectionUtils.isEmpty(wareHouseCodeList)) {
            log.info("授权的目的仓代码为空，不显示任何数据!");
            return new Page<>(new PageInfo<>());
        }
        request.setWareHouseCodeList(wareHouseCodeList);
        // 查询入库单列表
        Page.builder(request.getPage(), request.getRow());
        List<WarehouseWarrantResponse> list = warehouseWarrantMapper.selectWarehouseWarrantListBySelective(request);

        PageInfo<WarehouseWarrantResponse> pageInfo = new PageInfo<>(list);
        return new Page<>(pageInfo);
    }

    @Override
    public WarehouseWarrantDetailResponseVo getWarehouseWarrantDetailByPrimaryKey(Long primaryKey) {
        WarehouseWarrantDetailResponseVo responseVo = new WarehouseWarrantDetailResponseVo();
        List<ProductInfoVo> productInfoVoList = new ArrayList<>();
        // 查询入库单明细
        WarehouseWarrant warehouseWarrant = warehouseWarrantMapper.selectByPrimaryKey(primaryKey);
        String sequenceNumber = warehouseWarrant.getSequenceNumber();
        // 查询商品明细
        List<WarehouseWarrantDetail> detailList = warrantDetailMapper.selectByParentSequenceNumber(sequenceNumber);
        detailList.forEach(d -> {
            ProductInfoVo infoVo = new ProductInfoVo();
            infoVo.setBoxNo(d.getBoxNo());
            infoVo.setQuantityShipped(d.getQuantityShipped());
            infoVo.setSystemSku(d.getProductSku());
            infoVo.setCommodityNameCn(d.getProductCnName());
            infoVo.setCommodityNameEn(d.getProductEnName());
            productInfoVoList.add(infoVo);
        });
        responseVo.setProductInfos(productInfoVoList);
        try {
            if (warehouseWarrant.getCollectingService() == 1) {
                // 查询揽收信息
                WarehouseAddresserInfo addresserInfo = addresserInfoMapper.selectByParentSequenceNumber(sequenceNumber);
                BeanUtils.copyProperties(responseVo, addresserInfo);
            }
            BeanUtils.copyProperties(responseVo, warehouseWarrant);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("类型转换失败!");
            e.printStackTrace();
        }
        return responseVo;
    }

    @Override
    public void insertWarrant(WarehouseWarrantVo request, UserInfoVO userInfo) {
        // 通过目的仓代码获取对应账户的token和key
        AuthorizeDTO authorizeDTO = getAuthorizeDTO(request.getWarehouseCode());
        String token = authorizeDTO.getAppToken();
        String key = authorizeDTO.getAppKey();
        // 创建入库单
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        log.info("生成UUID:{}", uuid);
        // 入库单标准信息
        WarehouseWarrant warehouseWarrant = createStandardModel(userInfo);
        commonWarehouseWarrantSave(false,token, key, warehouseWarrant, request, uuid, SERVICE_NAME_CREATE_GRN);
    }

    @Override
    public void commitWarrant(Long primaryKey) {
        WarehouseWarrant warehouseWarrant = warehouseWarrantMapper.selectByPrimaryKey(primaryKey);
        String sequenceNumber = warehouseWarrant.getSequenceNumber();
        WarehouseAddresserInfo addresserInfo = addresserInfoMapper.selectByParentSequenceNumber(sequenceNumber);
        List<WarehouseWarrantDetail> detailList = warrantDetailMapper.selectByParentSequenceNumber(sequenceNumber);
        GranaryCreateWarehouseWarrantRequest granaryWarrantRequest = mapGranaryWarrantRequest(warehouseWarrant, detailList, addresserInfo);
        granaryWarrantRequest.setVerify(1);
        log.info("查询入库单列表时提交谷仓创建入库单，requestDetail:{}", JSON.toJSONString(granaryWarrantRequest));
        // 通过目的仓代码获取对应账户的token和key
        AuthorizeDTO authorizeDTO = getAuthorizeDTO(warehouseWarrant.getWarehouseCode());
        String token = authorizeDTO.getAppToken();
        String key = authorizeDTO.getAppKey();
        updateWarehouseWarrant(true, token, key, granaryWarrantRequest, sequenceNumber, SERVICE_NAME_MODIFY_GRN);
    }

    @Override
    public List<GranaryTransferWarehouseResponse.TransferWarehouseDeatil> getTransferWarehouse(String warehouseCode) {
        // 通过目的仓代码获取对应账户的token和key
        AuthorizeDTO authorizeDTO = getAuthorizeDTO(warehouseCode);
        String token = authorizeDTO.getAppToken();
        String key = authorizeDTO.getAppKey();
        String callResponse = callGranaryWarrantRequest(token, key, SERVICE_NAME_TRANSFER_WAREHOUSE);
        GranaryTransferWarehouseResponse response = JSON.parseObject(callResponse, GranaryTransferWarehouseResponse.class);
        judgeReturnResult(SERVICE_NAME_TRANSFER_WAREHOUSE, response);
        return response.getData();
    }

    @Override
    public GranarySmCodeResponse.SmCodeData getSmCode(String warehouseCode) {
        // 通过目的仓代码获取对应账户的token和key
        AuthorizeDTO authorizeDTO = getAuthorizeDTO(warehouseCode);
        String token = authorizeDTO.getAppToken();
        String key = authorizeDTO.getAppKey();
        String callResponse = callGranaryWarrantRequest(token, key, SERVICE_NAME_SM_CODE);
        GranarySmCodeResponse response = JSON.parseObject(callResponse, GranarySmCodeResponse.class);
        judgeReturnResult(SERVICE_NAME_SM_CODE, response);
        return response.getData();
    }

    @Override
    public void updateWarrant(ModifyWarehouseWarrantVo request, UserInfoVO userInfo) {
        // 添加入库单表
        WarehouseWarrant warrant = new WarehouseWarrant();
        // 根据主键查询序号、创建账户、创建时间、供应商id、供应商名称
        WarehouseWarrant warehouseWarrant = warehouseWarrantMapper.selectValidColumnByPrimaryKey(request.getId());
        String uuid = warehouseWarrant.getSequenceNumber();
        String createBy = warehouseWarrant.getCreateBy();
        Date creatTime = warehouseWarrant.getCreatTime();
        String supplier = warehouseWarrant.getSupplier();
        Integer supplierId = warehouseWarrant.getSupplierId();
        String receivingCode = warehouseWarrant.getReceivingCode();
        // 删除当前入库单
        deleteWarehouseWarrantDetail(request.getId());
        // 添加修改人信息
        warrant.setLastUpdateBy(userInfo.getLoginName());
        warrant.setUpdateTime(new Date());
        warrant.setCreateBy(createBy);
        warrant.setCreatTime(creatTime);
        warrant.setSupplier(supplier);
        warrant.setSupplierId(supplierId);
        warrant.setReceivingCode(receivingCode);
        // 添加入库单基本信息
        WarehouseWarrantVo warrantVo = new WarehouseWarrantVo();
        try {
            // 类替换
            BeanUtils.copyProperties(warrantVo, request);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("类转换失败");
            e.printStackTrace();
        }
        // 通过目的仓代码获取对应账户的token和key
        AuthorizeDTO authorizeDTO = getAuthorizeDTO(request.getWarehouseCode());
        String token = authorizeDTO.getAppToken();
        String key = authorizeDTO.getAppKey();
        // 重新插入编辑后的入库单信息
        commonWarehouseWarrantSave(true,token, key, warrant, warrantVo, uuid, SERVICE_NAME_MODIFY_GRN);
    }

    @Override
    public void deleteWarehouseWarrantDetail(Long primaryKey) {
        String sequenceNumber = warehouseWarrantMapper.selectValidColumnByPrimaryKeyOnSequenceNumber(primaryKey);
        warehouseWarrantMapper.deleteByPrimaryKey(primaryKey);
        warrantDetailMapper.deleteByParentSequenceNumber(sequenceNumber);
        addresserInfoMapper.deleteByParentSequenceNumber(sequenceNumber);
    }

    @Override
    public void editorsComment(Long primaryKey, String comment) {
        warehouseWarrantMapper.updateCommentByPrimaryKey(primaryKey, comment);
    }

    @Override
    public void deleteByDiscardWarehouseWarrant(Long primaryKey) {
        WarehouseWarrant warehouseWarrant = warehouseWarrantMapper.selectByPrimaryKey(primaryKey);
        Map<String, String> map = new HashMap<>();
        map.put("receiving_code", warehouseWarrant.getReceivingCode());
        // 通过目的仓代码获取对应账户的token和key
        AuthorizeDTO authorizeDTO = getAuthorizeDTO(warehouseWarrant.getWarehouseCode());
        String token = authorizeDTO.getAppToken();
        String key = authorizeDTO.getAppKey();
        String response = callGranaryWarrantRequest(token, key, map, SERVICE_NAME_DEL_GRN);
        GranaryCreateWarehouseWarrantResponse warehouseWarrantResponse = JSON.parseObject(response, GranaryCreateWarehouseWarrantResponse.class);
        judgeReturnResult(SERVICE_NAME_DEL_GRN, warehouseWarrantResponse);
        warehouseWarrantMapper.updateWarrantStatusByReceivingCode(warehouseWarrantResponse.getData().getReceivingCode(), Byte.valueOf("4"));
    }

    @Override
    public Map<String, List<VatDetailInfo>> getVatList(String warehouseCode) {
        final String status = "2";
        final String warehouseCodeFinal = warehouseCode;
        // 处理目的仓代码
        warehouseCode = splitWarehouseCode(warehouseCode);
        // 获取所有可用进出口商列表
        List<VatDetailInfo> infoList = vatDetailInfoMapper.selectBycvStatus(status);
        String finalWarehouseCode = warehouseCode;
        // 筛选出目的仓代码的进口商列表
        List<VatDetailInfo> collect = infoList.stream().filter(v -> v.getVatType() == 1 && finalWarehouseCode.equals(v.getWarehouseCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            // 通过目的仓代码获取对应账户的token和key
            AuthorizeDTO authorizeDTO = getAuthorizeDTO(warehouseCodeFinal);
            String token = authorizeDTO.getAppToken();
            String key = authorizeDTO.getAppKey();
            // 调用谷仓API查询进出口商列表
            String response = callGranaryWarrantRequest(token, key, SERVICE_NAME_GET_VAT_LIST);
            GranaryVatListResponse vatListResponse = JSON.parseObject(response, GranaryVatListResponse.class);
            // 添加数据库
            List<VatDetailInfo> list = new ArrayList<>();
            List<GranaryVatListResponse.VatDetail> detailList = vatListResponse.getData();
            detailList.forEach(d -> {
                VatDetailInfo vatDetailInfo = new VatDetailInfo();
                try {
                    BeanUtils.copyProperties(vatDetailInfo, d);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("类转换失败");
                    e.printStackTrace();
                }
                // 映射 营业执照/商业登记书 和 增值税证明文件，因转码后字符串较大，暂不存数据库
//            vatDetailInfo.setGstCertificateFile(d.getGstCertificate().getFile());
//            vatDetailInfo.setGstCertificateFileType(d.getGstCertificate().getFileType());
//            vatDetailInfo.setImporterCompanyLicenceFile(d.getImporterCompanyLicence().getFile());
//            vatDetailInfo.setImporterCompanyLicenceFileType(d.getImporterCompanyLicence().getFileType());
                list.add(vatDetailInfo);
            });
            if (CollectionUtils.isNotEmpty(list)) {
                // 更新数据库
                vatDetailInfoMapper.deleteFrom();
                vatDetailInfoMapper.insertList(list);
                // 重新再查再查一次
                infoList = vatDetailInfoMapper.selectBycvStatus(status);
            }
        }
        Map<String, List<VatDetailInfo>> map = new HashMap<>();
        List<VatDetailInfo> importerList = infoList.stream().filter(l -> l.getVatType() == 1 && finalWarehouseCode.equals(l.getWarehouseCode())).collect(Collectors.toList());
        List<VatDetailInfo> exporterList = infoList.stream().filter(l -> l.getVatType() == 2).collect(Collectors.toList());
        map.put("importerList", importerList);
        map.put("exporterList", exporterList);
        return map;
    }


    @Override
    public void printGcReceivingBox(Long primaryKey, HttpServletResponse response, String receivingCode, String printSize, String printType, String[] arr) {
        Map<String, Object> map = new HashMap<>();
        map.put("receiving_code", receivingCode);
        map.put("print_size", printSize);
        map.put("print_type", printType);
        map.put("receiving_box_no_arr", arr);
        CommonPrintMethod(primaryKey, response, map, SERVICE_NAME_DEL_ASN_BOX);
    }

    @Override
    public void printSku(Long primaryKey, HttpServletResponse response, String printSize, String printCode, String[] arr) {
        Map<String, Object> map = new HashMap<>();
        map.put("print_size", printSize);
        map.put("print_code", printCode);
        map.put("product_sku_arr", arr);
        CommonPrintMethod(primaryKey, response, map, SERVICE_NAME_PRINT_SKU);
    }

    @Override
    public void syncWarrantStatus() {
        // 只取需要更新状态的入库单单号（1:审核中和2:待收货和0：草稿状态）
        List<String> receivingCodeList = warehouseWarrantMapper.selectReceivingCodeByReceivingStatus();
        if (CollectionUtils.isEmpty(receivingCodeList)) {
            log.info("没有需要更新状态的入库单");
            return;
        }
        receivingCodeList = receivingCodeList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        int size = receivingCodeList.size();
        log.info("需要更新状态的入库单数量为：{}", size);
        int count = 1;
        if (size > 100) {
            count = size % 100 == 0 ? size / 100 : size / 100 + 1;
        }
        for (int i = 0; i < count; i++) {
            // 每次截取100条数据
            List<String> list = receivingCodeList.stream().skip(i * 100).limit(100).collect(Collectors.toList());
            // 获取谷仓账号数量
            WareHouseSearchVO vo = new WareHouseSearchVO();
            vo.setWareHouseStatus(1);
            List<WareHouseServiceProviderDTO> serviceProviderList = wareHouseService.getServiceProviderList(vo, true);
            List<WareHouseAuthorizeDTO> authorizeList = serviceProviderList.get(0).getAuthorizeList();
            // 用谷仓所有账号都去更新一次入库单状态
            authorizeList.forEach(a -> {
                String token = a.getAppToken();
                String key = a.getAppKey();
                GranaryQueryWarehouseWarrantListRequest warrantListRequest = new GranaryQueryWarehouseWarrantListRequest(list);
                String callResponse = callGranaryWarrantRequest(token, key, warrantListRequest, SERVICE_NAME_GRN_LIST);
                GranaryQueryWarehouseWarrantListResponse response = JSON.parseObject(callResponse, GranaryQueryWarehouseWarrantListResponse.class);
                judgeReturnResult(SERVICE_NAME_GRN_LIST, response);
                // 过滤状态为审核中的入库单，然后获取对应的状态以及入库单号
                Map<String, Integer> map = new HashMap<>();
                response.getData().stream().filter(r -> r.getReceivingStatus() != 1).collect(Collectors.toList()).forEach(r -> map.put(r.getReceivingCode(), r.getReceivingStatus()));
                if (MapUtils.isNotEmpty(map)) {
                    log.info("入库单映射前detail：{}", map.toString());
                    // 入库单状态映射
                    Map<String, Integer> updateMap = new HashMap<>();
                    map.forEach((k, v) -> {
                        if (v == 10) {
                            // 已入库，上架完成
                            updateMap.put(k, 3);
                        } else if (v == 2) {
                            // 审核不通过， 异常
                            updateMap.put(k, 5);
                        } else if (v == 100) {
                            // 已取消， 废弃
                            updateMap.put(k, 4);
                        } else if (v == 1) {
                            // 审核中， 待审核
                            updateMap.put(k, 1);
                        } else {
                            updateMap.put(k, 2);
                        }
                    });
                    log.info("入库单映射后detail：{}", updateMap.toString());
                    // 更新品连入库单状态
                    warehouseWarrantMapper.updateReceivingStatusByReceivingCodeList(updateMap);
                }
            });
        }
    }

    /**
     * 通过目的仓代码查询对应谷仓账号信息（APPtoken，AppKey）
     *
     * @param wareHouseCode
     * @return
     */
    private AuthorizeDTO getAuthorizeDTO(String wareHouseCode) {
        List<AuthorizeDTO> code = wareHouseService.getAuthorizeByWareHouseCode(Collections.singletonList(wareHouseCode));
        return code.get(0);
    }

    /**
     * 公共打印
     *
     * @param primaryKey
     * @param response
     * @param map
     * @param serviceName
     */
    private void CommonPrintMethod(Long primaryKey, HttpServletResponse response, Map<String, Object> map, String serviceName) {
        // 生成字母加数字组成的随机字符串
        String fileName = RandomStringUtils.randomAlphanumeric(12);
        // 通过目的仓代码获取对应账户的token和key
        AuthorizeDTO authorizeDTO = getAuthorizeDTO(warehouseWarrantMapper.selectByPrimaryKey(primaryKey).getWarehouseCode());
        String token = authorizeDTO.getAppToken();
        String key = authorizeDTO.getAppKey();
        String callResponse = callGranaryWarrantRequest(token, key, map, serviceName);
        GranaryPrintAsnBoxResponse boxResponse = JSON.parseObject(callResponse, GranaryPrintAsnBoxResponse.class);
        judgeReturnResult(serviceName, boxResponse);
        String type = boxResponse.getData().getImageType();
        String fileBase64 = boxResponse.getData().getLabelImage();
        try {
            //Base64解码，replaceAll：去掉无用的空格，否则会导致解码的文件不可用
            byte[] b = new BASE64Decoder().decodeBuffer(fileBase64.replaceAll(" ", ""));
            if (StringUtils.equals(type, "png")) {
                // 设置下载文件类型
                response.setHeader("content-Type", "image/png");
                // 设置下载文件名称后缀
                fileName = fileName.concat(".png");
            } else {
                response.setHeader("content-Type", "application/pdf");
                fileName = fileName.concat(".pdf");
            }
            // 设置文件下载方式，直接下载
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            // 设置文件大小，如果post请求，必须设置
            response.setHeader("Content-Length", String.valueOf(b.length));
            OutputStream out = response.getOutputStream();
            out.write(b);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("下载文件失败!");
            e.printStackTrace();
        }
    }

    /**
     * 抽取公共逻辑，创建入库单和编辑入库单，添加数据库数据操作
     *
     * @param warehouseWarrant
     * @param request
     * @param uuid
     * @param newFlag 添加入库单、编辑入库单标识  false：添加入库单 true：编辑入库单
     */
    private void commonWarehouseWarrantSave(boolean newFlag,String token, String key, WarehouseWarrant warehouseWarrant, WarehouseWarrantVo request, String uuid, String serviceName) {
        // 构建一个基础入库单
        createBasicsWarehouseWarrant(warehouseWarrant, request, uuid);
        // 如果有商品信息，添加商品
        List<WarehouseWarrantDetail> detailList = new ArrayList<>();
        List<ProductInfoVo> productInfos = request.getProductInfos();
        log.info("商品size:{} 明细：{}", productInfos.size(), JSON.toJSONString(productInfos));
        if (CollectionUtils.isNotEmpty(productInfos)) {
            addProductinfo(detailList, productInfos, uuid);
        }
        // 添加入库单商品
        Map<String, Object> map = new HashMap<>();
        map.put("listColumn", detailList.get(0));
        map.put("listData", detailList);
        warrantDetailMapper.insertList(map);
        boolean flag = request.isCommitFlag();
        if (StringUtils.equals(request.getTransitType(), String.valueOf(NumberUtils.INTEGER_ZERO))) {
            log.info("添加标准入库单，detail：{}", JSON.toJSONString(warehouseWarrant));
            warehouseWarrantMapper.insertSelective(warehouseWarrant);
            // 判断是保存草稿还是提交谷仓
            GranaryCreateWarehouseWarrantRequest granaryRequest = mapGranaryWarrantRequest(warehouseWarrant, detailList, null);
            if (flag) {
                granaryRequest.setVerify(1);
            } else {
                granaryRequest.setVerify(0);
            }
            log.info("创建入库单时提交谷仓创建入库单,调用谷仓API：{}，requestDetail:{}", serviceName, JSON.toJSONString(granaryRequest));
            updateWarehouseWarrant(flag, token, key, granaryRequest, uuid, serviceName);
            return;
        }
        // 揽收信息
        WarehouseAddresserInfo addresserInfo = new WarehouseAddresserInfo();
        // 谷仓中转仓入库
        createTransitWarrant(warehouseWarrant, addresserInfo, request, uuid);
        log.info("添加谷仓中转入库单，detail：{}", JSON.toJSONString(warehouseWarrant));
        warehouseWarrantMapper.insertSelective(warehouseWarrant);
        if (StringUtils.equals(request.getCollectingService(), String.valueOf(NumberUtils.INTEGER_ONE))) {
            log.info("添加揽收信息，detail：{}", JSON.toJSONString(warehouseWarrant));
            addresserInfoMapper.insert(addresserInfo);
        }
        // 判断是保存草稿还是提交谷仓
        GranaryCreateWarehouseWarrantRequest granaryRequest = mapGranaryWarrantRequest(warehouseWarrant, detailList, addresserInfo);
        if (flag) {
            granaryRequest.setVerify(1);
        } else {
            granaryRequest.setVerify(0);
        }
        log.info("创建入库单时提交谷仓创建入库单,调用谷仓API：{}，requestDetail:{}", serviceName, JSON.toJSONString(granaryRequest));
        updateWarehouseWarrant(flag, token, key, granaryRequest, uuid, serviceName);
    }

    /**
     * 调用谷仓API，需要请求参数
     *
     * @param granaryWarrantRequest
     * @param serviceName
     * @throws Exception
     */
    private String callGranaryWarrantRequest(String token, String key, Object granaryWarrantRequest, String serviceName) {
        long startMillis = System.currentTimeMillis();
        try {
            String aa= JSONObject.toJSONString(granaryWarrantRequest);
            String serviceResponse = granaryUtils.getInstance(token, key, url, JSON.toJSONString(granaryWarrantRequest), serviceName).getCallService();
            long endMillis = System.currentTimeMillis();
            log.info("调用谷仓API:{},总耗时：{}", serviceName, endMillis - startMillis);
            return serviceResponse;
        } catch (Exception e) {
            long endMillis = System.currentTimeMillis();
            log.error("调用谷仓API:{}接口异常!总耗时：{}", serviceName, endMillis - startMillis);
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408, "调用谷仓" + serviceName + "接口异常!");
        }
    }

    /**
     * 提交谷仓创建入库单后，返回入库单号，更新入库单表
     *
     * @param requestDTO
     * @param uuid
     */
    private void updateWarehouseWarrant(boolean flag, String token, String key, GranaryCreateWarehouseWarrantRequest requestDTO, String uuid, String serviceName) {
        // 提交谷仓API
        String warrantResponse = callGranaryWarrantRequest(token, key, requestDTO, serviceName);
        GranaryCreateWarehouseWarrantResponse createWarrantResponse = JSON.parseObject(warrantResponse, GranaryCreateWarehouseWarrantResponse.class);
        judgeReturnResult(SERVICE_NAME_CREATE_GRN, createWarrantResponse);
        String receivingCode = createWarrantResponse.getData().getReceivingCode();
        log.info("调用谷仓API:{},接口成功，receivingCode：{}", serviceName, receivingCode);
        // 更新品连入库单表，入库单号以及入库单状态
        if (flag) {
            warehouseWarrantMapper.updateBySequenceNumber(new WarehouseWarrant(uuid, receivingCode, Byte.valueOf("1")));
        }else{
            warehouseWarrantMapper.updateBySequenceNumber(new WarehouseWarrant(uuid, receivingCode, Byte.valueOf("0")));
        }
    }

    /**
     * 调用谷仓API，没有请求参数
     *
     * @param serviceName
     * @throws Exception
     */
    private String callGranaryWarrantRequest(String token, String key, String serviceName) {
        long startMillis = System.currentTimeMillis();
        try {
            String serviceResponse = granaryUtils.getInstance(token, key, url, GRANARY_REQUEST, serviceName).getCallService();
            long endMillis = System.currentTimeMillis();
            log.info("调用谷仓API:{},总耗时：{}", serviceName, endMillis - startMillis);
            return serviceResponse;
        } catch (Exception e) {
            long endMillis = System.currentTimeMillis();
            log.error("调用谷仓API:{}接口异常！总耗时：{}", serviceName, endMillis - startMillis);
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408, "调用谷仓" + serviceName + "接口异常!");
        }
    }

    /**
     * 统一校验调用谷仓API是否成功，如果失败，打印对应的错误信息，并抛出调用谷仓API异常
     *
     * @param serviceName
     * @param granaryResponse
     */
    private void judgeReturnResult(String serviceName, GranaryResponseInterface granaryResponse) {
        if (StringUtils.equals(GRANARY_FLAG_FAILURE, granaryResponse.getAsk())) {
            String message = granaryResponse.getError() == null ? granaryResponse.getMessage() : granaryResponse.getError().getErrMessage();
            log.error("调用谷仓{}接口失败，errorMessage:{}", serviceName, message);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408, message);
        }
    }

    /**
     * 映射谷仓创建入库单request
     *
     * @param warehouseWarrant
     * @param detailList
     * @param addresserInfo
     * @return
     */
    private GranaryCreateWarehouseWarrantRequest mapGranaryWarrantRequest(WarehouseWarrant warehouseWarrant, List<WarehouseWarrantDetail> detailList, WarehouseAddresserInfo addresserInfo) {
        GranaryCreateWarehouseWarrantRequest granaryRequest = new GranaryCreateWarehouseWarrantRequest();
        if (StringUtils.isNotBlank(warehouseWarrant.getReceivingCode())){
            granaryRequest.setReceiving_code(warehouseWarrant.getReceivingCode());
        }
        granaryRequest.setReference_no(warehouseWarrant.getSequenceNumber());
        granaryRequest.setTransit_type(warehouseWarrant.getTransitType());
        granaryRequest.setReceiving_shipping_type(warehouseWarrant.getReceivingShippingType());
        granaryRequest.setTracking_number(warehouseWarrant.getTrackingNumber());
        // 处理目的仓代码
        String splitWarehouseCode = splitWarehouseCode(warehouseWarrant.getWarehouseCode());
        granaryRequest.setWarehouse_code(splitWarehouseCode);
        granaryRequest.setEta_date(DateUtils.formatDate(warehouseWarrant.getEtaDate(), FORMAT_PATTERN));
        // 添加入库单商品明细
        granaryRequest.setItemsByWarehouseWarrantDetail(detailList);
        // 增值税号、增值税豁免号
        if (StringUtils.isNotBlank(warehouseWarrant.getVatNumber()) || StringUtils.isNotBlank(warehouseWarrant.getExemptionNumber())) {
            granaryRequest.setVatByWarehouseWarrant(warehouseWarrant.getVatNumber(), warehouseWarrant.getExemptionNumber(), warehouseWarrant.getEori());
        }
        if (StringUtils.isNotBlank(warehouseWarrant.getClearanceAttachedName())) {
            // 需要添加报关附件信息
            Map<String, String> map = getFileTypeAndToBase64(warehouseWarrant.getClearanceAttached());
            granaryRequest.setClearance_file(map.get(FILE_BASE64), map.get(FILE_TYPE));
        }
        if (warehouseWarrant.getTransitType() == 3) {
            // 如果为中转仓入库，添加中转仓入库单信息
            granaryRequest.setSm_code(warehouseWarrant.getSmCode());
            granaryRequest.setWeight(warehouseWarrant.getWeight());
            granaryRequest.setVolume(warehouseWarrant.getVolume());
            granaryRequest.setTransit_warehouse_code(warehouseWarrant.getTransitWarehouseCode());
            granaryRequest.setPickup_form(warehouseWarrant.getPickupForm());
            granaryRequest.setCustoms_type(warehouseWarrant.getCustomsType());
            // 发票文件
            Map<String, String> map = getFileTypeAndToBase64(warehouseWarrant.getInvoiceAttached());
            granaryRequest.setInvoice_base64(map.get(FILE_BASE64));
            granaryRequest.setCollecting_service(warehouseWarrant.getCollectingService());
            if (warehouseWarrant.getCollectingService() == 1) {
                // 需要添加揽收信息
                granaryRequest.setCollecting_address(addresserInfo);
                if (null != addresserInfo.getCaDate()) {
                    granaryRequest.setCollecting_time(DateUtils.formatDate(addresserInfo.getCaDate(), FORMAT_PATTERN));
                }
            }
            String valueAddService = null;
            if (null != warehouseWarrant.getValueAddedService()) {
                switch (warehouseWarrant.getValueAddedService()) {
                    case "0":
                        valueAddService = "world_ease";
                        break;
                    case "1":
                        valueAddService = "origin_crt";
                        break;
                    case "2":
                        valueAddService = "fumigation";
                        break;
                    default:
                        valueAddService = null;
                }
            }
            if (StringUtils.isNotBlank(valueAddService)) {
                granaryRequest.setValue_add_service(valueAddService);
            }
            granaryRequest.setClearance_service(warehouseWarrant.getClearanceService());
            if (warehouseWarrant.getClearanceService() == 1) {
                granaryRequest.setImport_company(warehouseWarrant.getImportCompany());
                granaryRequest.setExport_company(warehouseWarrant.getExportCompany());
            }
        }
        return granaryRequest;
    }

    /**
     * 处理目的仓库代码，截取最后一个‘_’之后的字符
     *
     * @param warehouseCode
     * @return
     */
    private String splitWarehouseCode(String warehouseCode) {
        String[] warehouseCodeArr = warehouseCode.split("_");
        return warehouseCodeArr[warehouseCodeArr.length - 1];
    }

    /**
     * 获取网络文件类型，并且编码为base64
     *
     * @param fileUrl
     * @return
     * @throws Exception
     */
    private Map<String, String> getFileTypeAndToBase64(String fileUrl) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        HttpURLConnection conn;
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(fileUrl)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407, "获取网络文件类型地址(url)为空!");
        }
        try {
            URL url = new URL(fileUrl);
            conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            // 设置字符编码
            conn.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            conn.connect();

            // 文件名
            String filePathUrl = conn.getURL().getFile();
            filePathUrl = filePathUrl.replaceAll("[/;]", "\\\\");
            String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);
            String fileType = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
            map.put(FILE_TYPE, fileType);

            // 如果上传的是压缩包，需要延迟1-2秒，否则有可能拿不到上传文件，会报FileNotFoundException
            if (StringUtils.equalsAny(fileType, "rar", "zip")) {
                Thread.sleep(1000);
            }

            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[conn.getContentLength()];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len;
            //使用一个输入流从buffer里把数据读取出来
            while ((len = inStream.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            //关闭输入流
            inStream.close();
            //对字节数组Base64编码
            map.put(FILE_BASE64, Base64Utils.encodeToString(outStream.toByteArray()));
            return map;
        } catch (Exception e) {
            log.error("获取网络文件类型，并且编码为base64失败,url:{} error:{}", fileUrl, e);
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407, "获取网络文件类型，并且编码为base64失败!");
        }
    }

    /**
     * 构建中转仓入库单
     *
     * @param warehouseWarrant
     * @param request
     * @param uuid
     */
    private void createTransitWarrant(WarehouseWarrant warehouseWarrant, WarehouseAddresserInfo addresserInfo, WarehouseWarrantVo request, String uuid) {
        warehouseWarrant.setTransitWarehouseCode(request.getTransitWarehouseCode());
        warehouseWarrant.setTransitWarehouseName(request.getTransitWarehouseName());
        warehouseWarrant.setSmCode(request.getSmCode());
        warehouseWarrant.setWeight(request.getWeight());
        warehouseWarrant.setVolume(request.getVolume());
        warehouseWarrant.setPickupForm(Byte.valueOf(request.getPickupForm()));
        warehouseWarrant.setCustomsType(Byte.valueOf(request.getCustomsType()));
        warehouseWarrant.setInvoiceAttached(request.getInvoiceAttached());
        warehouseWarrant.setInvoiceAttachedName(request.getInvoiceAttachedName());
        String collectingService = request.getCollectingService();
        if (StringUtils.equals(collectingService, String.valueOf(NumberUtils.INTEGER_ONE))) {
            // 如果选择上门提货，需要提供揽收信息
            addresserInfo.setParentSequenceNumber(uuid);
            addresserInfo.setCaFirstName(request.getCaFirstName());
            addresserInfo.setCaLastName(request.getCaLastName());
            addresserInfo.setCaContactPhone(request.getCaContactPhone());
            addresserInfo.setCaCountryCode(request.getCaCountryCode());
            addresserInfo.setCaState(request.getCaState());
            addresserInfo.setCaCity(request.getCaCity());
            addresserInfo.setCaAddress1(request.getCaAddress1());
            addresserInfo.setCaAddress2(request.getCaAddress2());
            addresserInfo.setCaZipcode(request.getCaZipcode());
            if (StringUtils.isNotBlank(request.getCaDate())) {
                addresserInfo.setCaDate(DateUtils.parseDate(request.getCaDate(), FORMAT_PATTERN));
            }
        }

        warehouseWarrant.setCollectingService(Byte.valueOf(collectingService));
        if (StringUtils.isNotBlank(request.getValueAddedService())) {
            warehouseWarrant.setValueAddedService(request.getValueAddedService());
        }
        warehouseWarrant.setClearanceService(Byte.valueOf(request.getClearanceService()));
        if (null != request.getImportCompany()) {
            warehouseWarrant.setImportCompany(request.getImportCompany());
        }
        if (null != request.getExportCompany()) {
            warehouseWarrant.setExportCompany(request.getExportCompany());
        }
    }

    /**
     * 添加商品信息
     *
     * @param detailList
     * @param productInfos
     * @param uuid
     * @return
     */
    private void addProductinfo(List<WarehouseWarrantDetail> detailList, List<ProductInfoVo> productInfos, String uuid) {
        productInfos.forEach(p -> {
            WarehouseWarrantDetail detail = new WarehouseWarrantDetail();
            detail.setParentSequenceNumber(uuid);
            detail.setProductSku(p.getSystemSku());
            detail.setProductCnName(p.getCommodityNameCn());
            detail.setProductEnName(p.getCommodityNameEn());
            detail.setBoxNo(p.getBoxNo());
            detail.setQuantityShipped(p.getQuantityShipped());
            detailList.add(detail);
        });
    }

    /**
     * 构建一个标准的WarehouseWarrant
     *
     * @return
     */
    private WarehouseWarrant createStandardModel(UserInfoVO userInfo) {
        WarehouseWarrant warehouseWarrant = new WarehouseWarrant();
        warehouseWarrant.setCreateBy(userInfo.getLoginName());
        warehouseWarrant.setCreatTime(new Date());
        // 供应商名称
        warehouseWarrant.setSupplier(userInfo.getTopUserLoginName());
        // 供应商Id -> 主账户Id
        warehouseWarrant.setSupplierId(userInfo.getTopUserId());
        return warehouseWarrant;
    }

    /**
     * 构建入库单基础信息
     *
     * @param warehouseWarrant
     * @param request
     * @param uuid
     */
    private void createBasicsWarehouseWarrant(WarehouseWarrant warehouseWarrant, WarehouseWarrantVo request, String uuid) {
        // 入库单序号
        warehouseWarrant.setSequenceNumber(uuid);
        // 目的仓服务商名称
        warehouseWarrant.setWarehouseFacilitatorName(request.getWarehouseFacilitatorName());
        // 目的仓服务商代码
        warehouseWarrant.setWarehouseFacilitatorCode(request.getWarehouseFacilitatorCode());
        // 目的仓库名称
        warehouseWarrant.setWarehouseName(request.getWarehouseName());
        // 目的仓库编码
        warehouseWarrant.setWarehouseCode(request.getWarehouseCode());
        // 入库单类型
        warehouseWarrant.setTransitType(Byte.valueOf(request.getTransitType()));
        // 运输方式
        warehouseWarrant.setReceivingShippingType(Byte.valueOf(request.getReceivingShippingType()));
        // 追踪号
        if (StringUtils.isNotBlank(request.getTrackingNumber())) {
            warehouseWarrant.setTrackingNumber(request.getTrackingNumber());
        }
        // 计划到货时间
        if (StringUtils.isNotBlank(request.getEtaDate())) {
            warehouseWarrant.setEtaDate(DateUtils.parseDate(request.getEtaDate(), FORMAT_PATTERN));
        }
        // 增值税号
        if (StringUtils.isNotBlank(request.getVatNumber())) {
            warehouseWarrant.setVatNumber(request.getVatNumber());
        }
        // 增值税豁免号
        if (StringUtils.isNotBlank(request.getExemptionNumber())) {
            warehouseWarrant.setExemptionNumber(request.getExemptionNumber());
        }
        // EORI
        if (StringUtils.isNotBlank(request.getEori())) {
            warehouseWarrant.setEori(request.getEori());
        }
        // 清关附件
        if (StringUtils.isNotBlank(request.getClearanceAttachedName())) {
            warehouseWarrant.setClearanceAttachedName(request.getClearanceAttachedName());
            warehouseWarrant.setClearanceAttached(request.getClearanceAttached());
        }
    }

}
