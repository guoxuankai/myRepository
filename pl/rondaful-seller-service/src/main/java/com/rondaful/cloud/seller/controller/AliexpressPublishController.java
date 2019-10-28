package com.rondaful.cloud.seller.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.security.UserSession;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.seller.common.aliexpress.AliexpressMethodNameEnum;
import com.rondaful.cloud.seller.common.aliexpress.HttpTaoBaoApi;
import com.rondaful.cloud.seller.common.aliexpress.JsonAnalysis;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import com.rondaful.cloud.seller.dto.AliexpressPublishListingDTO;
import com.rondaful.cloud.seller.dto.AliexpressPublishListingExcelDTO;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.aliexpress.*;
import com.rondaful.cloud.seller.enums.AliexpressEnum;
import com.rondaful.cloud.seller.enums.AliexpressOperationEnum;
import com.rondaful.cloud.seller.rabbitmq.AliexpressSender;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.service.IAliexpressListingService;
import com.rondaful.cloud.seller.service.IAliexpressPhotoBankService;
import com.rondaful.cloud.seller.service.IAliexpressPublishListingService;
import com.rondaful.cloud.seller.utils.GeneratePlateformSku;
import com.rondaful.cloud.seller.vo.AliexpressMoreVO;
import com.rondaful.cloud.seller.vo.AliexpressPublishListingSearchVO;
import com.rondaful.cloud.seller.vo.AliexpressPublishListingVO;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 基础数据controller
 * @author chenhan
 *
 */
@Api(description = "Aliexpress刊登接口")
@RestController
@RequestMapping("/aliexpress/publish")
public class AliexpressPublishController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(AliexpressPublishController.class);
	@Autowired
	private IAliexpressPublishListingService aliexpressPublishListingService;
	@Autowired
	private GetLoginUserInformationByToken getUserInfo;
	@Autowired
	private AuthorizationSellerService authorizationSellerService;
	@Autowired
	private AliexpressSender aliexpressSender;
	@Autowired
	private IAliexpressListingService aliexpressListingService;
	@Autowired
	private IAliexpressPhotoBankService aliexpressPhotoBankService;
	@Autowired
	private HttpTaoBaoApi httpTaoBaoApi;
	@Autowired
	private AliexpressConfig config;
	@AspectContrLog(descrption = "速卖通列表查询",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通列表数据查询 AliexpressPublishListingDTO", notes = "")
	@PostMapping("/findPage")
	public Page<AliexpressPublishListingDTO> findAll(AliexpressPublishListingSearchVO vo) {
		try {
			UserDTO userDTO = getUserInfo.getUserDTO();
			String headeri18n = request.getHeader("i18n");
			vo.setLanguage(headeri18n);
			//设置默认分页页数
			if(vo.getPage()==null){
				vo.setPage(1);
			}
			if(vo.getRow()==null){
				vo.setRow(10);
			}
			if(userDTO.getManage()){
				vo.setSellerId(userDTO.getUserId().toString());
			}else{
				vo.setSellerId(userDTO.getTopUserId().toString());
				vo.setEmpowerIds(this.getEmpowerIds(userDTO.getBinds()));
				if(vo.getEmpowerIds()==null || vo.getEmpowerIds().size()==0){
					return null;
				}
			}

			Page<AliexpressPublishListingDTO> findAll = aliexpressPublishListingService.findPage(vo);
			return findAll;
		} catch (Exception e) {
			logger.error("速卖通列表查询",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"速卖通列表查询");
		}
	}



	@ApiOperation(value = "速卖通导出", notes = "")
	@GetMapping("/excelAliexpress")
	public void excelAliexpress(AliexpressPublishListingSearchVO vo) {
		try {
			UserDTO userDTO = getUserInfo.getUserDTO();
			String headeri18n = request.getHeader("i18n");
			vo.setLanguage(headeri18n);

			if(userDTO.getManage()){
				vo.setSellerId(userDTO.getUserId().toString());
			}else{
				vo.setSellerId(userDTO.getTopUserId().toString());
				vo.setEmpowerIds(this.getEmpowerIds(userDTO.getBinds()));
				if(vo.getEmpowerIds()==null || vo.getEmpowerIds().size()==0){
					return ;
				}
			}

			List<AliexpressPublishListingExcelDTO> findAll = aliexpressPublishListingService.findAllExcel(vo);

			Workbook workbook = null;
			workbook = ExcelExportUtil.exportBigExcel(new ExportParams(null, "Sheet1", ExcelType.HSSF), AliexpressPublishListingExcelDTO.class, findAll);
			ExcelExportUtil.closeExportBigExcel();
			String dateStr = DateUtils.dateToString(new Date(), DateUtils.FORMAT_1);
			String filename = "attachment;filename=" + "Aliexpress"+dateStr+".xlsx";
			response.setHeader("Content-disposition", filename);
			response.setContentType("application/x-download");
			response.setCharacterEncoding("UTF-8");
			try (OutputStream outputStream = response.getOutputStream()) {
				workbook.write(outputStream);
				workbook.close();
			} catch (Exception e) {
				logger.error("导出异常:", e.getMessage(), e);
			}
		} catch (Exception e) {
			logger.error("导出异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"导出异常");
		}
	}



	@AspectContrLog(descrption="保存草稿",actionType= SysLogActionType.ADD)
	@PostMapping("/submitfeed-draft")
	@ApiOperation(value="保存草稿",notes="保存草稿,仅限于保存草稿，不参与预生成报文")
	public String submitfeedDraft(@RequestBody AliexpressPublishRequest prublishRequest)
	{
		if(UserSession.getUserBaseUserInfo()== null)
		{
			throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100406,"获取当前用户失败");
		}
		Empower empower = new Empower();
		if(prublishRequest.getEmpowerId()!=null){
			empower.setStatus(1);
			empower.setEmpowerId(prublishRequest.getEmpowerId().intValue());
			empower.setPlatform(3);//速卖通平台
			empower = authorizationSellerService.selectOneByAcount(empower);
			if( empower == null)
			{
				throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
			}
			prublishRequest.setPublishAccount(empower.getAccount());
		}
		String userLogin = UserSession.getUserBaseUserInfo().getUsername();
		//prublishRequest.setSellerId(UserSession.getUserBaseUserInfo().getUserid().toString());
		AliexpressPublishListing tempObj = null;
		if(prublishRequest.getId()==null){
			tempObj = aliexpressPublishListingService.insertPublishListing(prublishRequest,  userLogin, AliexpressEnum.AliexpressStatusEnum.DRAFT.getCode(), empower); //草稿
		}else{
			tempObj = aliexpressPublishListingService.updatePublishListing(prublishRequest,  userLogin,  AliexpressEnum.AliexpressStatusEnum.DRAFT.getCode(), empower); //草稿
		}
		//状态是上线和下线状态 队列刊登
		if(tempObj.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.SALE.getCode()
				|| tempObj.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.END.getCode()){
			AliexpressPublishModel model = aliexpressPublishListingService.getPublishModelById(tempObj.getId(),1);
			aliexpressSender.send(model);
		}
		return tempObj.getId()+"";
	}

	@AspectContrLog(descrption="发布刊登",actionType= SysLogActionType.ADD)
	@PostMapping("/submitfeed-save")
	@ApiOperation(value="发布刊登",notes="保存数据并且发布刊登")
	public String submitfeedSave(@RequestBody AliexpressPublishRequest prublishRequest)
	{
		if(UserSession.getUserBaseUserInfo()== null)
		{
			throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100406,"获取当前用户失败");
		}
		Empower empower = new Empower();
		if(prublishRequest.getEmpowerId()!=null) {
			empower.setStatus(1);
			empower.setEmpowerId(prublishRequest.getEmpowerId().intValue());
			empower.setPlatform(3);//速卖通平台
			empower = authorizationSellerService.selectOneByAcount(empower);
			if (empower == null) {
				throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100600, "找不到授权信息");
			}
			prublishRequest.setPublishAccount(empower.getAccount());
		}
		String userLogin = UserSession.getUserBaseUserInfo().getUsername();
		//prublishRequest.setSellerId(UserSession.getUserBaseUserInfo().getUserid().toString());
		AliexpressPublishListing tempObj = null;
		if(prublishRequest.getId()==null){
			tempObj = aliexpressPublishListingService.insertPublishListing(prublishRequest,  userLogin,  AliexpressEnum.AliexpressStatusEnum.PUBLISH.getCode(), empower); //刊登中
		}else{
			tempObj = aliexpressPublishListingService.updatePublishListing(prublishRequest,  userLogin,  AliexpressEnum.AliexpressStatusEnum.PUBLISH.getCode(), empower); //刊登中
		}
		// 刊登接口
		AliexpressPublishModel model = aliexpressPublishListingService.getPublishModelById(tempObj.getId(),1);
		aliexpressSender.send(model);
		return tempObj.getId()+"";
	}


	@AspectContrLog(descrption="速卖通刊登详情",actionType= SysLogActionType.QUERY)
	@PostMapping("/view-details/{id}")
	@ApiOperation("速卖通刊登详情数据")
	public AliexpressPublishModel viewDetailsById(@PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
		}

		AliexpressPublishModel model = aliexpressPublishListingService.getPublishModelById(id,1);
		if (model == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");
		}

		String sellerId = this.getSellerId().toString();
		if(!model.getSellerId().equalsIgnoreCase(sellerId))
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "无权限查看该数据");
		}

		return model;
	}

	@AspectContrLog(descrption="速卖通刊登编辑详情",actionType= SysLogActionType.QUERY)
	@PostMapping("/view-editdetails/{id}")
	@ApiOperation("速卖通刊登详情数据")
	public AliexpressPublishModel viewEditDetailsById(@PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
		}

		AliexpressPublishModel model = aliexpressPublishListingService.getPublishModelById(id,2);
		if (model == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");
		}

		String sellerId = this.getSellerId().toString();
		if(!model.getSellerId().equalsIgnoreCase(sellerId))
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "无权限查看该数据");
		}

		return model;
	}
	@AspectContrLog(descrption="单独的刊登",actionType= SysLogActionType.UDPATE)
	@PostMapping("/publish-submitfeed")
	@ApiOperation(value="单独的刊登    草稿后调用",notes="发布刊登，对已存在的数据进行发布，如果数据有更改，需要调用保存并刊登接口")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "刊登表id", required = true)
	})
	public void submitfeed(Long id)
	{
		try
		{
			if (id == null) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
			}
			AliexpressPublishModel model = aliexpressPublishListingService.getPublishModelById(id,1);
			if (model == null) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");
			}
			if(!(model.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.DRAFT.getCode()
					|| model.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.PUBLISH_FAILED.getCode())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "刊登状态不正确无法操作");
			}
			AliexpressPublishRequest publishRequest = new AliexpressPublishRequest();
			BeanUtils.copyProperties(model, publishRequest);
			aliexpressPublishListingService.check(publishRequest,false,null);

			//t刊登操作
			if(model!=null) {
				Empower empower = new Empower();
				empower.setStatus(1);
				empower.setEmpowerId(model.getEmpowerId().intValue());
				empower.setPlatform(3);//速卖通平台
				empower = authorizationSellerService.selectOneByAcount(empower);
				if(empower == null)
				{
					throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
				}
				model.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.PUBLISH.getCode());
				//刊登接口
				aliexpressSender.send(model);

				AliexpressPublishListing updateModel = new AliexpressPublishListing();
				updateModel.setId(model.getId());
				updateModel.setPublishStatus(AliexpressEnum.AliexpressStatusEnum.PUBLISH.getCode());
				aliexpressPublishListingService.updateByPrimaryKeySelective(updateModel);

				//刊登操作日志
				UserDTO userDTO = getUserInfo.getUserDTO();
				aliexpressPublishListingService.insertAliexpressOperationLog(model.getId(),
						userDTO.getUserName(),AliexpressOperationEnum.RELIST.getCode(),"发布刊登",Long.valueOf(userDTO.getUserId()));
			}

		}catch (GlobalException e) {
			throw e;
		}catch(Exception e)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
	}


	@AspectContrLog(descrption="删除速卖通刊登",actionType= SysLogActionType.DELETE)
	@DeleteMapping("/delete/{id}")
	@ApiOperation("删除速卖通刊登")
	public String delete(@ApiParam(value = "被删除的id", name = "id", required = true) @PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "被删除的id不能为空");
		}
		AliexpressPublishListing publishListing = aliexpressPublishListingService.getAliexpressPublishListingById(id);
		if (publishListing == null) {
			return "false";
		}
		String sellerId = this.getSellerId().toString();
		if(!publishListing.getSellerId().equalsIgnoreCase(sellerId)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能删除其他用户的刊登");
		}
		if(publishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.SALE.getCode()
				|| publishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.END.getCode()
				|| publishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.PUBLISH.getCode()
				|| publishListing.getPublishStatus()==AliexpressEnum.AliexpressStatusEnum.AUDIT.getCode()){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登状态不能删除");
		}
		try {
			aliexpressPublishListingService.deleteAliexpressPublishListing(id);
			//刊登操作日志
			UserDTO userDTO = getUserInfo.getUserDTO();
			aliexpressPublishListingService.insertAliexpressOperationLog(publishListing.getId(), userDTO.getUserName(),AliexpressOperationEnum.DELETE_PUBLISH.getCode(),"删除",Long.valueOf(userDTO.getUserId()));
		} catch (Exception e) {
			logger.error("删除速卖通刊登异常", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "删除速卖通刊登异常");
		}
		return "true";
	}

	@AspectContrLog(descrption="复制速卖通刊登",actionType= SysLogActionType.ADD)
	@PostMapping("/copyAliexpress/{id}")
	@ApiOperation("复制速卖通刊登")
	public void copy(@ApiParam(value = "被复制的id", name = "id", required = true) @PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "被复制的id不能为空");
		}
		AliexpressPublishListing publishListing = aliexpressPublishListingService.getAliexpressPublishListingById(id);
		if (publishListing == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "被复制的id不存在");
		}
		String sellerId = this.getSellerId().toString();
		if(!publishListing.getSellerId().equalsIgnoreCase(sellerId)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能复制其他用户的刊登");
		}
		try {
			aliexpressPublishListingService.insertcopyAliexpressPublish(id,1,null,null);
			//刊登操作日志
			UserDTO userDTO = getUserInfo.getUserDTO();
			aliexpressPublishListingService.insertAliexpressOperationLog(publishListing.getId(),
					userDTO.getUserName(),AliexpressOperationEnum.COPY.getCode(),"",Long.valueOf(userDTO.getUserId()));

		}catch (GlobalException e) {
			throw e;
		} catch (Exception e) {
			logger.error("复制速卖通刊登异常", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "复制速卖通刊登异常");
		}
	}
	@AspectContrLog(descrption="修改备注",actionType= SysLogActionType.UDPATE)
	@PostMapping("/udpateAliexpress-remark")
	@ApiOperation("修改备注")
	public void  udpateRemarkById(@ApiParam(value = "当前数据的id", name = "id", required = true) @RequestParam Long id,
								  @ApiParam(value = "备注", name = "remark", required = true) @RequestParam String  remark) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
		}
		AliexpressPublishListing publishListing = aliexpressPublishListingService.getAliexpressPublishListingById(id);
		if (publishListing == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");
		}
		AliexpressPublishListing updateObj = new AliexpressPublishListing();
		try {
			updateObj.setId(id);
			updateObj.setRemark(remark);
			int rows = aliexpressPublishListingService.updateByPrimaryKeySelective(updateObj);
			if(rows <= 0) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "更新记录数0条");
			}
		}catch (GlobalException e) {
			throw e;
		} catch (Exception e) {
			logger.error("修改备注失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "更新记录失败");
		}
	}
	@AspectContrLog(descrption="查看刊登操作日志",actionType= SysLogActionType.QUERY)
	@PostMapping("/viewAliexpress-log/{id}")
	@ApiOperation(value="查看刊登操作日志" ,notes="msgType")
	public List<AliexpressOperationLog> viewAliexpressLogById(
			@ApiParam(value = "当前数据的id", name = "id", required = true) @PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
		}
		AliexpressOperationLog subListQuery = new AliexpressOperationLog();
		subListQuery.setListingId(id);
		List<AliexpressOperationLog> result =  aliexpressPublishListingService.getAliexpressOperationLogBylistingId(id);
		return result;
	}
	@AspectContrLog(descrption="查看刊登错误日志",actionType= SysLogActionType.QUERY)
	@PostMapping("/viewAliexpress-errorLog/{id}")
	@ApiOperation(value="查看刊登错误日志" ,notes="msgType")
	public List<AliexpressPublishListingError> getAliexpressPublishListingErrorBylistingId(
			@ApiParam(value = "当前数据的id", name = "id", required = true) @PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
		}

		List<AliexpressPublishListingError> result =  aliexpressPublishListingService.getAliexpressPublishListingErrorBylistingId(id);
		return result;
	}




	@AspectContrLog(descrption="修改刊登价格和库存",actionType= SysLogActionType.UDPATE)
	@PostMapping("/udpateAliexpressProduct")
	@ApiOperation("修改刊登价格和库存")
	public void  udpateAliexpressProduct(@RequestBody AliexpressPublishUpdateRequest request) {
		if (request ==null || request.getId() == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
		}
		AliexpressPublishListing publishListing = aliexpressPublishListingService.getAliexpressPublishListingById(request.getId());
		if (publishListing == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");
		}
		String msg="";
		String type="";
		try {
			aliexpressPublishListingService.updateAliexpressPublishListingProduct(request);
			//1价格2库存
			if(request.getType()==1){
				msg="速卖通刊登价格修改";
				type = AliexpressOperationEnum.UPDATE_PRICE.getCode();
			}else if(request.getType()==2){
				msg="速卖通刊登库存修改";
				type = AliexpressOperationEnum.UPDATE_INVENTORY.getCode();
			}
			//刊登操作日志
			UserDTO userDTO = getUserInfo.getUserDTO();
			aliexpressPublishListingService.insertAliexpressOperationLog(publishListing.getId(),
					userDTO.getUserName(),type,msg,Long.valueOf(userDTO.getUserId()));
		} catch (GlobalException e) {
			throw e;
		} catch (Exception e) {
			logger.error("修改刊登价格和库存失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "修改刊登价格和库存失败");
		}
	}

    @AspectContrLog(descrption="速卖通刊登上线下线",actionType= SysLogActionType.UDPATE)
    @ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "刊登表id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "ids", value = "刊登表id list 数据", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "type", value = "类型1上线2下线", required = true)
    })
    @PostMapping("/udpateAliexpressPublishListing")
    @ApiOperation("速卖通刊登上线下线")
    public void  udpateAliexpressPublishListing(AliexpressPublishListingVO vo) {
		if(vo.getId()!=null && vo.getId()>0){
			vo.getIds().add(vo.getId());
		}
		for(Long id:vo.getIds()){
			aliexpressPublishListingService.udpateAliexpressPublishListing(id,vo.getType());
		}
    }


	@AspectContrLog(descrption="同步速卖通刊登商品",actionType= SysLogActionType.UDPATE)
	@PostMapping("/syncAliexpressListingIds")
	@ApiOperation("同步速卖通刊登商品")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "ids", value = "刊登表id list 数据", required = true)
	})
	public Integer syncAliexpressListingIds(AliexpressPublishListingVO vo) {
		int countnum =0;
		Integer sellerId = null;
		UserDTO userDTO = getUserInfo.getUserDTO();
		if (userDTO.getManage()) {
			sellerId = userDTO.getUserId();
		} else {
			sellerId = userDTO.getTopUserId();
		}
		for (Long id:vo.getIds()) {
			if (id == null) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
			}
			AliexpressPublishListing publishListing = aliexpressPublishListingService.getAliexpressPublishListingById(id);
			if (publishListing == null || publishListing.getItemId()==null) {
				continue;
			}
			countnum++;
			if (!publishListing.getSellerId().equalsIgnoreCase(sellerId.toString())) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "无权限查看该数据");
			}
			try {
				aliexpressListingService.updateAliexpressListing(publishListing.getEmpowerId(), userDTO.getUserId().longValue(), userDTO.getLoginName(), sellerId.longValue(), null, publishListing.getItemId(), null, null);

			}catch (GlobalException e){
				throw e;
			}catch (Exception e) {
				logger.error("同步速卖通刊登商品异常", e);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "同步速卖通刊登商品异常");
			}
		}
		return countnum;
	}


	@AspectContrLog(descrption="同步速卖通Listing",actionType= SysLogActionType.QUERY)
	@PostMapping("/syncAliexpressPListing")
	@ApiOperation(value="同步速卖通Listing",notes="")
	public Long syncAliexpressListing(){
		UserDTO userDTO = getUserInfo.getUserDTO();
		Long sellerId;
		if(userDTO.getManage()){
			sellerId = userDTO.getUserId().longValue();
		}else{
			sellerId = userDTO.getTopUserId().longValue();
		}
		String key ="aliexpress-syncListing"+ sellerId;
		if (redisUtils.exists(key)){
			return 0L;
		}else {
			redisUtils.set(key,1,600L);//十分钟不能重复请求
		}

		Long productCount = 0L;
		Empower queryEmpower = new Empower();
		queryEmpower.setPinlianId(sellerId.intValue());
		queryEmpower.setPlatform(3);
		queryEmpower.setStatus(1);
		List<Empower> listEmpower = authorizationSellerService.selectObjectByAccountDataLimit(queryEmpower);
		if(listEmpower!=null && listEmpower.size()>0){

			for(Empower empower:listEmpower){
				Long count = aliexpressListingService.syncAliexpressPListingProductStatus(empower.getEmpowerId().longValue(),sellerId,
						userDTO.getUserId().longValue(),userDTO.getLoginName());
				productCount = productCount+count;
			}


		}else {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登账号为空");
		}

		return productCount;
	}


	@AspectContrLog(descrption="速卖通刊登一键延长有效期",actionType= SysLogActionType.UDPATE)
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "ids", value = "刊登表id list 数据", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "type", value = "类型0全部1部分", required = true)
	})
	@PostMapping("/renewexpire")
	@ApiOperation("速卖通刊登一键延长有效期")
	public Integer renewexpire(AliexpressPublishListingVO vo) {
		Long sellerId = this.getSellerId().longValue();

		if(vo.getType()==0){
			Empower queryEmpower = new Empower();
			queryEmpower.setPinlianId(sellerId.intValue());
			queryEmpower.setPlatform(3);
			queryEmpower.setStatus(1);
			List<Empower> listEmpower = authorizationSellerService.selectObjectByAccountDataLimit(queryEmpower);
			if(listEmpower!=null && listEmpower.size()>0){
				Map<Long,String> map = Maps.newHashMap();
				for(Empower empower:listEmpower){
					Map<Long,String> retMap = this.syncAliexpressPListing(empower.getEmpowerId().longValue(),sellerId,"onSelling");
					map.putAll(retMap);
				}
				for (Map.Entry<Long,String> val :map.entrySet()){
					this.publishRenewexpire(null,val.getKey(),val.getValue());
				}
				return map.size();
			}else {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登账号为空");
			}
		}else {
			for(Long id:vo.getIds()){
				AliexpressPublishListing publishListing = aliexpressPublishListingService.getAliexpressPublishListingById(id);
				if (publishListing != null) {
					this.publishRenewexpire(id,publishListing.getItemId(),aliexpressPhotoBankService.getEmpowerById(publishListing.getEmpowerId()));
				}
			}
			return vo.getIds().size();
		}


	}
	private void publishRenewexpire(Long id,Long itemId,String token){
		Map<String,Object> map = Maps.newHashMap();
		map.put("sessionKey",token);
		map.put("productId",itemId);
		httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.RENEWEXPIRE.getCode(),map);
//		Map<String,Object> retmap = JsonAnalysis.getGatewayMsg(json);
//		String success = retmap.get("success").toString();
//		if("200".equals(success)){
//
//		}
	}


	private Map<Long,String> syncAliexpressPListing(Long empowerId,Long sellerId,String productStatus){
		Map<Long,String> retMap = Maps.newHashMap();
		Map<String,Object> map = Maps.newHashMap();
		String token = aliexpressPhotoBankService.getEmpowerById(empowerId);
		map.put("sessionKey",token);
		map.put("pageSize",100L);
		map.put("currentPage",1L);
		map.put("productStatusType",productStatus);
		String json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.FINDPRODUCTPAGE.getCode(),map);
		Map<String,Object> retmap = JsonAnalysis.getGatewayMsg(json);
		String success = retmap.get("success").toString();

		if("200".equals(success)){
			AliexpressProductListModel model = JSONObject.parseObject(retmap.get("data").toString(), AliexpressProductListModel.class);

			if(model!=null) {
				if(model.getAeopAEProductDisplayDTOList()!=null){
					for(AliexpressProductModel aliexpressProductModel : model.getAeopAEProductDisplayDTOList()){
						retMap.put(aliexpressProductModel.getProductId(),token);
					}
				}

				if(model.getTotalPage()>1) {
					for (long i = 2; i <= model.getTotalPage(); i++) {
						map.put("currentPage", i);
						json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.FINDPRODUCTPAGE.getCode(), map);
						retmap = JsonAnalysis.getGatewayMsg(json);
						success = retmap.get("success").toString();

						if ("200".equals(success)) {
							model = JSONObject.parseObject(retmap.get("data").toString(), AliexpressProductListModel.class);


							if(model.getAeopAEProductDisplayDTOList()!=null){
								for(AliexpressProductModel aliexpressProductModel : model.getAeopAEProductDisplayDTOList()){
									retMap.put(aliexpressProductModel.getProductId(),token);
								}
							}
						}
					}
				}
			}
		}
		return retMap;
	}


	private Integer getSellerId(){
        Integer userId = null;
        UserDTO userDTO = getUserInfo.getUserDTO();
        if(userDTO.getManage()){
            userId = userDTO.getUserId();
        }else{
            userId = userDTO.getTopUserId();
        }
        return userId;
    }


	@AspectContrLog(descrption="速卖通商品违禁词查询",actionType= SysLogActionType.QUERY)
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "categoryId", value = "分类id", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "title", value = "标题", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "keywords", value = "关键字", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "productProperties", value = "商品类目属性", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "detail", value = "商品的详细描述", required = false)
	})
	@PostMapping("/findaeproductprohibitedwords")
	@ApiOperation("速卖通刊登商品违禁词查询")
	public AliexpressProhibitedWordsResult  findaeproductprohibitedwords(Long empowerId,String categoryId,String title,String keywords,String productProperties,String detail) {
		Map<String,String> map = Maps.newHashMap();
		map.put("sessionKey",aliexpressPhotoBankService.getEmpowerById(empowerId));
		map.put("categoryId",categoryId);
		map.put("title",title);
		map.put("keywords",keywords);
		map.put("productProperties",productProperties);
		map.put("detail",detail);
		String url = config.getAliexpressUrl()+"/api/aliexpress/findaeproductprohibitedwords";
		String json = HttpUtil.post(url, map);
		//String json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.FINDAEPRODUCTPROHIBITEDWORDS.getCode(),map);
		Map<String,Object> retMap = JsonAnalysis.getGatewayMsg(json);
		String success = retMap.get("success").toString();
		if("200".equals(success)){
			AliexpressProhibitedWordsResult result =JSONObject.parseObject(retMap.get("data").toString(), AliexpressProhibitedWordsResult.class);
			return result;
		}else{
			String msg = retMap.get("msg").toString();
			throw new GlobalException(success, msg);
		}
	}

	@AspectContrLog(descrption="速卖通平台sku生成",actionType= SysLogActionType.QUERY)
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "sku", value = "skus多个用,号格开", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Long", name = "publishListingId", value = "屏蔽重复数据的id", required = false)
	})
	@PostMapping("/findPlatformSku")
	@ApiOperation("速卖通平台sku生成")
	public Map<String,String> findPlatformSku(String skus,Long publishListingId) {
		if(StringUtils.isBlank(skus)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku为空");
		}
		Map<String,String> map = Maps.newHashMap();
		//品连sku+分隔符（*、&、#、|。取4个字符随机一位）+后缀（随机6位数字和字母组合）
		String[] skustr = skus.split(",");
		for(String sku:skustr){
			String platformSku = GeneratePlateformSku.getAliexpressPlateformSku(sku);

			List<AliexpressPublishListingProduct> list = aliexpressPublishListingService.getProductByPlatformSku(platformSku,publishListingId);
			while (list!=null && list.size()>0){
				platformSku = GeneratePlateformSku.getAliexpressPlateformSku(sku);
				list = aliexpressPublishListingService.getProductByPlatformSku(platformSku,publishListingId);
			}
			map.put(sku,platformSku);
		}


		return map;
	}


	@AspectContrLog(descrption = "Aliexpress速卖通刊登数量统计",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "Aliexpress速卖通刊登数量统计", notes = "")
	@PostMapping("/getAliexpressSkuNumber")
	public List<Map<String,Object>> getAliexpressSkuNumber(){
		return aliexpressPublishListingService.getAliexpressSkuNumber();
	}



	@AspectContrLog(descrption="速卖通多刊登",actionType= SysLogActionType.ADD)
	@PostMapping("/moreAliexpress")
	@ApiOperation("速卖通多刊登")
	public void moreAliexpress(AliexpressMoreVO vo) {
		if (vo.getId() == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "速卖通多刊登id不能为空");
		}
		AliexpressPublishListing publishListing = aliexpressPublishListingService.getAliexpressPublishListingById(vo.getId());
		if (publishListing == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "速卖通多刊登id不存在");
		}
		String sellerId = this.getSellerId().toString();
		if(!publishListing.getSellerId().equalsIgnoreCase(sellerId)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能操作其他用户的刊登");
		}
		if(vo.getPublishStatus() == AliexpressEnum.AliexpressStatusEnum.PUBLISH.getCode()) {
			AliexpressPublishModel model = aliexpressPublishListingService.getPublishModelById(vo.getId(), 1);
			AliexpressPublishRequest publishRequest = new AliexpressPublishRequest();
			BeanUtils.copyProperties(model, publishRequest);
			aliexpressPublishListingService.check(publishRequest, false, null);
		}
		try {
			for(Long empowerId : vo.getEmpowerIds()) {
				Long idnew = aliexpressPublishListingService.insertcopyAliexpressPublish(vo.getId(), 2,empowerId ,vo.getPublishStatus());
				//刊登操作日志
				UserDTO userDTO = getUserInfo.getUserDTO();
				aliexpressPublishListingService.insertAliexpressOperationLog(publishListing.getId(),
						userDTO.getUserName(), AliexpressOperationEnum.COPY.getCode(), "",Long.valueOf(userDTO.getUserId()));
				//状态是上线和下线状态 队列刊登
				if (vo.getPublishStatus() == AliexpressEnum.AliexpressStatusEnum.PUBLISH.getCode()) {
					AliexpressPublishModel model = aliexpressPublishListingService.getPublishModelById(idnew, 1);
					aliexpressSender.send(model);
				}
			}
		}catch (GlobalException e) {
			throw e;
		} catch (Exception e) {
			logger.error("速卖通多刊登异常", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "速卖通多刊登异常");
		}
	}

}


