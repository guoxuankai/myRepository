package com.rondaful.cloud.commodity.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.constant.RedisKeyConstant;
import com.rondaful.cloud.commodity.dto.ExcelToPlatformSku;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.SellerSkuMap;
import com.rondaful.cloud.commodity.entity.SkuMapBind;
import com.rondaful.cloud.commodity.entity.SkuMapImport;
import com.rondaful.cloud.commodity.entity.SkuMapImportLog;
import com.rondaful.cloud.commodity.entity.SkuMapRule;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.mapper.SellerSkuMapMapper;
import com.rondaful.cloud.commodity.mapper.SkuMapBindMapper;
import com.rondaful.cloud.commodity.mapper.SkuMapImportLogMapper;
import com.rondaful.cloud.commodity.mapper.SkuMapImportMapper;
import com.rondaful.cloud.commodity.mapper.SkuMapRuleMapper;
import com.rondaful.cloud.commodity.rabbitmq.MQSender;
import com.rondaful.cloud.commodity.remote.RemoteSellerService;
import com.rondaful.cloud.commodity.service.ISellerSkuMapService;
import com.rondaful.cloud.commodity.utils.Utils;
import com.rondaful.cloud.commodity.vo.EmpowerRequestVo;
import com.rondaful.cloud.commodity.vo.OrderTransferVo;
import com.rondaful.cloud.commodity.vo.QuerySkuMapForOrderVo;
import com.rondaful.cloud.commodity.vo.SkuInventoryVo;
import com.rondaful.cloud.commodity.vo.SkuMapAddVo;
import com.rondaful.cloud.commodity.vo.SkuMapUpdateStatusVo;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.FileService;
import com.rondaful.cloud.common.utils.ExcelUtil;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RemoteUtil;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Service
public class SellerSkuMapServiceImpl implements ISellerSkuMapService {
	
	private static final Logger log = LoggerFactory.getLogger(SellerSkuMapServiceImpl.class);
   
	@Autowired
	private SellerSkuMapMapper sellerSkuMapMapper;
    
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
    
    @Resource
	private FileService fileService;
	
	@Value("${rondaful.system.env}")
	public String env;
	
	@Autowired
	private SkuMapImportMapper skuMapImportMapper;
	
	@Autowired
	private RemoteSellerService remoteSellerService;
	
	@Autowired
	private SkuMapImportLogMapper skuMapImportLogMapper;
	
	@Autowired
    private CommoditySpecMapper commoditySpecMapper;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private SkuMapBindMapper skuMapBindMapper;
	
	@Autowired
	private SkuMapRuleMapper skuMapRuleMapper;
	
	@Autowired
	private MQSender mqSender;



    @Override
    @Transactional(rollbackFor=Exception.class)
	public Map<String, Object> addByExcel(MultipartFile[] files) {
    	Map<String, Object> result=new HashMap<String, Object>();
    	AtomicInteger failNum=new AtomicInteger(0);//失败的数量
		AtomicInteger successNum=new AtomicInteger(0);//成功的数量
		int totalNum=0;//总数
		Long importId=0L;//文件导入ID
		
    	String optUser="";
    	UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	optUser=user.getUsername();
		}
        
        MultipartFile file = files[0];
        String taskName=file.getOriginalFilename();
		String fileName=Utils.createFileName(file.getOriginalFilename());
        String url ="";
		try {
			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put(fileName, file.getBytes());
			Map<String, String> urlMap=fileService.uploadMultipleFile(ConstantAli.getEnv(env), ConstantAli.getFolder("product"), fileMap, null, null, null);
			if (urlMap != null) {
				Iterator it = urlMap.entrySet().iterator();
				Map.Entry entry = (Map.Entry) it.next();
				url = (String) entry.getValue();
			}
		} catch (Exception e) {
			log.error("上传导入平台sku映射的Excel异常", e);
		}
		
		try {
			SkuMapImport im=new SkuMapImport();
			im.setFileName(taskName);
			im.setOptUser(optUser);
			im.setFileUrl(url);
			int n=skuMapImportMapper.insert(im);
			importId=im.getId();
			
			SkuMapImportLog importLog=null;
			String authorizationId="";
			Map<String, Object> empowerInfo=null;
			if (n>0) {
				ExcelUtil<SellerSkuMap> excelRead = new ExcelUtil<>(files[0].getInputStream());
				List<SellerSkuMap> list=excelRead.read(new ExcelToPlatformSku(), 2);
				if (list != null && list.size()>0) {
					totalNum=list.size();
					for (SellerSkuMap skuMap : list) {
						//校验平台
						Integer platform=getPlatformByCode(skuMap.getPlatform());
						if (platform == null) {
							addImportLog(importLog, im.getId(), skuMap.getPlatform(), skuMap.getSellerSelfAccount(),
									skuMap.getPlatformSku(),skuMap.getSkuGroup(),"失败","平台校验失败");
							failNum.incrementAndGet();
							continue;
						}
						
						//校验店铺 
						Integer status = null;
			        	Integer empowerId = null;
						empowerInfo=getEmpowerInfoByAccount(platform,skuMap.getSellerSelfAccount(),true);
						status=(Integer) empowerInfo.get("status");
						empowerId=(Integer) empowerInfo.get("empowerId");
						if (status != null && empowerId != null && status==1) {
							authorizationId=String.valueOf(empowerId);
						}else {
							addImportLog(importLog, im.getId(), skuMap.getPlatform(), skuMap.getSellerSelfAccount(),
									skuMap.getPlatformSku(),skuMap.getSkuGroup(),"失败","店铺授权不可用");
							failNum.incrementAndGet();
							continue;
						}
		                
						//校验唯一性
		                int uniqueNum=sellerSkuMapMapper.getUniqueNum(skuMap.getPlatform(), authorizationId, skuMap.getPlatformSku());
		                if (uniqueNum > 0) {
		                	addImportLog(importLog, im.getId(), skuMap.getPlatform(), skuMap.getSellerSelfAccount(),
									skuMap.getPlatformSku(),skuMap.getSkuGroup(),"失败",
									skuMap.getPlatform()+" "+skuMap.getSellerSelfAccount()+" "+skuMap.getPlatformSku()+"组合已存在");
		                	failNum.incrementAndGet();
							continue;
						}
		                
		                boolean skuExist=true;
		                String[] skuArr=skuMap.getSkuGroup().split("\\|");
		                if (skuArr == null || skuArr.length==0) {
		                	addImportLog(importLog, im.getId(), skuMap.getPlatform(), skuMap.getSellerSelfAccount(),
									skuMap.getPlatformSku(),skuMap.getSkuGroup(),"失败","品连sku及数量不能为空");
		                	failNum.incrementAndGet();
		        		}else {
		        			 for (int i = 0; i < skuArr.length; i++) {
		        				String[] skuGroupArr=skuArr[i].split(":");
		                    	if (skuGroupArr.length<2) {
		                    		addImportLog(importLog, im.getId(), skuMap.getPlatform(), skuMap.getSellerSelfAccount(),
											skuMap.getPlatformSku(),skuMap.getSkuGroup(),"失败","sku数量必须大于0");
				                	failNum.incrementAndGet();
				                	skuExist=false;
									break;
								}
		                    	
		                    	String systemSku=skuGroupArr[0];
		                    	int num=Integer.parseInt(skuGroupArr[1]);
		                    	
		                    	// 校验sku是否存在
		                		List<CommoditySpec> ll = commoditySpecMapper.page(new CommoditySpec(){{
		                                this.setSystemSku(systemSku);
		                        }});
		                        if (ll==null || ll.size()==0) {
		                        	addImportLog(importLog, im.getId(), skuMap.getPlatform(), skuMap.getSellerSelfAccount(),
											skuMap.getPlatformSku(),skuMap.getSkuGroup(),"失败","品连sku不存在");
				                	failNum.incrementAndGet();
				                	skuExist=false;
									break;
		                        }
		                        if (num==0) {
		                        	addImportLog(importLog, im.getId(), skuMap.getPlatform(), skuMap.getSellerSelfAccount(),
											skuMap.getPlatformSku(),skuMap.getSkuGroup(),"失败","sku数量必须大于0");
				                	failNum.incrementAndGet();
				                	skuExist=false;
									break;
								}
		            		}
						}
		                
		                if (skuExist) {
		                	//新增
			                skuMap.setAuthorizationId(authorizationId);
			                int m=sellerSkuMapMapper.insert(skuMap);
			                if (m>0) {
			                	addImportLog(importLog, im.getId(), skuMap.getPlatform(), skuMap.getSellerSelfAccount(),
										skuMap.getPlatformSku(),skuMap.getSkuGroup(),"成功","平台sku绑定成功");
			                	successNum.incrementAndGet();
			                	
			                	//插入sku及数量表
			                    for (int i = 0; i < skuArr.length; i++) {
			                    	String systemSku=skuArr[i].split(":")[0];
			                    	int num=Integer.parseInt(skuArr[i].split(":")[1]);
			                    	SkuMapBind bind=new SkuMapBind();
			                        bind.setMapId(skuMap.getId());
			                        bind.setSystemSku(systemSku);
			                        bind.setSkuNum(num);
			                        skuMapBindMapper.insert(bind);
			                        
			                        //如果不是other平台，发送转单MQ,916版本只考虑一个品连sku的情况
			                        if (!skuMap.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform()) ) {
			                        	OrderTransferVo transferVo=new OrderTransferVo();
			                        	Map<String, String> skuRelationMap=new HashMap<String, String>();
			                        	skuRelationMap.put(skuMap.getPlatformSku(), systemSku);
			                        	transferVo.setPlatform(skuMap.getPlatform());
			                        	transferVo.setSkuRelationMap(skuRelationMap);
			                        	transferVo.setEmpowerID(Integer.parseInt(skuMap.getAuthorizationId()));
			                        	mqSender.orderTransferMq(net.sf.json.JSONObject.fromObject(transferVo).toString());
			                		}
			            		}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("导入平台sku映射Excel异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "导入平台sku映射Excel异常");
		}
		
		result.put("total", totalNum);
		result.put("success", successNum);
		result.put("fail", failNum);
		result.put("importId", importId);
		return result;
	}
    
    /**
     * @Description:插入日志
     * @param importLog
     * @param importId
     * @param platform
     * @param empowerAccount
     * @param platformSku
     * @param systemSku
     * @param state
     * @param content
     * @return void
     * @author:范津
     */
    private void addImportLog(SkuMapImportLog importLog,Long importId,String platform,String empowerAccount,
    		String platformSku,String systemSku,String state,String content) {
    	
    	importLog=new SkuMapImportLog();
    	importLog.setImportId(importId);
		importLog.setPlatform(platform);
		importLog.setEmpowerAccount(empowerAccount);
		importLog.setPlatformSku(platformSku);
		importLog.setSystemSku(systemSku);
		importLog.setState(com.rondaful.cloud.common.utils.Utils.translation(state));
		importLog.setContent(com.rondaful.cloud.common.utils.Utils.translation(content));
		skuMapImportLogMapper.insert(importLog);
    }
    
    
    /**
     * @Description:返回平台对应的数字
     * @param platform
     * @return
     * @return Integer
     * @author:范津
     */
    private Integer getPlatformByCode(String platform) {
    	if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform()))
             return 1;
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform()))
            return 2;
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform()))
            return 3;
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform()))
            return 4;
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.WISH.getPlatform()))
            return 5;
        return null;
    }
    

	@Override
	public Page<SellerSkuMap> page(SellerSkuMap skuMap) {
		List<SellerSkuMap> resultList=null;
		try {
			List<Integer> authorizationIds=null;
			//获取有数据权限的店铺,数据权限在卖家接口做了
			RemoteUtil.invoke(remoteSellerService.getEmpowerSearchVO(new EmpowerRequestVo()));
	        List<Map> empowerList = RemoteUtil.getList();
	        if (empowerList != null && empowerList.size()>0) {
	        	authorizationIds=new ArrayList<Integer>();
	        	for (Map empower : empowerList) {
	        		authorizationIds.add((Integer) empower.get("empowerId"));
				}
	        }else {
	        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "卖家服务异常");
			}
	        
	        if (authorizationIds != null && authorizationIds.size()>0) {
	        	skuMap.setAuthorizationIds(authorizationIds);
			}
	        resultList=sellerSkuMapMapper.page(skuMap);
	        if (resultList != null && resultList.size()>0) {
	        	// 店铺ID换账号
				for (SellerSkuMap sellerSkuMap : resultList) {
					
					String account=(String) redisUtils.stringGet(RedisKeyConstant.KEY_EMPOWER_ACCOUNT_+sellerSkuMap.getAuthorizationId());
					
					if (StringUtils.isBlank(account)) {
						Map<String, Object> empowerInfo=getEmpowerInfoById(Integer.parseInt(sellerSkuMap.getAuthorizationId()));
						if (empowerInfo != null) {
							String empowerAccount = (String) empowerInfo.get("account");
							String selleAccount = (String) empowerInfo.get("selleAccount");
							account=empowerAccount+":"+selleAccount;
							if (StringUtils.isNotBlank(account) && StringUtils.isNotBlank(selleAccount)) {
								redisUtils.stringSet(RedisKeyConstant.KEY_EMPOWER_ACCOUNT_+sellerSkuMap.getAuthorizationId(),account, 86400L);//1day
							}
						}
					}
					//店铺账号
					sellerSkuMap.setSellerSelfAccount(account.split(":")[0]);
					//卖家账号
					if (account.split(":").length>1) {
						sellerSkuMap.setSellerPlAccount(account.split(":")[1]);
					}
				}
			}
	        
		} catch (Exception e) {
			log.error("查询平台sku映射列表异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询平台sku映射列表异常");
		}
		
		PageInfo pageInfo = new PageInfo(resultList);
	    return new Page(pageInfo);
	}
	
	
	/**
	 * 新增
	 */
	@Transactional(rollbackFor={GlobalException.class,RuntimeException.class,Exception.class})
	@Override
	public void addSkuMap(SellerSkuMap skuMap) {
		//1.校验平台
		Integer platform=getPlatformByCode(skuMap.getPlatform());
		if (platform == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台校验未通过");
		}
		
		//2.校验店铺 
		Integer status = null;
		Map<String, Object> empowerInfo=getEmpowerInfoById(Integer.parseInt(skuMap.getAuthorizationId()));
		status=(Integer) empowerInfo.get("status");
		if (status == null || status != 1) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "店铺授权不可用");
		}
        
		//3.校验唯一性
        int uniqueNum=sellerSkuMapMapper.getUniqueNum(skuMap.getPlatform(), skuMap.getAuthorizationId(), skuMap.getPlatformSku());
        if (uniqueNum > 0) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, 
        			com.rondaful.cloud.common.utils.Utils.translation(skuMap.getPlatform()+" "+skuMap.getAuthorizationId()+" "+skuMap.getPlatformSku()+"组合已存在"));
		}
        
        String[] skuArr=skuMap.getSkuGroup().split("\\|");
        if (skuArr == null || skuArr.length==0) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku及数量不能为空");
		}
        
        //4.新增
        if (skuMap.getStatus()==null) {
        	skuMap.setStatus(1);
		}
        sellerSkuMapMapper.insert(skuMap);
        
        
        //5.插入sku及数量表
        for (int i = 0; i < skuArr.length; i++) {
        	String systemSku=skuArr[i].split(":")[0];
        	int num=Integer.parseInt(skuArr[i].split(":")[1]);
        	
        	//6.校验sku是否存在
    		List<CommoditySpec> ll = commoditySpecMapper.page(new CommoditySpec(){{
                    this.setSystemSku(systemSku);
            }});
            if (ll==null || ll.size()==0) {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku不存在");
            }
            if (num==0) {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku数量必须大于0");
			}
        	
        	SkuMapBind bind=new SkuMapBind();
            bind.setMapId(skuMap.getId());
            bind.setSystemSku(systemSku);
            bind.setSkuNum(num);
            skuMapBindMapper.insert(bind);
            
            //如果不是other平台，发送转单MQ,916版本只考虑一个品连sku的情况
            if (!skuMap.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform()) ) {
            	OrderTransferVo transferVo=new OrderTransferVo();
            	Map<String, String> skuRelationMap=new HashMap<String, String>();
            	skuRelationMap.put(skuMap.getPlatformSku(), systemSku);
            	transferVo.setPlatform(skuMap.getPlatform());
            	transferVo.setSkuRelationMap(skuRelationMap);
            	transferVo.setEmpowerID(Integer.parseInt(skuMap.getAuthorizationId()));
            	mqSender.orderTransferMq(net.sf.json.JSONObject.fromObject(transferVo).toString());
    		}
		}
	}
	
	/**
	 * 编辑
	 */
	@Transactional(rollbackFor={RuntimeException.class,Exception.class})
	@Override
	public void updateSkuMap(SellerSkuMap skuMap) {
		//1.校验平台
		Integer platform=getPlatformByCode(skuMap.getPlatform());
		if (platform == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台校验未通过");
		}
		
		//2.校验店铺 
		Integer status = null;
		Map<String, Object> empowerInfo=getEmpowerInfoById(Integer.parseInt(skuMap.getAuthorizationId()));
		status=(Integer) empowerInfo.get("status");
		if (status == null || status != 1) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "店铺授权不可用");
		}	
        
		//3.校验唯一性
		SellerSkuMap oldMap=sellerSkuMapMapper.selectByPrimaryKey(skuMap.getId());
		if (oldMap == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku映射不存在");
		}
		if (!skuMap.getPlatform().equals(oldMap.getPlatform()) 
				|| !skuMap.getAuthorizationId().equals(oldMap.getAuthorizationId()) || !skuMap.getPlatformSku().equals(oldMap.getPlatformSku())) {
			int uniqueNum=sellerSkuMapMapper.getUniqueNum(skuMap.getPlatform(), skuMap.getAuthorizationId(), skuMap.getPlatformSku());
	        if (uniqueNum > 0) {
	        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, 
	        			com.rondaful.cloud.common.utils.Utils.translation(
	        					skuMap.getPlatform()+" "+skuMap.getAuthorizationId()+" "+skuMap.getPlatformSku()+"组合已存在"));
			}
		}
        
        String[] skuArr=skuMap.getSkuGroup().split("\\|");
        if (skuArr == null || skuArr.length==0) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku及数量不能为空");
		}
        
        //4.删除已有sku
        skuMapBindMapper.deleteByMapId(skuMap.getId());
        
        //5.插入sku及数量表
        for (int i = 0; i < skuArr.length; i++) {
        	String systemSku=skuArr[i].split(":")[0];
        	int num=Integer.parseInt(skuArr[i].split(":")[1]);
        	
        	//6.校验sku是否存在
    		List<CommoditySpec> ll = commoditySpecMapper.page(new CommoditySpec(){{
                    this.setSystemSku(systemSku);
            }});
            if (ll==null || ll.size()==0) {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku不存在");
            }
            if (num==0) {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku数量必须大于0");
			}
        	
        	SkuMapBind bind=new SkuMapBind();
            bind.setMapId(skuMap.getId());
            bind.setSystemSku(systemSku);
            bind.setSkuNum(num);
            skuMapBindMapper.insert(bind);
            
            //如果不是other平台，发送转单MQ,916版本只考虑一个品连sku的情况
            if (skuMap.getStatus()==1 && !skuMap.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform()) ) {
            	OrderTransferVo transferVo=new OrderTransferVo();
            	Map<String, String> skuRelationMap=new HashMap<String, String>();
            	skuRelationMap.put(skuMap.getPlatformSku(), systemSku);
            	transferVo.setPlatform(skuMap.getPlatform());
            	transferVo.setSkuRelationMap(skuRelationMap);
            	transferVo.setEmpowerID(Integer.parseInt(skuMap.getAuthorizationId()));
            	mqSender.orderTransferMq(net.sf.json.JSONObject.fromObject(transferVo).toString());
    		}
		}
        
        //7.更新
        skuMap.setVersion(oldMap.getVersion());
        sellerSkuMapMapper.updateByPrimaryKeySelective(skuMap);
		
	}
	

	
	/**
	 * @Description:根据店铺账号获取指定店铺的数据
	 * @param platform 平台 1 ebay   2 amazon 3 aliexpress 4other
	 * @param account 店铺账号
	 * @param hasPermission 是否需要数据权限，false:查全部
	 * @return
	 * @author:范津
	 */
	private Map<String, Object> getEmpowerInfoByAccount(Integer platform,String account,boolean hasPermission){
		Map<String, Object> result=new HashMap<String, Object>();
		Integer status=null;
		Integer empowerId=null;
		String selleAccount="";
		EmpowerRequestVo vo=new EmpowerRequestVo();
		vo.setPlatform(platform);
		vo.setAccount(account);
		if (!hasPermission) {
			vo.setDataType("10");
		}
		try {
			RemoteUtil.invoke(remoteSellerService.getEmpowerSearchVO(vo));
	        List<Map> empowerList = RemoteUtil.getList();
	        if (empowerList != null && empowerList.size()>0) {
	        	// 状态 0未授权   1 正常授权   2授权过期  3停用
	        	status=(Integer) ((Map) empowerList.get(0)).get("status");
	        	// 店铺(授权)ID
	        	empowerId=(Integer) ((Map) empowerList.get(0)).get("empowerId");
	        	// 卖家账号
	        	selleAccount= (String) ((Map) empowerList.get(0)).get("pinlianAccount");
	        }
		} catch (Exception e) {
			log.error("获取卖家服务店铺信息异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "卖家服务异常");
		}
		
        result.put("status", status);
        result.put("empowerId", empowerId);
        result.put("selleAccount", selleAccount);
        return result;
	}
	
	/**
	 * @Description:根据店铺ID获取指定店铺的数据
	 * @param empowerId 店铺ID
	 * @return
	 * @author:范津
	 */
	private Map<String, Object> getEmpowerInfoById(Integer empowerId){
		Map<String, Object> result=new HashMap<String, Object>();
		Integer status=null;
		String account="";
		String selleAccount="";
		EmpowerRequestVo vo=new EmpowerRequestVo();
		vo.setEmpowerId(empowerId);
		vo.setDataType("10");
		RemoteUtil.invoke(remoteSellerService.getEmpowerSearchVO(vo));
        List<Map> empowerList = RemoteUtil.getList();
        if (empowerList != null && empowerList.size()>0) {
        	// 状态 0未授权   1 正常授权   2授权过期  3停用
        	status=(Integer) ((Map) empowerList.get(0)).get("status");
        	// 店铺账号
        	account=(String) ((Map) empowerList.get(0)).get("account");
        	// 卖家账号
        	selleAccount= (String) ((Map) empowerList.get(0)).get("pinlianAccount");
        }
        result.put("status", status);
        result.put("account", account);
        result.put("selleAccount", selleAccount);
        return result;
	}

	@Transactional(rollbackFor={RuntimeException.class,Exception.class})
	@Override
	public void deleteByPrimaryKey(Long id) {
		skuMapBindMapper.deleteByMapId(id);
		sellerSkuMapMapper.deleteByPrimaryKey(id);
	}

	@Override
	public JSONObject getSkuNameAndSpec(String systemSku) {
		JSONObject json=null;
    	List<String> list=new ArrayList<String>();
    	list.add(systemSku);
		List<CommoditySpec> specList = commoditySpecMapper.selectCommoditySpecBySku(list);
	    if (specList != null && specList.size() > 0) {
	    	json=new JSONObject();
	    	CommoditySpec cs = specList.get(0);
	    	json.put("commodityNameCn", cs.getCommodityNameCn());
	    	json.put("commodityNameEn", cs.getCommodityNameEn());
	    	json.put("spu", cs.getSPU());
	    	json.put("commodityPriceUs", cs.getCommodityPriceUs());
	    	json.put("masterPicture", cs.getMasterPicture());
		    if (StringUtils.isNotBlank(cs.getWarehousePriceGroup())) {
		    	List<SkuInventoryVo> inventoryList=new ArrayList<SkuInventoryVo>();
		    	String[] priceArr=cs.getWarehousePriceGroup().split("\\|");
		    	for (int i = 0; i < priceArr.length; i++) {
		    		SkuInventoryVo vo=new SkuInventoryVo();
		    		vo.setWarehouseId(Integer.parseInt(priceArr[i].split(":")[0]));
		    		vo.setWarehousePrice(priceArr[i].split(":")[1]);
		    		inventoryList.add(vo);
				}
		    	if (inventoryList.size()>0) {
		    		json.put("inventoryList", inventoryList);
				}
			}
	    	
	    	StringBuilder specCnBuilder=new StringBuilder();
	    	StringBuilder specEnBuilder=new StringBuilder();
	    	if (StringUtils.isNotBlank(cs.getCommoditySpecEn())) {
	    		String[] specCnArray = cs.getCommoditySpec().split("\\|");
                for (int i = 0; i < specCnArray.length; i++) {
                	specCnBuilder.append(specCnArray[i].split(":")[1]).append(";");
                }
	    		String[] specEnArray = cs.getCommoditySpecEn().split("\\|");
                for (int i = 0; i < specEnArray.length; i++) {
                	specEnBuilder.append(specEnArray[i].split(":")[1]).append(";");
                }
            }else {//兼容历史数据
	        	if(StringUtils.isNotBlank(cs.getCommoditySpec())) {
	        		String[] specArray = cs.getCommoditySpec().split("\\|");
	                for (int i = 0; i < specArray.length; i++) {
	                    String[] arr = specArray[i].split(":");
	                    if (arr.length>1 && arr[1].contains("(") && arr[1].contains(")")) {
	                    	specCnBuilder.append(arr[1].substring(0, arr[1].indexOf("("))).append(";");
	                    	specEnBuilder.append(arr[1].substring(arr[1].indexOf("(") + 1, arr[1].lastIndexOf(")"))).append(";");
	                    }
	                }
	        	}
            }
	    	if (specCnBuilder.length()>0) {
	    		json.put("specValueCn", specCnBuilder.substring(0, specCnBuilder.length()-1));
			}
	    	if (specEnBuilder.length()>0) {
	    		json.put("specValueEn", specEnBuilder.substring(0, specEnBuilder.length()-1));
			}
	    }
	    return json;
	}

	@Override
	public JSONObject getSkuMapByPlatformSku(String platform,String platformSku,String authorizationId,boolean flag) {
		JSONObject result=null;
		SellerSkuMap map=new SellerSkuMap();
		map.setPlatform(platform);
		map.setPlatformSku(platformSku);
		map.setAuthorizationId(authorizationId);
		if (flag) {
			map.setStatus(1);
		}
		List<SellerSkuMap> list=sellerSkuMapMapper.page(map);
		if (list != null && list.size()>0) {
			result=new JSONObject();
			JSONArray skuArray=new JSONArray();
			List<SkuMapBind> skuBinds=list.get(0).getSkuBinds();
			for (SkuMapBind skuMapBind : skuBinds) {
				JSONObject skuInfo=getSkuNameAndSpec(skuMapBind.getSystemSku());
				skuInfo.put("skuNum", skuMapBind.getSkuNum());
				skuInfo.put("systemSku", skuMapBind.getSystemSku());
				skuArray.add(skuInfo);
			}
			result.put("id", list.get(0).getId());
			result.put("status", list.get(0).getStatus());
			result.put("skuList", skuArray);
			result.put("platform", list.get(0).getPlatform());
			result.put("authorizationId", list.get(0).getAuthorizationId());
			result.put("platformSku", list.get(0).getPlatformSku());
		}
		return result;
	}

	@Override
	public void exportImportLogExcel(Long importId, HttpServletResponse response) {
		try {
			SkuMapImport skuImport=skuMapImportMapper.selectByPrimaryKey(importId);
			
			List<SkuMapImportLog> list=skuMapImportLogMapper.selectByImportId(importId);
			if (list != null && list.size()>0) {
				for (SkuMapImportLog skuMapImportLog : list) {
					skuMapImportLog.setState(com.rondaful.cloud.common.utils.Utils.translation(skuMapImportLog.getState()));
					skuMapImportLog.setContent(com.rondaful.cloud.common.utils.Utils.translation(skuMapImportLog.getContent()));
				}
				String fileName = skuImport.getFileName();
	            Workbook workbook = ExcelExportUtil.exportBigExcel(new ExportParams(null, "Sheet1", ExcelType.HSSF), SkuMapImportLog.class, list);
	            ExcelExportUtil.closeExportBigExcel();
	            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
	            response.setContentType("application/x-download");
	            response.setCharacterEncoding("UTF-8");
	            try (OutputStream outputStream = response.getOutputStream()) {
	                workbook.write(outputStream);
	                workbook.close();
	            } catch (Exception e) {
	                log.error("导出异常", e.getMessage(), e);
	            }
			}
        } catch (Exception e) {
            log.error("平台sku映射导入结果明细导出异常", e);
        }
	}

	@Override
	public JSONArray getSkuMapForOrder(List<QuerySkuMapForOrderVo> voList) {
		JSONArray result=new JSONArray();
		try {
			//查询映射表是否存在
			for (QuerySkuMapForOrderVo vo : voList) {
				SellerSkuMap skuMap=sellerSkuMapMapper.getByUniqueKey(vo.getPlatform(), vo.getAuthorizationId(), vo.getPlatformSku());
				if (skuMap != null) {
					vo.setSkuBinds(skuMap.getSkuBinds());
				}else {
					if (StringUtils.isNotBlank(vo.getSellerId())) {
						//不存在，根据匹配规则截取出品连sku，再校验品连sku是否存在，存在则封装返回
						SkuMapRule mapRule=skuMapRuleMapper.selectBySellerId(vo.getSellerId());
						if (mapRule != null && mapRule.getStatus()==0) {//0 启用 1 停用
							String systemSku="";
							if ("splitByNum".equalsIgnoreCase(mapRule.getRuleType())) {
								if (StringUtils.isNotBlank(mapRule.getRule1()) && StringUtils.isNotBlank(mapRule.getRule2())) {
									int startIndex=Integer.valueOf(mapRule.getRule1())>0 ? Integer.valueOf(mapRule.getRule1())-1 : 0; 
									systemSku=vo.getPlatformSku().substring(
											startIndex, 
											Integer.valueOf(mapRule.getRule1()) + Integer.valueOf(mapRule.getRule2()) - 1);
								}
							}else if ("spliteByChar".equalsIgnoreCase(mapRule.getRuleType())) {
								if (StringUtils.isNotBlank(mapRule.getRule1())) {
				                    int i = vo.getPlatformSku().indexOf(mapRule.getRule1());
				                    if (i != -1) {
				                    	systemSku = vo.getPlatformSku().substring(i + 1);
				                    }
				                } else {
				                	systemSku = vo.getPlatformSku();
				                }
								
				                if (StringUtils.isNotBlank(mapRule.getRule2()) && StringUtils.isNotBlank(systemSku)) {
				                    int i = systemSku.lastIndexOf(mapRule.getRule2());
				                    if (i > 0) {
				                    	systemSku=systemSku.substring(0, i);
				                    }
				                }
							}
							
							if (StringUtils.isNotBlank(systemSku)) {
								List<String> list=new ArrayList<String>();
						    	list.add(systemSku);
								List<CommoditySpec> specList = commoditySpecMapper.selectCommoditySpecBySku(list);
							    if (specList != null && specList.size() > 0) {
							    	SkuMapBind skubind=new SkuMapBind();
							    	skubind.setSystemSku(systemSku);
							    	skubind.setSkuNum(1);
							    	List<SkuMapBind> skuMapBinds=new ArrayList<SkuMapBind>();
							    	skuMapBinds.add(skubind);
							    	vo.setSkuBinds(skuMapBinds);
							    }
							}
						}
					}
				}
				result.add(vo);
			}
		} catch (Exception e) {
			log.error("订单查询平台sku映射异常",e);
		}
		log.error("订单获取平台sku映射返回===》{}",JSON.toJSON(result).toString());
		return result;
	}

	@Override
	public void addSkuMapForSeller(List<SkuMapAddVo> voList) {
		for (SkuMapAddVo skuMapAddVo : voList) {
			if (StringUtils.isBlank(skuMapAddVo.getPlatform()) || StringUtils.isBlank(skuMapAddVo.getPlatformSku()) || StringUtils.isBlank(skuMapAddVo.getAuthorizationId())) {
        		log.error("卖家新增平台sku映射，参数错误");
        		continue;
    		}
    		if (StringUtils.isBlank(skuMapAddVo.getSkuGroup())) {
    			log.error("卖家新增平台sku映射，sku及数量不能为空");
        		continue;
    		}
    		//校验平台
    		Integer platform=getPlatformByCode(skuMapAddVo.getPlatform());
    		if (platform == null) {
    			log.error("卖家新增平台sku映射，平台校验未通过{}",skuMapAddVo.getPlatform());
    			continue;
    		}
    		//校验唯一性
            int uniqueNum=sellerSkuMapMapper.getUniqueNum(skuMapAddVo.getPlatform(), skuMapAddVo.getAuthorizationId(), skuMapAddVo.getPlatformSku());
            if (uniqueNum > 0) {
            	log.error("卖家新增平台sku映射，组合已存在{}",skuMapAddVo.getPlatform()+"_"+skuMapAddVo.getAuthorizationId()+"_"+skuMapAddVo.getPlatformSku());
            	continue;
    		}
    		
            String[] skuArr=skuMapAddVo.getSkuGroup().split("\\|");
            if (skuArr == null || skuArr.length==0) {
            	log.error("卖家新增平台sku映射，sku及数量不能为空{}",skuMapAddVo.getSkuGroup());
            	continue;
    		}
            //新增
            SellerSkuMap skuMap=new SellerSkuMap();
    		skuMap.setPlatform(skuMapAddVo.getPlatform());
    		skuMap.setAuthorizationId(skuMapAddVo.getAuthorizationId());
    		skuMap.setPlatformSku(skuMapAddVo.getPlatformSku());
    		skuMap.setSkuGroup(skuMapAddVo.getSkuGroup());
            skuMap.setStatus(1);
            sellerSkuMapMapper.insert(skuMap);
            
            boolean flag=false;
            //插入sku及数量表
            for (int i = 0; i < skuArr.length; i++) {
            	String systemSku=skuArr[i].split(":")[0];
            	int num=Integer.parseInt(skuArr[i].split(":")[1]);
            	
            	//校验sku是否存在
        		List<CommoditySpec> ll = commoditySpecMapper.page(new CommoditySpec(){{
                        this.setSystemSku(systemSku);
                }});
                if (ll==null || ll.size()==0) {
                	log.error("卖家新增平台sku映射，sku不存在{}",systemSku);
                	flag=true;
                	break;
                }
            	
            	SkuMapBind bind=new SkuMapBind();
                bind.setMapId(skuMap.getId());
                bind.setSystemSku(systemSku);
                bind.setSkuNum(num);
                skuMapBindMapper.insert(bind);
                
                //如果不是other平台，发送转单MQ,916版本只考虑一个品连sku的情况
                if (!skuMap.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform()) ) {
                	OrderTransferVo transferVo=new OrderTransferVo();
                	Map<String, String> skuRelationMap=new HashMap<String, String>();
                	skuRelationMap.put(skuMap.getPlatformSku(), systemSku);
                	transferVo.setPlatform(skuMap.getPlatform());
                	transferVo.setSkuRelationMap(skuRelationMap);
                	transferVo.setEmpowerID(Integer.parseInt(skuMap.getAuthorizationId()));
                	mqSender.orderTransferMq(net.sf.json.JSONObject.fromObject(transferVo).toString());
        		}
    		}
            
            if (flag) {
            	sellerSkuMapMapper.deleteByPrimaryKey(skuMap.getId());
			}
            
		}
	}

	@Override
	public void updateMapStatus(SkuMapUpdateStatusVo vo) {
		SellerSkuMap skuMap=sellerSkuMapMapper.selectByPrimaryKey(vo.getId());
		List<SkuMapBind> skuBinds=skuMap.getSkuBinds();
		
		if (skuBinds != null && skuBinds.size()>0 && vo.getStatus()==1 
				&& !skuMap.getPlatform().equalsIgnoreCase(OrderRuleEnum.platformEnm.OTHER.getPlatform()) ) {
			
			for (SkuMapBind skuMapBind : skuBinds) {
				OrderTransferVo transferVo=new OrderTransferVo();
	        	Map<String, String> skuRelationMap=new HashMap<String, String>();
	        	skuRelationMap.put(skuMap.getPlatformSku(), skuMapBind.getSystemSku());
	        	transferVo.setPlatform(skuMap.getPlatform());
	        	transferVo.setSkuRelationMap(skuRelationMap);
	        	transferVo.setEmpowerID(Integer.parseInt(skuMap.getAuthorizationId()));
	        	mqSender.orderTransferMq(net.sf.json.JSONObject.fromObject(transferVo).toString());
			}
		}
		
		sellerSkuMapMapper.updateStatus(vo.getId(),vo.getStatus());
	}

	
}
