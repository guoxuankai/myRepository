package com.rondaful.cloud.supplier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.common.GetLoginInfo;
import com.rondaful.cloud.supplier.dto.AuthorizeDTO;
import com.rondaful.cloud.supplier.dto.HouseNameDTO;
import com.rondaful.cloud.supplier.dto.HouseTypeDTO;
import com.rondaful.cloud.supplier.entity.DeliveryRecord;
import com.rondaful.cloud.supplier.entity.WarehouseInventory;
import com.rondaful.cloud.supplier.entity.WarehouseSync;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.basics.InitWarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseInitDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.InventoryDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.OrderInvDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.LogisticsMapDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.LogisticsPublishDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.LogisticsSelectDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.QuerySelectDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.SuggestDTO;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.model.enums.WarehouseFirmEnum;
import com.rondaful.cloud.supplier.model.request.basic.WarehouseSelectDTO;
import com.rondaful.cloud.supplier.model.request.basic.WarehouseServiceDTO;
import com.rondaful.cloud.supplier.model.request.inventory.QueryInvReq;
import com.rondaful.cloud.supplier.model.response.basic.InitWarehouseReq;
import com.rondaful.cloud.supplier.model.response.provide.InvSellerReq;
import com.rondaful.cloud.supplier.model.response.provide.third.WarehouseReq;
import com.rondaful.cloud.supplier.remote.RemoteCommodityService;
import com.rondaful.cloud.supplier.service.*;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(description = "仓库接口提供者")
@RestController
@RequestMapping(value = "/provider/")
public class WarehouseProvideController {

    private final Logger logger = LoggerFactory.getLogger(WarehouseProvideController.class);

    @Autowired
    GetLoginInfo getLoginInfo;
    @Autowired
    RemoteCommodityService remoteCommodityService;
    @Autowired
    IWarehouseOperateInfoService warehouseOperateInfoService;
    @Autowired
    IWareHouseService wareHouseService;
    @Autowired
    private IWarehouseInventoryService inventoryService;
    @Autowired
    private IWarehouseWarrantService warehouseWarrantService;
    @Autowired
    private IWarehouseBasicsService basicsService;
    @Autowired
    private IProcurementService procurementService;
    @Autowired
    private IInventoryService nInventoryService;
    @Autowired
    private ILogisticsService logisticsService;

    @ApiOperation(value = "接收ERP库存", notes = "")
    @PostMapping(value = "syncInventory")
    public void syncInventory(@RequestBody List<WarehouseInventory> inventoryList) {
        logger.info("erp仓库库存变动通知:params={}", JSONObject.toJSON(inventoryList));
        inventoryList.forEach(inventory->{
            this.nInventoryService.change(inventory.getSupplierSku());
        });

    }


    @ApiOperation(value = "库存预警通知", notes = "")
    @RequestMapping(value = "inventoryWarnNotice", method = RequestMethod.POST)
    public void inventoryWarnNotice() {
        try {
            inventoryService.inventoryWarnNotice();
        } catch (Exception e) {
            logger.error("库存预警通知异常:{}", e);
            e.printStackTrace();
        }
    }




    @ApiOperation(value = "根据仓库code查询授权信息", notes = "")
    @GetMapping(value = "/getAuthorizeByWarehouseCode")
    public AuthorizeDTO getAuthorizeByCode(String warehouseCode) {
        AuthorizeDTO authorizeDTO = null;
        try {
            authorizeDTO = wareHouseService.getAuthorizeByCode(warehouseCode);
        } catch (Exception e) {
            logger.error("根据仓库code查询授权信息失败", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, " 根据仓库code查询授权信息异常");
        }
        return authorizeDTO;
    }

    @ApiOperation(value = "根据公司编码批量查询授权信息", notes = "")
    @PostMapping(value = "getAuthorizeByCompanyCodeList")
    public List<AuthorizeDTO> getAuthorizeByCompanyCodeList(@RequestBody List<String> list) {
        return wareHouseService.getAuthorizeByCompanyCodeList(list);
    }

    @ApiOperation(value = "定时更新入库单状态", notes = "")
    @RequestMapping(value = "syncWarrantStatus", method = RequestMethod.POST)
    public void syncWarrantStatus() {
        try {
        	logger.info("更新入库单状态入口");
            warehouseWarrantService.syncWarrantStatus();
        } catch (Exception e) {
            logger.error("同步入库单异常：{}",e);
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "根据仓库名称取得仓库信息", notes = "")
    @RequestMapping(value = "getWarehouseInfoByParam", method = RequestMethod.POST)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "warehouseName", value = "仓库名称", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "companyCode", value = "客户编码", required = false, dataType = "string", paramType = "query")
       })
    public List<WarehouseSync> getWarehouseInfoByParam(String warehouseName,String warehouseCode,String companyCode){
    	Map<String, String> param=new HashMap<>();
    	if(StringUtils.isNotBlank(warehouseName)) {
    		param.put("warehouseName", warehouseName);
    	}
    	if(StringUtils.isNotBlank(warehouseCode)) {
    		param.put("warehouseCode", warehouseCode);
    	}
    	if(StringUtils.isNotBlank(companyCode)) {
    		param.put("companyCode", companyCode);
    	}
    	return warehouseOperateInfoService.selectWarehouseByParam(param);
    }

    @AspectContrLog(descrption = "提供星商查询仓库库存",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "提供星商查询仓库库存", notes = "page当前页码，row每页显示行数", response = WarehouseInventory.class)
	@PostMapping("getProductInventory")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "页码" , required = true, dataType = "string", paramType = "query" ),
        @ApiImplicitParam(name = "row", value = "每页显示行数", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "pinlianSku", value = "品连SKU", required = false, dataType = "string", paramType = "query")})
	@RequestRequire(require = "page, row", parameter = String.class)
	public Page<WarehouseInventory> getProductInventory(String page, String row,String warehouseCode,String pinlianSku) {
		Page.builder(page, row);
		WarehouseInventory inv=new WarehouseInventory();
		if(StringUtils.isNotBlank(warehouseCode)) {
			inv.setWarehouseCode(warehouseCode);
		}
		if(StringUtils.isNotBlank(pinlianSku)) {
			inv.setPinlianSku(pinlianSku);
		}

		Page<WarehouseInventory> p = inventoryService.page(inv);
		return p;
    }

    @AspectContrLog(descrption = "取得可用仓库列表",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "提供星商可用仓库列表", notes = "")
    @PostMapping(value = "/getValidWarehouseList")
    public List<WarehouseReq> getValidWarehouseList(){
        List<InitWarehouseDTO> list=this.basicsService.getAuth(1);
        List<WarehouseReq> result=new ArrayList<>(list.size());
        list.forEach(dto -> {
            dto.getItem().forEach(item->{
                WarehouseReq req=new WarehouseReq(dto.getName()+"-"+item.getName(),item.getId().toString(),item.getCountryCode());
                req.setWarehouseNameEn(dto.getName()+"-"+Utils.translation(item.getName()));
                result.add(req);
            });
        });
		return result;
    }

    @AspectContrLog(descrption = "询仓库库存",actionType = SysLogActionType.QUERY)
   	@ApiOperation(value = "询仓库库存", notes = "page当前页码，row每页显示行数", response = WarehouseInventory.class)
   	@PostMapping("/getInventoryByParam")
   	@ApiImplicitParams({
           @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = true, dataType = "string", paramType = "query"),
           @ApiImplicitParam(name = "pinlianSku", value = "品连SKU", required = true, dataType = "string", paramType = "query")})
   	public Integer getInventoryByParam(String warehouseCode,String pinlianSku) {
    	Map<String,String> param = new HashMap<>();
   		if(StringUtils.isNotBlank(warehouseCode)) {
   			param.put("warehouseCode", warehouseCode);
   		}
   		if(StringUtils.isNotBlank(pinlianSku)) {
   			param.put("pinlianSku", pinlianSku);
   		}

   	  Integer  availableQty= inventoryService.getInvAvailableQtyByParam(param);
   		return availableQty;
    }


    @ApiOperation(value = "添加采购建议")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pinlinaSKu", value = "pinlianSku", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "warehouseCode", value = "仓库编码", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "amount", value = "数量", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单号", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping("addSuggest")
    public Integer addSuggest(String pinlinaSKu,String warehouseCode,Integer amount,String orderId){
        SuggestDTO dto=new SuggestDTO();
        dto.setAmount(amount);
        dto.setOrderId(orderId);
        dto.setPinlianSku(pinlinaSKu);
        dto.setWarehouseId(this.basicsService.codeToId(warehouseCode));
        return this.procurementService.addSuggest(dto);
    }

    @ApiOperation(value = "获取所有仓库列表")
    @GetMapping("getAllWarehouseTree")
    public List<KeyValueDTO> getAllWarehouseTree(HttpServletRequest request){
        List<KeyValueDTO> result=new ArrayList<>();
        List<WarehouseSelectDTO> list=this.basicsService.getSelect(new ArrayList<>(0),new ArrayList<>(0),request.getHeader("i18n"),null);
        list.forEach(dto->{
            String name=WarehouseFirmEnum.getByName(dto.getName());
            dto.getChilds().forEach(child->{
                KeyValueDTO dto1=new KeyValueDTO(child.getId().toString(),child.getName());
                dto1.setDesc(name);
                result.add(dto1);
            });
        });
        return result;
    }


    @ApiOperation(value = "根据sku列表获取库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseId", value = "仓库id", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "skus", value = "sku数组,json.tostring", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping("getsBySku")
    public List<InventoryDTO> getsBySku(Integer warehouseId, String skus){
        WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(warehouseId);
        if (warehouseDTO==null|| !StatusEnums.ACTIVATE.getStatus().equals(warehouseDTO.getStatus())){
            return null;
        }
        return this.nInventoryService.getsBySku(warehouseId,  JSONArray.parseArray(skus,String.class));
    }

    @ApiOperation(value = "根据仓库id获取仓库信息")
    @ApiImplicitParam(name = "warehouseId", value = "仓库id", required = true, dataType = "Integer", paramType = "query")
    @GetMapping("getWarehouseById")
    public WarehouseDTO getWarehouseById(Integer warehouseId){
        return this.basicsService.getByWarehouseId(warehouseId);
    }

    @ApiOperation(value = "根据仓库id批量获取仓库信息")
    @ApiImplicitParam(name = "ids", value = "ids列表json.tostring", required = true, dataType = "String", paramType = "query")
    @GetMapping("getWarehouseByIds")
    public List<WarehouseDTO> getWarehouseByIds(String ids){
        List<WarehouseDTO> result=new ArrayList<>();
        List<Integer> list=JSONArray.parseArray(ids,Integer.class);
        list.forEach(id->{
            result.add(this.basicsService.getByWarehouseId(id));
        });
        return result;
    }

    @ApiOperation(value = "根据sku列表获取库存")
    @ApiImplicitParam(name = "skus", value = "sku数组,json.tostring", required = true, dataType = "string", paramType = "query")
    @PostMapping("getsInvBySku")
    public List<OrderInvDTO> getsInvBySku(String skus){
        return this.nInventoryService.getsInvBySku(JSONArray.parseArray(skus,String.class));
    }

    @ApiOperation(value = "根据仓库账号id获取仓库信息")
    @ApiImplicitParam(name = "firmId", value = "仓库账号id", required = true, dataType = "string", paramType = "query")
    @PostMapping("getByFirmId")
    public WarehouseInitDTO getByFirmId(Integer firmId){
        return this.basicsService.getByFirmId(firmId);
    }

    @ApiOperation(value = "根据服务商编码获取下属所有仓库id")
    @ApiImplicitParam(name = "serviceCode", value = "服务商编码", required = true, dataType = "string", paramType = "query")
    @PostMapping("basic/getsByType")
    public List<Integer> getsByType(String serviceCode){
        return this.basicsService.getsByType(serviceCode);
    }


    @ApiOperation(value = "获取服务商名称")
    @ApiImplicitParam(name = "type", value = "1-公共,0-私营,空全部", dataType = "Integer",paramType = "query",required = true)
    @GetMapping("getsServiceName")
    public List<WarehouseServiceDTO> getsServiceName(Integer type){
        return this.basicsService.getsServiceName(type);
    }


    @ApiOperation(value = "获取服务商下所有账号")
    @ApiImplicitParam(name = "type", value = " 1  共有仓  2  私有仓  ", dataType = "Integer", paramType = "query")
    @GetMapping("basic/getAuth")
    public List<InitWarehouseDTO> getAuth(Integer type){
        return this.basicsService.getAuth(type);
    }


    @ApiOperation(value = "校验库存")
    @PostMapping(value = "inventory/checkInventory")
    public Boolean checkInventory(@RequestBody List<QueryInvReq> list){
        Map<String,Integer> map=new HashMap<>();
        List<String> skus=new ArrayList<>();
        Integer warehouseId=0;
        for (QueryInvReq req:list) {
            warehouseId=req.getWarehouseId();
            skus.add(req.getPinlianSku());
            map.put(req.getPinlianSku(),req.getQty());
        }
        List<InventoryDTO> invs=this.nInventoryService.getsBySku(warehouseId,skus);
        Boolean result=true;
        for (InventoryDTO dto:invs) {
            if (dto.getLocalAvailableQty()<map.get(dto.getPinlianSku())){
                return false;
            }
        }
        return result;
    }

    @ApiOperation(value = "根据仓库code查询绑定名称")
    @ApiImplicitParam(name = "codes", value = "仓库code json字符串",  dataType = "String", paramType = "query")
    @PostMapping("basic/getsNameByCode")
    public List<HouseTypeDTO> getsNameByCode(String codes){
        if (StringUtils.isEmpty(codes)){
            return new ArrayList<>();
        }
        Map<String,List<HouseNameDTO>> map=new HashMap<>(2);
        List<Integer> ids=JSONArray.parseArray(codes,Integer.class);
        for (Integer id:ids) {
            WarehouseDTO dto=this.basicsService.getByWarehouseId(id);
            if (!map.containsKey(dto.getFirmCode())){
                map.put(dto.getFirmCode(),new ArrayList<>());
            }
            map.get(dto.getFirmCode()).add(new HouseNameDTO(dto.getWarehouseId().toString(),dto.getWarehouseName()));
        }
        List<HouseTypeDTO> result=new ArrayList<>();
        map.forEach((k,v)->{
            result.add(new HouseTypeDTO(WarehouseFirmEnum.getByCode(k),v));
        });
        return result;
    }

    @ApiOperation(value = "根据账号名查询id")
    @ApiImplicitParam(name = "name", value = "账号名称",  dataType = "String", paramType = "query")
    @GetMapping("basic/getFirmByName")
    public Integer getFirmByName(String name){
        return this.basicsService.getFirmByName(name);
    }


    @ApiOperation(value = "根据sku列表获取库存(不需在同一个仓库)")
    @ApiImplicitParam(name = "skus", value = "sku数组", required = true, dataType = "string", paramType = "body")
    @PostMapping("getBySku")
    public List<InventoryDTO> getBySku(@RequestBody List<String> skus){
        return this.nInventoryService.getBySku(skus);
    }


    @ApiOperation(value = "谷仓库存变动监听")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appToken", value = "appToken", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pinlianSku", value = "pinlianSku", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "warehouseCode", value = "谷仓原始仓库code", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping("updateInventory")
    public void updateInventory(String appToken, String pinlianSku, String warehouseCode){
        logger.info("监听到谷仓库存变动:appToken={},pinlianSku={},warehouseCode={}",appToken,pinlianSku,warehouseCode);
        this.nInventoryService.updateGInventory(appToken,pinlianSku,warehouseCode);
    }

    @ApiOperation(value = "获取供应链公司绑定仓库服务商数")
    @ApiImplicitParam(name = "supplyId", value = "供应链公司id", required = true, dataType = "string", paramType = "query")
    @GetMapping("getBindAccount")
    public Integer getBindAccount(Integer supplyId){
        return this.basicsService.getBindService(supplyId);
    }


    @ApiOperation(value = "根据sku手动拉取库存")
    @ApiImplicitParam(name = "sku", value = "sku", required = true, dataType = "string", paramType = "query")
    @GetMapping("change")
    public void change(String sku){
        this.nInventoryService.change(sku);
    }

    @ApiOperation(value = "根据仓库id及品连sku获取库存状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseId", value = "仓库id", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pinlianSku", value = "品连sku", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "platform", value = "查侵权时传，平台，1：eBay，2：Amazon，3：wish，4：AliExpress", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "siteCode", value = "查侵权时传，站点编码", dataType = "string", paramType = "query")
    })
    @GetMapping("getSellerInv")
    public InvSellerReq getSellerInv(Integer warehouseId,String pinlianSku,Integer platform,String siteCode){
        InventoryDTO dto=this.nInventoryService.getByWPinlianSku(warehouseId,pinlianSku);
        InvSellerReq result=new InvSellerReq();
        if (dto!=null){
            result.setAvailableQty(dto.getAvailableQty());
            result.setWarnVal(dto.getWarnVal());
        }else {
            result=new InvSellerReq();
        }

        Object object=this.remoteCommodityService.getBySku(pinlianSku,platform,siteCode);
        if (object==null){
            return result;
        }
        JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
        if (jsonObject.getJSONObject("data")!=null){
            result.setCommodityPriceUs(jsonObject.getJSONObject("data").getString("commodityPriceUs"));
            result.setStatus(jsonObject.getJSONObject("data").getInteger("state"));
            result.setTortFlag(jsonObject.getJSONObject("data").getInteger("tortFlag"));
        }else {
            return null;
        }
        return result;
    }

    @ApiOperation(value = "获取有商品的仓库")
    @GetMapping("getAll")
    public List<KeyValueDTO> getAll(HttpServletRequest request){
        return this.nInventoryService.getAll(request.getHeader("i18n"));
    }

    @ApiOperation(value = "监听wms仓库库存")
    @PostMapping("monitorWms")
    public void monitorWms(String appKey,String sku,String warehouseCode){
        this.nInventoryService.monitorWms(appKey,sku,warehouseCode);
    }

    @ApiOperation(value = "修改本地待出货数量")
    @PostMapping("updateLocalShipping")
    public Integer updateLocalShipping(Integer warehouseId,String pinlianSku,Integer qty){
        logger.info("修改本地出货数量:warehouseId={},pinlianSku={},={}",warehouseId,pinlianSku,qty);
        return this.nInventoryService.updateLocalShipping(warehouseId,pinlianSku,qty);
    }

    @ApiOperation(value = "初始化sku绑定的卖家")
    @GetMapping("initBindSeller")
    public void initBindSeller(){
        this.nInventoryService.initBindSeller();
    }

    @ApiOperation(value = "sku绑定卖家")
    @PostMapping("bindSeller")
    public void bindSeller(@RequestBody List<String> skus){
        this.nInventoryService.bindSeller(skus);
    }

    @ApiOperation(value = "获取邮箱仓库的授权")
    @ApiImplicitParam(name = "type", value = "1  共有仓  2  私有仓  空  全部",  dataType = "Integer", paramType = "query")
    @GetMapping("getWarehouseInit")
    public List<InitWarehouseReq> getWarehouseInit(Integer type){
        List<InitWarehouseDTO> list=this.basicsService.getAuth(type);
        List<InitWarehouseReq> result=new ArrayList<>(list.size());
        list.forEach(dto -> {
            InitWarehouseReq req=new InitWarehouseReq();
            BeanUtils.copyProperties(dto,req);
            Map<String,Integer> map=new HashMap<>();
            dto.getItem().forEach(item -> {
                if (req.getSupplyId()==null){
                    WarehouseDTO warehouseDTO=this.basicsService.getByWarehouseId(item.getId());
                    req.setSupplyId(warehouseDTO.getSupplyId());
                }
                map.put(item.getCode(),item.getId());
            });
            req.setItems(map);
            result.add(req);
        });
        return result;
    }

    @ApiOperation(value = "根据code换id")
    @ApiImplicitParam(name = "warehouseCode", value = "仓库编码",  dataType = "String", paramType = "query")
    @GetMapping("getByAppTokenAndCode")
    public WarehouseDTO getByAppTokenAndCode (String warehouseCode){
        return this.basicsService.getByAppTokenAndCode(warehouseCode);
    }

    @ApiOperation(value = "订单运费试算")
    @PostMapping("orderLogistics")
    public LogisticsCostVo  orderLogistics(@RequestBody LogisticsCostVo cost){
        return this.logisticsService.orderLogistics(cost);
    }

    @ApiOperation(value = "初始话物流方式")
    @ApiImplicitParam(name = "firmId", value = "服务商账号id",  dataType = "Integer", paramType = "query")
    @GetMapping("logisticsInit")
    public void logisticsInit(Integer firmId){
        this.logisticsService.init(firmId);
    }

    @ApiOperation(value = "运费试算下拉")
    @PostMapping("getLogisticsSelect")
    public List<LogisticsSelectDTO> getLogisticsSelect(@RequestBody QuerySelectDTO dto){
        return this.logisticsService.getSelect(dto);
    }

    @ApiOperation(value = "运费试算下拉")
    @PostMapping("publishLogistics")
    public LogisticsPublishDTO publishLogistics(@RequestBody QuerySelectDTO dto){
        return this.logisticsService.publishLogistics(dto);
    }

    @ApiOperation(value = "根据物流方式获取平台的物流方式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "物流code",  dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "warehouseId", value = "发货仓库",  dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "platform", value = "平台 1-ebay 2-亚马逊 3-wish 4-速卖通",  dataType = "Integer", paramType = "query")
    })
    @GetMapping("getPlatLogisticsByCode")
    public LogisticsMapDTO getPlatLogisticsByCode(String code, Integer warehouseId, Integer platform){
        return this.logisticsService.getPlatLogisticsByCode(code,warehouseId,platform);
    }

}
