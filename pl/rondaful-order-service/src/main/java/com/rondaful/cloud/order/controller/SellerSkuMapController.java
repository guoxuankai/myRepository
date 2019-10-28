package com.rondaful.cloud.order.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.constant.UserConstants;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.ExcelUtil;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.entity.orderRule.SellerSkuMap;
import com.rondaful.cloud.order.entity.orderRule.SkuMapRule;
import com.rondaful.cloud.order.enums.SkuEnmus;
import com.rondaful.cloud.order.mapper.SellerSkuMapMapper;
import com.rondaful.cloud.order.remote.RemoteCommodityService;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.ISellerSkuMapService;
import com.rondaful.cloud.order.service.SkuMapRuleService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//@Api(description = "卖家与品连sku映射接口")
//@RestController
//@RequestMapping("/skuMap")
public class SellerSkuMapController {

    private static final Logger logger = LoggerFactory.getLogger(SellerSkuMapController.class);

    private final ISellerSkuMapService sellerSkuMapService;

    private final RemoteCommodityService remoteCommodityService;

    private final RemoteSellerService remoteSellerService;

    private final SkuMapRuleService skuMapRuleService;

    private final GetLoginUserInformationByToken userInfo;
    
    private final SellerSkuMapMapper sellerSkuMapMapper;

    @Autowired
    public SellerSkuMapController(ISellerSkuMapService ISellerSkuMapService,
                                  RemoteCommodityService remoteCommodityService,
                                  RemoteSellerService remoteSellerService, 
                                  SkuMapRuleService skuMapRuleService, 
                                  GetLoginUserInformationByToken userInfo,
                                  SellerSkuMapMapper sellerSkuMapMapper) {
        this.sellerSkuMapService = ISellerSkuMapService;
        this.remoteCommodityService = remoteCommodityService;
        this.remoteSellerService = remoteSellerService;
        this.skuMapRuleService = skuMapRuleService;
        this.userInfo = userInfo;
        this.sellerSkuMapMapper=sellerSkuMapMapper;
    }
    

    @AspectContrLog(descrption = "order批量添加sku映射", actionType = SysLogActionType.ADD)
    @PostMapping("/addSkuMaps")
    @ApiOperation("批量添加卖家与品连sku映射")
    public List<String> addSkuMaps(@RequestBody List<SellerSkuMap> maps) {
        ArrayList<SellerSkuMap> paramsError = new ArrayList<>();
        ArrayList<SellerSkuMap> platformTypeError = new ArrayList<>();
        ArrayList<SellerSkuMap> commodityError = new ArrayList<>();
        ArrayList<SellerSkuMap> righMaps = new ArrayList<>();
        for (SellerSkuMap m : maps) {
            if (StringUtils.isBlank(m.getPlSku())
                    || StringUtils.isBlank(m.getPlatformSku())
                    || m.getStatus() == null
                    || StringUtils.isBlank(m.getPlatform())
                    || StringUtils.isBlank(m.getAuthorizationId())) {
                paramsError.add(m);
                continue;
            }
            if (!m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())
                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())
                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.WISH.getPlatform())
                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform())
                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform())) {
                platformTypeError.add(m);
                continue;
            }

            if (!this.checkSku(m)) {
                commodityError.add(m);
            } else {
                righMaps.add(m);
            }
        }
        try {
            HashMap<String, List<SellerSkuMap>> inserts = sellerSkuMapService.inserts(righMaps);
            List<SellerSkuMap> platformError = inserts.get(SkuEnmus.skuType.PLATFORM.getKey());
            StringBuilder sb = new StringBuilder();
            if (paramsError.size() > 0) {
                paramsError.forEach(s -> sb.append(s.getPlSku()).append(","));
                sb.append(" 等品连sku参数不完全！").append("\n");
            }
            if (platformTypeError.size() > 0) {
                platformTypeError.forEach(s -> sb.append(s.getPlSku()).append(","));
                sb.append(" 等品连sku平台类型有误！").append("\n");
            }
            if (commodityError.size() > 0) {
                commodityError.forEach(s -> sb.append(s.getPlSku()).append(","));
                sb.append(" 等品连sku已下架！").append("\n");
            }
            if (platformError.size() > 0) {
                platformError.forEach(s -> sb.append(s.getPlatformSku()).append(","));
                sb.append(" 等平台SKU已存在！").append("\n");
            }
            String errorMessage = sb.toString();
            if (StringUtils.isNotBlank(errorMessage)) {
                logger.error("_________________-" + errorMessage);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), Utils.translation(errorMessage));
            }
            return null;
        } catch (Exception e) {
            logger.error("批量添加卖家与品连sku映射异常", e);
            if (e instanceof GlobalException)
                throw e;
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "添加sku失败");
        }
    }


    @AspectContrLog(descrption = "order批量添加sku映射", actionType = SysLogActionType.ADD)
    @PostMapping("/addSkuMapsByExcel")
    @ApiOperation("Excel添加sku映射")
    public void addSkuMapsByExcel(@RequestParam("files") MultipartFile[] files) {
        List<SellerSkuMap> list=null;
        try {
        	if (files != null && files.length>0) {
        		ExcelUtil<SellerSkuMap> excelRead = new ExcelUtil<>(files[0].getInputStream());
                list = excelRead.read(new SkuMapExcelMapRowImpl(), 2);
                list = this.queryEmpower(list);
			}
        } catch (Exception e) {
            logger.error("Excel文件上传sku映射异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "添加sku失败");
        }
        if (list != null && list.size() > 0)
            this.addSkuMaps(list);
    }


    public class SkuMapExcelMapRowImpl implements ExcelUtil.ExcelMapRow<SellerSkuMap> {
        public SellerSkuMap mapRow(XSSFRow row) {
            try {
                SellerSkuMap map = new SellerSkuMap();
                row.getCell(0).setCellType(CellType.STRING);
                map.setPlatform(row.getCell(0).getStringCellValue());
                row.getCell(1).setCellType(CellType.STRING);
                map.setSellerSelfAccount(row.getCell(1).getStringCellValue());
                row.getCell(2).setCellType(CellType.STRING);
                map.setPlatformSku(row.getCell(2).getStringCellValue());
                row.getCell(3).setCellType(CellType.STRING);
                map.setPlSku(row.getCell(3).getStringCellValue());
                map.setStatus(1);
                return map;
            } catch (Exception e) {
                logger.error("获取值失败：", e);
                return null;
            }
        }

    }

    private List<SellerSkuMap> queryEmpower(List<SellerSkuMap> maps) {
        HashMap<String, String> empowerMap = new HashMap<>();
        String key;
        String value;
        for (SellerSkuMap map : maps) {
            key = map.getPlatform() + ":" + map.getSellerSelfAccount();
            value = empowerMap.get(key);
            if (StringUtils.isNotBlank(value)) {
                map.setAuthorizationId(value);
            } else {
                try {
                    String allNoPage = remoteSellerService.findAllNoPage(map.getSellerSelfAccount(), null, 1, null, sellerSkuMapService.getPlatform(map.getPlatform()));
                    String dataString = Utils.returnRemoteResultDataString(allNoPage, "卖家服务异常");
                    List<Empower> empowers = JSONObject.parseArray(dataString, Empower.class);
                    Empower empower = empowers.get(0);
                    value = String.valueOf(empower.getEmpowerid());
                    map.setAuthorizationId(value);
                    empowerMap.put(key, value);
                } catch (Exception e) {
                    logger.error("文件添加sku映射查询授权ID异常 平台：" + map.getPlatform() + " 店铺：" + map.getSellerSelfAccount(), e);
                }
            }
        }
        return maps;
    }

    @AspectContrLog(descrption = "系统内部定时任务批量添加sku映射", actionType = SysLogActionType.ADD)
    @PostMapping("/addSkuMapsWhitTask")
    public void addSkuMapsWhitTask(@RequestBody List<SellerSkuMap> maps, String key) {
        if (StringUtils.isNotBlank(key) && key.equalsIgnoreCase(UserConstants.FEIGN_REQUEST_VALUE)) {
            this.addSkuMaps(maps);
        }
    }

    @AspectContrLog(descrption = "order更新sku映射", actionType = SysLogActionType.UDPATE)
    @PutMapping("/updateSkuMap")
    @ApiOperation("更新sku映射")
    public void updateSkuMap(@RequestBody SellerSkuMap map) {
        if (map.getId() == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        if (StringUtils.isNotBlank(map.getPlatform())
                && (!map.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())
                && !map.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())
                && !map.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.WISH.getPlatform())
                && !map.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform())
                && !map.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform())
                ))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        try {
            if (map.getStatus() != null && map.getStatus() == 1) {
                if (StringUtils.isBlank(map.getPlSku())) {
                    map.setPlSku(sellerSkuMapService.selectByPrimaryKey(map.getId()).getPlSku());
                }
                if (!this.checkSku(map)) {
                    logger.error("新增映射不能有下架产品");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "不能启用下架商品的映射");
                }
            }
        } catch (Exception e) {
            logger.error("更新sku映射不能启用下架产品", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "不能启用下架商品的映射");
        }
        try {
            if (map.getId() != null) sellerSkuMapService.updateByPrimaryKeySelective(map);
        } catch (Exception e) {
            if (e instanceof GlobalException)
                throw e;
            logger.error("更新sku异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    private boolean checkSku(SellerSkuMap map) {
        String result = remoteCommodityService.getSkuList("1", "1", null, null,
                null, null, map.getPlSku(), null, null, null);
        return StringUtils.isNotBlank(result) && result.contains(map.getPlSku());
    }

    private String getPla(String plate) {   //Wish,Amazon,eBay,AliExpress
        if (plate.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())) {
            return "Amazon";
        } else if (plate.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())) {
            return "eBay";
        } else if (plate.equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform())) {
            return "AliExpress";
        } else if (plate.equalsIgnoreCase(OrderRuleEnum.platformEnm.WISH.getPlatform())) {
            return "Wish";
        }else if (plate.equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform())) {
            return "other";
        }
        return null;
    }

    @AspectContrLog(descrption = "order批量添加sku映射前端", actionType = SysLogActionType.ADD)
    @PostMapping("/addSkuMapsWeb")
    @ApiOperation("批量添加卖家与品连sku映射前端")
    public List<String> addSkuMapsWeb(@RequestBody List<SellerSkuMap> maps) {
        String message = "";
        for (SellerSkuMap m : maps) {
            if (StringUtils.isBlank(m.getPlSku())
                    || StringUtils.isBlank(m.getPlatformSku())
                    || m.getStatus() == null
                    || StringUtils.isBlank(m.getPlatform())
                    || StringUtils.isBlank(m.getAuthorizationId())) {
                message = "平台sku: " + m.getPlatformSku() + " 参数不全";
                logger.error("_________________-" + message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), Utils.translation(message));
            }
            if (!m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())
                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())
                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.WISH.getPlatform())
                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform())
                    && !m.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform())) {
                message = "平台sku: " + m.getPlatformSku() + " 平台异常";
                logger.error("_________________-" + message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), Utils.translation(message));
            }

            if (!this.checkSku(m)) {
                message = "平台sku: " + m.getPlatformSku() + " 商品下架";
                logger.error("_________________-" + message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), Utils.translation(message));
            }
        }
        try {
            List<SellerSkuMap> platformError = sellerSkuMapService.insertsNotError(maps);
            StringBuilder sb = new StringBuilder();

            if (platformError.size() > 0) {
                platformError.forEach(s -> sb.append(s.getPlatformSku()).append(","));
                sb.append(" 等平台SKU已存在！").append("\n");
            }
            String errorMessage = sb.toString();
            if (StringUtils.isNotBlank(errorMessage)) {
                logger.error("_________________-" + errorMessage);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), Utils.translation(errorMessage));
            }
            return null;
        } catch (Exception e) {
            logger.error("批量添加卖家与品连sku映射异常", e);
            if (e instanceof GlobalException)
                throw e;
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "添加sku失败");
        }
    }

    @AspectContrLog(descrption = "order查询订单sku列表", actionType = SysLogActionType.QUERY)
    @GetMapping("/queryMaps")
    @ApiOperation(value = "查询sku映射列表", notes = "page当前页码，row每页显示行数", response = SellerSkuMap.class)
    @RequestRequire(require = "page,row", parameter = String.class)
    public Page<SellerSkuMap> queryMaps(SellerSkuMap model, String page, String row) {
        try {
            //todo 在卖家授权接口中判断账号的数据权限

  /*          if (StringUtils.isNotBlank(model.getSellerPlAccount())
                    || StringUtils.isNotBlank(model.getSellerSelfAccount())) {*/
            if (!setParam(model))
                return new Page<>(new PageInfo<SellerSkuMap>() {{
                    setStartRow(Integer.parseInt(page));
                    setPageSize(Integer.parseInt(row));
                }});
            /*          }*/
            Page.builder(page, row);
            Page<SellerSkuMap> pages = sellerSkuMapService.page(model);
            this.setAccountMessage(pages);
            return pages;
        } catch (Exception e) {
            logger.error("查询sku映射异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "order查询订单sku列表，没有账号和权限限制", actionType = SysLogActionType.QUERY)
    @GetMapping("/queryMapsNoLimit")
    @ApiOperation(value = "查询sku映射列表,没有账号和权限限制", notes = "", response = SellerSkuMap.class)
    public List<SellerSkuMap> queryMapsNoLimit(@RequestParam("plSku")String plSku) {
        try {
            List<SellerSkuMap> list = sellerSkuMapMapper.getAllByPlSku(plSku);
            if (list != null && list.size()>0) {
            	HashMap<String, Empower> empowerHashMap = new HashMap<>();
                Empower empower;
                for (SellerSkuMap map : list) {
                    empower = empowerHashMap.get(map.getAuthorizationId());
                    if (empowerHashMap.get(map.getAuthorizationId()) != null) {
                        map.setSellerPlAccount(empower.getPinlianaccount());
                        map.setSellerSelfAccount(empower.getAccount());
                        map.setSellerPlId(String.valueOf(empower.getPinlianid()));
                    } else {
                        try {
                            String oneEmpowByAccount = remoteSellerService.findOneEmpowByAccount(null,
                                    null, null, map.getAuthorizationId());
                            String dataString = Utils.returnRemoteResultDataString(oneEmpowByAccount, "卖家服务异常");
                            empower = JSONObject.parseObject(dataString, Empower.class);
                            map.setSellerPlAccount(empower.getPinlianaccount());
                            map.setSellerSelfAccount(empower.getAccount());
                            map.setSellerPlId(String.valueOf(empower.getPinlianid()));
                            empowerHashMap.put(map.getAuthorizationId(), empower);
                        } catch (Exception e) {
                            logger.error("返回skumap列表时查询账户信息异常,异常的授权id为：" + map.getAuthorizationId(), e);
                        }
                    }
                }
			}
            return list;
        } catch (Exception e) {
            logger.error("查询sku映射异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }


    /**
     * 设置参数
     *
     * @param model sku映射的参数对象
     * @return 是否设置成功
     */
    private boolean setParam(SellerSkuMap model) {
        String allNoPage = remoteSellerService.findAllNoPage(model.getSellerSelfAccount(), model.getSellerPlAccount(), 1, model.getPinlianIds(), null);
        try {
            String dataString = Utils.returnRemoteResultDataString(allNoPage, "卖家服务异常");
            List<Empower> empowers = JSONObject.parseArray(dataString, Empower.class);
            ArrayList<String> strings = new ArrayList<>();
            for (Empower e : empowers) {
                strings.add(e.getEmpowerid().toString());
            }
            if (strings.size() == 0) {
                return false;
            } else if (strings.size() == 1) {
                model.setAuthorizationId(strings.get(0));
            } else
                model.setAuthorizationIds(strings);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 为查询的分页数据设置用户账号信息等
     *
     * @param pages 分页对象
     */
    @SuppressWarnings("unchecked")
    private void setAccountMessage(Page<SellerSkuMap> pages) {
        PageInfo<SellerSkuMap> pageInfo = pages.getPageInfo();
        if (pageInfo != null && pageInfo.getList() != null && pageInfo.getList().size() > 0) {
            HashMap<String, Empower> empowerHashMap = new HashMap<>();
            Empower empower;
            for (SellerSkuMap map : pageInfo.getList()) {
                empower = empowerHashMap.get(map.getAuthorizationId());
                if (empowerHashMap.get(map.getAuthorizationId()) != null) {
                    map.setSellerPlAccount(empower.getPinlianaccount());
                    map.setSellerSelfAccount(empower.getAccount());
                    map.setSellerPlId(String.valueOf(empower.getPinlianid()));
                } else {
                    try {
                        String oneEmpowByAccount = remoteSellerService.findOneEmpowByAccount(null,
                                null, null, map.getAuthorizationId());
                        String dataString = Utils.returnRemoteResultDataString(oneEmpowByAccount, "卖家服务异常");
                        empower = JSONObject.parseObject(dataString, Empower.class);
                        map.setSellerPlAccount(empower.getPinlianaccount());
                        map.setSellerSelfAccount(empower.getAccount());
                        map.setSellerPlId(String.valueOf(empower.getPinlianid()));
                        empowerHashMap.put(map.getAuthorizationId(), empower);
                    } catch (Exception e) {
                        logger.error("返回skumap列表时查询账户信息异常,异常的授权id为：" + map.getAuthorizationId(), e);
                    }
                }
            }
        }
    }

    @AspectContrLog(descrption = "order删除订单sku映射", actionType = SysLogActionType.DELETE)
    @DeleteMapping("/deleteMap/{id}")
    @ApiOperation("删除sku映射")
    public String deleteMap(@ApiParam(value = "sku映射id", name = "id", required = true) @PathVariable Long id) {
        if (id == null || id == 0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        try {
            sellerSkuMapService.deleteByPrimaryKey(id);
            return "删除成功";
        } catch (Exception e) {
            logger.error("删除sku映射异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "添加用户sku规则", actionType = SysLogActionType.ADD)
    @PostMapping("/insertSkuMapRule")
    @ApiOperation("添加用户sku规则")
    public SkuMapRule insertSkuMapRule(@RequestBody SkuMapRule skuMapRule) {
        try {
            UserDTO userDTO = this.userInfo.getUserDTO();
            Boolean manage = userDTO.getManage();
            if (manage) {
                skuMapRule.setSellerId(String.valueOf(userDTO.getUserId()));
                skuMapRule.setSellerAccount(userDTO.getLoginName());
            } else {
                skuMapRule.setSellerId(String.valueOf(userDTO.getTopUserId()));
                skuMapRule.setSellerAccount(userDTO.getTopUserLoginName());
            }

            skuMapRuleService.insert(skuMapRule);
            return skuMapRule;
        } catch (Exception e) {
            logger.error("添加用户sku规则失败", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "添加失败");
        }
    }

    @AspectContrLog(descrption = "删除用户sku规则", actionType = SysLogActionType.DELETE)
    @DeleteMapping("/deleteSkuMapRule/{id}")
    @ApiOperation("删除用户sku规则")
    public void deleteSkuMapRule(@ApiParam(value = "sku映射id", name = "id", required = true) @PathVariable Integer id) {
        try {
            skuMapRuleService.delete(id);
        } catch (Exception e) {
            logger.error("删除用户sku规则异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "删除失败");
        }
    }

    @AspectContrLog(descrption = "更新用户sku规则", actionType = SysLogActionType.UDPATE)
    @PutMapping("/updateSkuMapRule")
    @ApiOperation("更新用户sku规则")
    public void updateSkuMapRule(@RequestBody SkuMapRule skuMapRule) {
        if (skuMapRule.getId() == null || skuMapRule.getId() == 0)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403.getCode(), "ID不能为空");
        try {
            skuMapRuleService.update(skuMapRule);
        } catch (Exception e) {
            logger.error("更新用户sku规则失败", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "更新失败");
        }
    }


    @AspectContrLog(descrption = "查询用户sku规则", actionType = SysLogActionType.QUERY)
    @GetMapping("/selectMyRule")
    @ApiOperation("查询用户sku规则")
    public SkuMapRule selectMyRule() {
        try {
            SkuMapRule skuMapRule = new SkuMapRule();
            skuMapRule.setSellerId(String.valueOf(userInfo.getUserDTO().getTopUserId()));
            return skuMapRuleService.selectBySellerId(skuMapRule);
        } catch (Exception e) {
            logger.error("查询用户sku规则异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询失败");
        }
    }

    @AspectContrLog(descrption = "查询平台sku对应的商品", actionType = SysLogActionType.QUERY)
    @GetMapping("/getSellerSkuMapByPlatformSku")
    @ApiOperation("查询平台sku对应的商品")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "platform", value = "平台 aliexpress，eBay，Amazon，with", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "authorizationId", value = "授权id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "platformSku", value = "平台sku", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "sellerId", value = "卖家id", required = true)
    })
    public SellerSkuMap getSellerSkuMapByPlatformSku(String platform, String authorizationId, String platformSku, String sellerId) {
        try {
            return sellerSkuMapService.getSellerSkuMapByPlatformSku(platform, authorizationId, platformSku, sellerId);
        } catch (Exception e) {
            logger.error("查询平台sku对应的商品异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询失败");
        }
    }

    @DeleteMapping("/deleteMap")
    @ApiOperation("根据品连sku删除sku映射")
    public void deleteMapByPlSku(@RequestParam("plSku") String plSku) {
        if (StringUtils.isBlank(plSku)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        try {
            sellerSkuMapService.deleteByPlSku(plSku);
        } catch (Exception e) {
            logger.error("根据品连sku删除sku映射", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

}
