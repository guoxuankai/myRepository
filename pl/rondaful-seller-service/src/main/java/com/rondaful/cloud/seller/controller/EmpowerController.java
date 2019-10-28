package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.EmpowerLog;
import com.rondaful.cloud.seller.remote.RemoteUserService;
import com.rondaful.cloud.seller.service.IEmpowerService;
import com.rondaful.cloud.seller.utils.AmazonBatchCopyUtils;
import com.rondaful.cloud.seller.vo.EmpowerSearchVO;
import com.rondaful.cloud.seller.vo.MarketplaceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 店铺授权
 */
@Api(description = "店铺授权")
@RestController
@RequestMapping("/empower")
public class EmpowerController extends BaseController{

	@Autowired
	private GetLoginUserInformationByToken getUserInfo;
	@Autowired
	private IEmpowerService empowerService;
	@Autowired
	private RemoteUserService remoteUserService;
	private final Logger logger = LoggerFactory.getLogger(EmpowerController.class);




	/**
	 * 查询所有账户
	 */
	@ApiOperation(value = "查询店铺授权日志", notes = "查询店铺授权日志")
	@GetMapping("/getEmpowerLogById")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "刊登账号id", required = true)
	})
	public List<EmpowerLog> getEmpowerLogById(Integer id){
		String handler = getUserInfo.getUserInfo().getUser().getLoginName();
		return empowerService.getEmpowerLogById(id,handler);
	}

	/**
	 * 查询所有账户
	 */
	@ApiOperation(value = "查询所有账户", notes = "查询所有账户")
	@PostMapping("/getEmpowerAll")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "platform", value = "平台 (1 ebay   2 amazon 3 aliexpress 4other) 不传查所有", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "status", value = "状态  （0未授权  1 正常授权  2授权过期 3停用4迁移）不传查所有", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pinlianId", value = "卖家账号id(为空当前账号，0全部 大于0查询你查询的用户)", required = false)
	})
	public List<Empower> getEmpowerAll(Integer platform,Integer status,Integer pinlianId){
		if(pinlianId==null){
			UserDTO userDTO = getUserInfo.getUserDTO();
			if(userDTO.getManage()){
				pinlianId = userDTO.getUserId();
			}else{
				pinlianId = userDTO.getTopUserId();
			}
		}else if(pinlianId==0){
			pinlianId = null;
		}
		return empowerService.getEmpowerAll(platform,status,pinlianId);
	}

	/**
	 * 分页查询授权列表信息
	 * @param vo
	 * @return
	 */
	@ApiOperation(value = "分页查询授权列表信息", notes = "分页查询授权列表信息")
	@PostMapping("/getEmpowerPage")
	public Page<Empower> getEmpowerPage(EmpowerSearchVO vo) {
		try {
			//数据权限
			if(!"10".equals(vo.getDataType())) {
				vo = getPermission(vo);
			}
			//设置默认分页页数
			if(vo.getPage()==null){
				vo.setPage(1);
			}
			if(vo.getRow()==null){
				vo.setRow(10);
			}
			vo.setRentType(1);
			Page<Empower> findAll = empowerService.getEmpowerPage(vo);
			return findAll;
		} catch (Exception e) {
			logger.error("查询授权列表失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询授权列表失败");
		}
	}


	/**
	 * 分页查询授权列表信息
	 * @param vo
	 * @return
	 */
	@ApiOperation(value = "查询授权店铺列表信息", notes = "查询授权店铺列表信息")
	@PostMapping("/getEmpowerSearchVO")
	public List<Empower> getEmpowerSearchVO(@RequestBody EmpowerSearchVO vo) {
		try {
			//数据权限
			if(!"10".equals(vo.getDataType())) {
				try
				{
					vo = getPermission(vo);
				}catch (Exception e){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406,"未登录");
				}
			}
			vo.setRentType(1);
			List<Empower> findAll = empowerService.getEmpowerVO(vo);
			return findAll;
		} catch (Exception e) {
			logger.error("查询授权列表失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询授权列表失败");
		}
	}



	/**
	 * 查询亚马逊所有欧洲站点的授权
	 * @param status 状态 0未授权  1 正常授权  2授权过期 3停用4迁移
	 * @param account 第三方账号
	 * @return 授权数据列表
	 */
	@ApiOperation(value = "多条件查询授权信息(数据权限控制)", notes = "多条件查询授权信息")
	@GetMapping("/selectEURByAccountDataLimit")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "status", value = "未授权  1 正常授权  2授权过期 3停用", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "account", value = "账号", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "thirdPartyName", value = "亚马逊卖家ID", required = true)
	})
	public List<Empower> selectEURByAccountDataLimit(Integer status,String account,String thirdPartyName) {
		try {
			EmpowerSearchVO empowerSearchVO = new EmpowerSearchVO();
			if(status != null)
				empowerSearchVO.setStatus(status);
			if(StringUtils.isNotBlank(account))
				empowerSearchVO.setAccount(account);
			empowerSearchVO.setThirdPartyName(thirdPartyName);
			empowerSearchVO = getPermission(empowerSearchVO);
			List<Empower> empowerVO = empowerService.getEmpowerVO(empowerSearchVO);
			ArrayList<Empower> resulte = new ArrayList<>();
			for(Empower emp: empowerVO){
				if(AmazonBatchCopyUtils.eurMarketplaces.contains(emp.getWebName())){
					resulte.add(emp);
				}
			}
			return resulte;
		} catch (Exception e) {
			logger.error("查询亚马逊所有欧洲站点的授权异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询亚马逊所有欧洲站点的授权异常");
		}
	}






	/**
	 * 店铺授权数据权限
	 * @param vo
	 * @return
	 */
	public EmpowerSearchVO getPermission(EmpowerSearchVO vo){
		UserDTO userDTO=getUserInfo.getUserDTO();//取出数据
		//外部平台调用
		if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
			if (userDTO.getManage()) {//主账号做的业务操作
				vo.setCmsBindCode(null);
			}else{
				if (userDTO.getBinds() == null) {
					return null;
				}
				List<String> cmsBindCode = Lists.newArrayList();
				for (UserAccountDTO dto:userDTO.getBinds()) {
					//取出数据权限卖家id
					if (UserEnum.platformType.SELLER.getPlatformType().equals(dto.getBindType())){
						cmsBindCode.addAll(dto.getBindCode());
					}
				}
				//赋值父id
				vo.setPinlianId(userDTO.getTopUserId());
				vo.setCmsBindCode(cmsBindCode);
				if(cmsBindCode==null || cmsBindCode.size()==0){
					return null;
				}
			}
		}else if (UserEnum.platformType.SELLER.getPlatformType().equals(userDTO.getPlatformType())) {//卖家平台账号进入
			if (!userDTO.getManage()) {//子账号
				vo.setPinlianId(userDTO.getTopUserId());
				vo.setBindCode(this.getEmpowerIds(userDTO.getBinds()));
				if(vo.getBindCode()==null || vo.getBindCode().size()==0){
					return null;
				}
			} else {//主账号
				vo.setPinlianId(userDTO.getUserId());
			}
		}else{
			return null;
		}
		return vo;
	}


	@ApiOperation(value = "亚马逊区域站点", notes = "亚马逊区域站点")
	@PostMapping("/getAmazonSite")
	public Map<String,List<MarketplaceVO>> getAmazonSite() {
		Map<String, MarketplaceId> map = MarketplaceIdList.createMarketplaceForKeyId();
		List<MarketplaceVO> marketplaceVOList1 = Lists.newArrayList();
		List<MarketplaceVO> marketplaceVOList2 = Lists.newArrayList();
		List<MarketplaceVO> marketplaceVOList3 = Lists.newArrayList();
		List<MarketplaceVO> marketplaceVOList4 = Lists.newArrayList();

		Map<String,List<MarketplaceVO>> mapMarketplaceVO = Maps.newHashMap();
		for (Map.Entry<String, MarketplaceId> entry : map.entrySet()) {
			MarketplaceId marketplace=entry.getValue();
			MarketplaceVO vo = new MarketplaceVO();
			vo.setCountryCode(marketplace.getCountryCode());
			vo.setCountryName(marketplace.getCountryName());
			vo.setMarketplaceId(marketplace.getMarketplaceId());
			vo.setUri(marketplace.getUri());
			if("GB".equals(marketplace.getCountryCode())
					||"FR".equals(marketplace.getCountryCode())
					||"DE".equals(marketplace.getCountryCode())
					||"ES".equals(marketplace.getCountryCode())
					||"IT".equals(marketplace.getCountryCode())){
				marketplaceVOList1.add(vo);
			}else if("US".equals(marketplace.getCountryCode())
					||"CA".equals(marketplace.getCountryCode())
					||"MX".equals(marketplace.getCountryCode())){
				marketplaceVOList2.add(vo);
			}else if("JP".equals(marketplace.getCountryCode())){
				marketplaceVOList3.add(vo);
			}else if("AU".equals(marketplace.getCountryCode())){
				marketplaceVOList4.add(vo);
			}
		}
		mapMarketplaceVO.put("1",marketplaceVOList1);
		mapMarketplaceVO.put("2",marketplaceVOList2);
		mapMarketplaceVO.put("3",marketplaceVOList3);
		mapMarketplaceVO.put("4",marketplaceVOList4);
		return mapMarketplaceVO;
	}

	/**
	 * 其他类店铺授权信息入库
	 * @param account
	 */
	@ApiOperation(value = "创建卖家账号新增其他授权信息", notes = "创建卖家账号新增其他授权信息")
	@PostMapping("/insertEmpowerOthers")
	public void insertEmpowerOthers(String account,Integer userId) {
		List<Empower> list = empowerService.checkEmpowerAccount(account,4,null);
		if(list!=null && list.size()>0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "店铺名称已存在!");
		}
		Empower empower = new Empower();
		empower.setAccount(account);
		empower.setPlatform(4);
		empower.setStatus(1);
		empower.setRentStatus(0);
		empower.setPinlianAccount(account);
		empower.setPinlianId(userId);
		empower.setParentAccount(account);
		empower.setRentType(1);
		Date date = new Date();
		empower.setAutoTime(date);
		empower.setCreateTime(date);
		empower.setUpdateTime(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(calendar.DAY_OF_YEAR, 5000);
		empower.setEndTime(calendar.getTime());
		empowerService.insertSelective(empower);
	}

	/**
	 * 店铺授权
	 * @param empower
	 */
	@ApiOperation(value = "店铺授权", notes = "店铺授权")
	@PostMapping("/insertEmpower")
	public Integer insertEmpower(Empower empower) {
		return empowerService.insertEmpower(empower);
	}

	/**
	 * 店铺授权
	 * @param empower
	 */
	@ApiOperation(value = "店铺重新授权", notes = "店铺重新授权")
	@PostMapping("/updateEmpower")
	public Integer updateEmpower(Empower empower) {
		//保存账号
		UserDTO userDTO = getUserInfo.getUserDTO();
		String pinlianAccount ="";
		String parentAccount ="";
		Integer userid = null;
		if (!userDTO.getManage()) {
			userid = userDTO.getTopUserId();
			pinlianAccount = userDTO.getTopUserLoginName();
			parentAccount = userDTO.getLoginName();
		} else {
			userid = userDTO.getUserId();
			pinlianAccount = userDTO.getLoginName();
			parentAccount = userDTO.getLoginName();
		}
		empower.setPinlianId(userid);
		empower.setPinlianAccount(pinlianAccount);
		empower.setParentAccount(parentAccount);
        empower.setRentType(1);
		return empowerService.updateEmpower(empower);
	}

	/**
	 * 编辑租赁账号主营类目和线上发货
	 * @param
	 */
	@ApiOperation(notes = "编辑租赁账号主营类目和线上发货", value = "编辑租赁账号主营类目和线上发货")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "int", name = "empowerId", value = "empowerId", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "int", name = "ebayEdis", value = "线上发货(0不启用1:erp  2:品连 3.卖家使用自己的edis)", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "categoryIds", value = "主营类目(多个用，隔开)", required = false)
	})
	@PostMapping("/updateEmpowerRentCategoryId")
	public void updateEmpowerRentCategoryId(Integer empowerId,Integer ebayEdis,String categoryIds) {
		Empower empower = new Empower();
		empower.setEmpowerId(empowerId);
		empower.setEbayEdis(ebayEdis);
		empower.setCategoryIds(categoryIds);
		empower.setUpdateTime(new Date());
		empowerService.updateByPrimaryKeySelective(empower);
	}

	/**
	 * 获取ebay请求的url
	 */
	@ApiOperation(value = "获取ebay授权的url", notes = "获取ebay授权的url")
	@GetMapping("/getEbayUrl")
	public String getEbayUrl() {
		try {
			String url = empowerService.getEbayUrl();
			String[] split = url.split("=");
			String sessionID = split[split.length -1];
			redisUtils.set("sessionID"+getUserInfo.getUserInfo().getUser().getUserid().toString(), sessionID,600L);
			return url;
		} catch (Exception e) {
			logger.error("获取ebay授权的url失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"获取ebay授权的url失败");
		}

	}




	@ApiOperation(value = "授权账号停用启用", notes = "授权账号停用启用")
	@PostMapping("/updateEmpowerByStatus")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "status", value = "1 启用  3停用", required = true)
	})
	public int updateEmpowerByStatus(Integer empowerId, Integer status) {
		return empowerService.updateEmpowerByStatus(empowerId,status);
	}



	/**
	 * 店铺详情
	 */
	@ApiOperation(value = "店铺详情", notes = "店铺详情")
	@GetMapping("/getEmpowerById")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "刊登账号id", required = true)
	})
	public Empower getEmpowerById(Integer id){
		return empowerService.getEmpowerById(id);
	}

	/**
     * 店铺详情
     */
    @ApiOperation(value = "亚马逊店铺详情", notes = "亚马逊店铺详情")
    @GetMapping("/getEmpowerByThirdPartyName")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "thirdPartyName", value = "亚马逊sellerid", required = true)
    })
    public List<Empower> getEmpowerByThirdPartyName(String thirdPartyName){
        UserDTO userDTO = getUserInfo.getUserDTO();
        Integer userid = null;
        if (!userDTO.getManage()) {
            userid = userDTO.getTopUserId();
        } else {
            userid = userDTO.getUserId();
        }
        EmpowerSearchVO vo = new EmpowerSearchVO();
        vo.setPinlianId(userid);
        vo.setThirdPartyName(thirdPartyName);
        return empowerService.getEmpowerVO(vo);
    }


    /**
     * 店铺详情
     */
    @ApiOperation(value = "未租赁店铺", notes = "未租赁店铺")
    @GetMapping("/getEmpowerByRent")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "type", value = "1用户权限，0没有权限", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "int", name = "platform", value = "平台 (1 ebay   2 amazon 3 aliexpress 4other)", required = false)
    })
    public List<Empower> getEmpowerByRent(int type,Integer platform){
        EmpowerSearchVO vo = new EmpowerSearchVO();
        if(type==1) {
            UserDTO userDTO = getUserInfo.getUserDTO();
            Integer userid = null;
            if (!userDTO.getManage()) {
                userid = userDTO.getTopUserId();
            } else {
                userid = userDTO.getUserId();
            }
            vo.setPinlianId(userid);
        }
        vo.setRentType(0);
		vo.setRentStatus(1);
		vo.setPlatform(platform);
        return empowerService.getEmpowerVO(vo);
    }




    @ApiOperation(value = "分页查询租赁店铺", notes = "分页查询租赁店铺")
    @PostMapping("/getEmpowerRentPage")
    public Page<Empower> getEmpowerRentPage(@RequestBody EmpowerSearchVO vo) {
        try {
            //设置默认分页页数
            if(vo.getPage()==null){
                vo.setPage(1);
            }
            if(vo.getRow()==null){
                vo.setRow(10);
            }
			vo.setOther(1);
            Page<Empower> findAll = empowerService.getEmpowerPage(vo);
            return findAll;
        } catch (Exception e) {
            logger.error("查询授权列表失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询授权列表失败");
        }
    }



    @ApiOperation(value = "新增租赁店铺", notes = "新增租赁店铺")
	@PostMapping("/insertEmpowerRent")
	public Integer insertEmpowerRent(@RequestBody  Empower empower) {
    	Date date = new Date();
		empower.setCreateTime(date);
		empower.setRentType(0);
		empower.setUpdateTime(date);
		empower.setRentStatus(1);
		return empowerService.insertSelective(empower);
	}


	@ApiOperation(value = "修改租赁店铺", notes = "修改租赁店铺")
	@PostMapping("/updateEmpowerRent")
	public Integer updateEmpowerRent(@RequestBody Empower empower) {
		Date date = new Date();
		empower.setUpdateTime(date);
		return empowerService.updateByPrimaryKeySelective(empower);
	}

	@ApiOperation(value = "迁移店铺", notes = "迁移店铺")
	@PostMapping("/updateMigrateEmpowerRent")
	public Integer updateMigrateEmpowerRent(Integer empowerId,Integer pinlianId,String pinlianAccount,String account,Integer platform) {
		empowerService.updateMigrateEmpowerRent(empowerId,pinlianId,pinlianAccount,account,platform);
		return 1;
	}

	@ApiOperation(value = "删除租赁店铺", notes = "删除租赁店铺")
	@PostMapping("/deleteEmpowerRent")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "int", name = "empowerId", value = "empowerId", required = true)
	})
	public Integer deleteEmpowerRent(Integer empowerId) {
		return empowerService.deleteByPrimaryKey(empowerId);
	}

	@ApiOperation(value = "验证账号是否重复", notes = "验证账号是否重复")
	@PostMapping("/checkEmpowerAccount")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "int", name = "empowerId", value = "empowerId", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "int", name = "platform", value = "平台 (1 ebay   2 amazon 3 aliexpress 4other)", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "account", value = "账号（自定义名称）,非amazon上的卖家账户，是用户自定义写的，与amazon账号无关", required = true)
	})
	public List<Empower> checkEmpowerAccount(String account, Integer platform,Integer empowerId) {
		return empowerService.checkEmpowerAccount(account,platform,empowerId);
	}

	@ApiOperation(value = "验证亚马逊站点的sellerid 是否重复", notes = "验证亚马逊站点的sellerid 是否重复")
	@PostMapping("/checkAccountWebName")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "int", name = "empowerId", value = "empowerId", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "webName", value = "亚马逊站点", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "thirdPartyName", value = "亚马逊店铺id", required = true)
	})
	public Integer checkAccountWebName(String webName, String thirdPartyName,Integer empowerId) {
    	if(StringUtils.isEmpty(webName)){
    		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "亚马逊站点为空");
		}
		if(StringUtils.isEmpty(thirdPartyName)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "亚马逊店铺id为空");
		}
		return empowerService.checkAccountWebName(webName, thirdPartyName,empowerId);
	}



	@ApiOperation(value = "验证账号paypal账号是否重复", notes = "验证账号paypal账号是否重复")
	@PostMapping("/checkEmpowerPaypal")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "int", name = "empowerId", value = "empowerId", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "int", name = "platform", value = "平台 (1 ebay   2 amazon 3 aliexpress 4other)", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "account", value = "账号（自定义名称）,非amazon上的卖家账户，是用户自定义写的，与amazon账号无关", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "paypalAccount01", value = "paypal账号", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "paypalAccount02", value = "paypal账号", required = false)
	})
	public Integer checkEmpowerPaypal(String account, Integer platform,Integer empowerId,String paypalAccount01,String paypalAccount02) {
		return empowerService.checkEmpowerPaypal(account, platform,empowerId,paypalAccount01,paypalAccount02);
	}

	@ApiOperation(value = "判断是否有租赁店铺", notes = "判断是否有租赁店铺")
	@PostMapping("/checkEmpowerRentStatus")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "int", name = "pinlianId", value = "卖家主账号id", required = true)
	})
	public Boolean checkEmpowerRentStatus(Integer pinlianId) {
    	if(pinlianId==null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "卖家主账号id为空");
		}
		EmpowerSearchVO vo = new EmpowerSearchVO();
		vo.setPinlianId(pinlianId);
		vo.setRentStatus(1);
		List<Empower> listEmpower = empowerService.getEmpowerVO(vo);
		if(listEmpower!=null && listEmpower.size()>0){
			return true;
		}else{
			return false;
		}
	}


}
