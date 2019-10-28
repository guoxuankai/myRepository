package com.rondaful.cloud.commodity.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.commodity.entity.BindCategoryAliexpress;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.SkuImport;
import com.rondaful.cloud.commodity.entity.SkuOperateLog;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.enums.WarehouseFirmEnum;
import com.rondaful.cloud.commodity.mapper.BindCategoryAliexpressMapper;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.rabbitmq.MQSender;
import com.rondaful.cloud.commodity.remote.RemoteUserService;
import com.rondaful.cloud.commodity.service.SkuOperateLogService;
import com.rondaful.cloud.commodity.service.SkuOtherService;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RemoteUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
* @author:范津 
* @date:2019年6月1日 下午3:53:49
 */
@Api(description = "商品其他接口")
@RestController
public class OtherController extends BaseController{
	private final Logger log = LoggerFactory.getLogger(OtherController.class);

	@Autowired
	private SkuOtherService skuOtherService;
	
	@Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
	
	@Autowired
    private CommoditySpecMapper commoditySpecMapper;
	
	@Autowired
	private MQSender mqSender;
	
	@Autowired
	private RemoteUserService remoteUserService;
	
	@Autowired
	private BindCategoryAliexpressMapper bindCategoryAliexpressMapper;
	
	@Autowired
	private SkuOperateLogService skuOperateLogService;
	
	
	@PostMapping("/other/batchImportSku")
    @ApiOperation("sku批量导入")
	 @ApiImplicitParams({
         @ApiImplicitParam(name = "files", value = "文件", dataType = "File", paramType = "query",required=true),
         @ApiImplicitParam(name = "supplierId", value = "供应商ID", dataType = "Long", paramType = "query"),
         @ApiImplicitParam(name = "imType", value = "导入类型，1：新增，2：编辑", dataType = "Long", paramType = "query")})
    public void addSkuMapsByExcel(@RequestParam("files") MultipartFile[] files,Long supplierId,Integer imType) {
        //1.文件导入，先上传oss,再写入任务表
		//2.监听线程获取未执行导入的文件，解析excel，再新增商品入库
		skuOtherService.addImportTask(files,supplierId,imType);
    }
   
    /**
     * @Description:重新发送sku的MQ消息(供应商端有时会消费不到)
     * @param systemSkuList
     * @return void
     * @author:范津
     */
    @ApiOperation("重新发送sku的MQ消")
    @PostMapping("/other/reSendMq")
    public void reSendSkuMqMsg(@RequestBody List<String> systemSkuList) {
		List<CommoditySpec> commoditySpec=commoditySpecMapper.getSystemSkuBySystemSku(systemSkuList);
		if (commoditySpec!=null && commoditySpec.size()>0) {
			log.info("发送MQ给供应商服务====>{}",JSON.toJSON(commoditySpec).toString());
			constructionSupplierForSpec(commoditySpec);//先构造供应商信息
			mqSender.commoditySkuAdd(net.sf.json.JSONArray.fromObject(commoditySpec).toString());
		}
    }
    
   /**
    * @Description:全部sku重新推MQ
    * @return void
    * @author:范津
    */
    @ApiOperation("全部sku重新推MQ")
    @GetMapping("/other/reSendAllSkuMq")
    public void reSendAllSkuMqMsg() {
		List<CommoditySpec> commoditySpec=commoditySpecMapper.page(new CommoditySpec());
		if (commoditySpec!=null && commoditySpec.size()>0) {
			log.info("发送MQ给供应商服务====>{}",JSON.toJSON(commoditySpec).toString());
			constructionSupplierForSpec(commoditySpec);//先构造供应商信息
			mqSender.commoditySkuAdd(net.sf.json.JSONArray.fromObject(commoditySpec).toString());
		}
    }
    
    
    /**
     * 服务远程调用构造供应商信息
     *
     */
    public void constructionSupplierForSpec(List<CommoditySpec> commoditySpecs) {
        try {
            Set<Long> list = new HashSet<Long>() {{
                for (CommoditySpec cs : commoditySpecs) {
                    this.add(Long.valueOf(cs.getSupplierId()));
                }
            }};
            RemoteUtil.invoke(remoteUserService.getSupplierList(list, 0));
            List<Map> result = RemoteUtil.getList();
            if (result != null && !result.isEmpty()) {
                for (CommoditySpec cs : commoditySpecs) {
                    for (int i=0;i<result.size();i++) {
                        if (Long.valueOf((Integer) ((Map)result.get(i)).get("userId")) == Long.valueOf(cs.getSupplierId())) {
                            cs.setSupplierName((String) ((Map)result.get(i)).get("loginName"));
                            cs.setSupplierCompanyName((String) ((Map)result.get(i)).get("companyName"));
                        }
                    }
                }
            }
        } catch (Exception e) {
        	log.error(e.getMessage());
        }
    }
    
    @GetMapping("/operate/commodity/list/export")
    @ApiOperation(value = "商品导出", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category_level_1", value = "一级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_2", value = "二级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_3", value = "三级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "创建开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "创建结束时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "autiState", value = "商品状态", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "commodityName", value = "商品名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "systemSku", value = "系统sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplierSku", value = "供应商sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SPU", value = "系统spu", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplierId", value = "供应商ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "vendibilityPlatform", value = "可售平台", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "brandId", value = "所属品牌id", dataType = "Long", paramType = "query") })
    @AspectContrLog(descrption = "商品导出",actionType = SysLogActionType.QUERY)
    public void exportSku(Long category_level_1, Long category_level_2, Long category_level_3, String startTime, String endTime, 
    		Integer autiState, String commodityName, String systemSku, String supplierSku,String SPU,Long supplierId,
    		String vendibilityPlatform, Long brandId,
    		@ApiParam(name = "skuIds", value = "skuId数组", required = false) @RequestParam(value = "skuIds", required = false) List<Long> skuIds){
        
    	Map<String,Object> param=new HashMap<String,Object>();
        param.put("category_level_1", category_level_1);
        param.put("category_level_2", category_level_2);
        param.put("category_level_3", category_level_3);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        if (autiState != null) {
        	param.put("autiState", autiState);
		}
        param.put("systemSku", systemSku);
        param.put("supplierSku", supplierSku);
        param.put("SPU", SPU);
        param.put("supplierId", supplierId);
        param.put("vendibilityPlatform", vendibilityPlatform);
        param.put("brandId", brandId);
        if (skuIds != null && skuIds.size()>0) {
        	param.put("skuIds", skuIds);
		}/*else {
			if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "开始日期和结束日期必填");
			}
			int diffDay=DateUtils.getDiffDay(DateUtils.strToDate(startTime,DateUtils.FORMAT_3),DateUtils.strToDate(endTime,DateUtils.FORMAT_3));
			if (diffDay > 31) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "开始日期和结束日期区间不能大于31天");
			}
		}*/
        //断是中文还是英文商品名称搜索
        if (isEnNameSearch()) {
        	param.put("commodityNameEn", commodityName);
		}else {
			param.put("commodityNameCn", commodityName);
		}
        
        //判断是否登录
        UserDTO userDto=getLoginUserInformationByToken.getUserDTO();
        Integer userId=null;
        Integer topUserId=null;
        Integer platformType=null;
        String optUser="";
        if (userDto!=null) {
        	userId=userDto.getUserId();
        	topUserId=userDto.getTopUserId();
        	platformType=userDto.getPlatformType();
        	optUser=userDto.getLoginName();
        	
        	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDto.getPlatformType())) {//供应商平台
        		if (userDto.getManage()) {//主账号
        			param.put("supplierId", userDto.getUserId());
    			}else {
    				param.put("supplierId", userDto.getTopUserId());
				}
        	}else if (UserEnum.platformType.CMS.getPlatformType().equals(userDto.getPlatformType())) {//管理平台
        		if (!userDto.getManage()) {//不是管理平台的管理员(主账号),只能看到自己绑定供应商的商品
        			List<String> supplierIds=new ArrayList<String>();
        			List<UserAccountDTO> accountDTOs=userDto.getBinds();
        			for (UserAccountDTO dto : accountDTOs) {
						if (dto.getBindType().intValue()==0) {
							supplierIds.addAll(dto.getBindCode());
						}
					}
					if (supplierIds.size()>0) {
						param.put("supplierIds", supplierIds);
					}else {
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当前账号未绑定供应商，没有符合条件的数据");
					}
        		}
			}
		}
        //新线程调用
        final Integer fuserId=userId;
        final Integer ftopUserId=topUserId;
        final Integer fplatformType=platformType;
        final String foptUser=optUser;
        final Map<String,Object> fparam=param;
        Boolean isEn=isEnNameSearch();
        new Thread(){
			public void run() {
				skuOtherService.asynExportSku(fparam,isEn,fuserId,ftopUserId,fplatformType,foptUser);
			}
		}.start();
    }
    
    
    @ApiOperation(value = "删除导入任务", notes = "")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "任务ID", dataType = "Long", paramType = "path",required=true) })
    @DeleteMapping("/other/importTask/del/{id}")
    public void delTask(@PathVariable long id) {
    	skuOtherService.delTask(id);
    }
    
    @GetMapping("/other/importTask/list")
	@ApiOperation(value = "商品导入任务列表", notes = "")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
        @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
        @ApiImplicitParam(name = "taskName", value = "任务名称", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "status", value = "商品状态", dataType = "Long", paramType = "query"),
        @ApiImplicitParam(name = "startTime", value = "创建开始时间", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "创建结束时间", dataType = "string", paramType = "query")
      })
	public Page<SkuImport> listCategory(String page,String row,String taskName,Integer status,String startTime, String endTime) {
    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("page", page);
    	param.put("row", row);
    	param.put("taskName", taskName);
    	param.put("status", status);
    	param.put("startTime", startTime);
    	param.put("endTime", endTime);
    	String optUser=getLoginUserInformationByToken.getUserInfo().getUser().getUsername();
    	if (StringUtils.isBlank(optUser)) {
			return null;
		}
    	param.put("optUser", optUser);
    	
    	return skuOtherService.querySkuImportList(param);
	}
    
    @GetMapping("/other/importTaskLog/export/{id}")
    @ApiOperation(value = "sku导入结果明细导出", notes = "")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "任务ID", dataType = "Long", paramType = "path",required=true) })
    public void exportLog(@PathVariable long id){
        skuOtherService.exportImportLogExcel(id, response);
    }
    
    @PostMapping("/category/bind/Aliexpress")
	@ApiOperation(value = "绑定速卖通分类映射", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pinlianCategoty3Id", value = "品连三级分类ID", dataType = "int", paramType = "query", required = true),
			@ApiImplicitParam(name = "aliCategoryIds", value = "速卖通分类ID,多级用逗号分隔", dataType = "string", paramType = "query", required = true) })
	@AspectContrLog(descrption = "绑定速卖通分类映射", actionType = SysLogActionType.UDPATE)
	public void bindCategory(@ApiIgnore BindCategoryAliexpress bind) {
		if (bind.getPinlianCategoty3Id() == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连三级分类ID不能为空");
		if (StringUtils.isBlank(bind.getAliCategoryIds()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "速卖通分类ID不能为空");
		skuOtherService.addOrUpdateCategoryBindAli(bind);
	}
    
    @GetMapping("/category/getBind/Aliexpress")
	@ApiOperation(value = "获取速卖通分类绑定关系", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pinlianCategoty3Id", value = "品连三级分类ID", dataType = "int", paramType = "query", required = true) })
	@AspectContrLog(descrption = "获取速卖通分类绑定关系", actionType = SysLogActionType.QUERY)
	public BindCategoryAliexpress getCangCategoryBind(Long pinlianCategoty3Id) {
		if (pinlianCategoty3Id == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连三级分类ID不能为空");
		return bindCategoryAliexpressMapper.getBindByCategoryId(pinlianCategoty3Id);
	}
    
    
    @GetMapping("/other/skuLog")
	@ApiOperation(value = "获取sku操作日志", notes = "")
	@ApiImplicitParams({
		 	@ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
		 	@ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "systemSku", value = "品连sku", dataType = "string", paramType = "query", required = true) })
	public Page<SkuOperateLog> getSkuLogList(@RequestParam("systemSku") String systemSku,String page,String row) {
    	Page.builder(page,row);
		return skuOperateLogService.getLogList(systemSku);
	}
    
}
