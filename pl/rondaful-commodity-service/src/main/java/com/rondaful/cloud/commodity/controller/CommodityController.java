package com.rondaful.cloud.commodity.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.common.Constant;
import com.rondaful.cloud.commodity.constant.ComomodityIndexConst;
import com.rondaful.cloud.commodity.entity.*;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.enums.SkuOperateInfoEnum;
import com.rondaful.cloud.commodity.mapper.*;
import com.rondaful.cloud.commodity.remote.RemoteUserService;
import com.rondaful.cloud.commodity.service.*;
import com.rondaful.cloud.commodity.utils.DateUtil;
import com.rondaful.cloud.commodity.utils.ValidatorUtil;
import com.rondaful.cloud.commodity.vo.CommoditySearchVo;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RemoteUtil;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchResult.Hit;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;




/**
 * 商品服务控制层
 * */
@Api(description="商品服务控制层")
@RequestMapping("/operate")
@RestController
public class CommodityController extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(CommodityController.class);

    @Autowired
    private IBrandService brandService;

    @Autowired
    private IAttributeService attributeService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ICommodityService commodityService;

    @Autowired
    private ICommoditySpecService commoditySpecService;

    @Autowired
    private ICommodityBaseService commodityBaseService;

    @Autowired
    private CommodityBaseMapper commodityBaseMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private AttributeMapper attributeMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Value("${erp.freight_trial}")
    private String freight_trial_url;
    
    @Value("${es.enabled}")
    private boolean isUseEs;
    
    @Resource
    private JestClient jestClient;
    
    @Autowired
	private CommonJestIndexService commonJestIndexService;
    
    @Autowired
    private CommodityBelongSellerMapper commodityBelongSellerMapper;
    
    @Autowired
	private MessageService messageService;
    
    @Autowired
	private SkuOperateLogService skuOperateLogService;
    

    @PostMapping("/brand/add")
    @ApiOperation(value = "添加品牌", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplierId", value = "所属供应商id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "brandName", value = "品牌名称", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "brandLogo", value = "品牌logo", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "brandWebsite", value = "品牌网站", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "brandDescribe", value = "品牌描述", dataType = "string", paramType = "query")})
    @RequestRequire(require = "brandName, brandLogo", parameter = String.class)
    @AspectContrLog(descrption = "添加品牌",actionType = SysLogActionType.ADD)
    @CacheEvict(value = "brandCache", allEntries = true)
    public void addBrand(@ApiIgnore Brand brand) throws Exception {
        Long supplierId = null;
        if (brand.getSupplierId() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不能为空");
        if (brand.getSupplierId().longValue() != 0) {
            RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>(){{
                this.add(brand.getSupplierId());
            }}, 0));
            List<Map> list = RemoteUtil.getList();
            if (list == null || list.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不存在");
            Map user = list.get(0);
            if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商不存在");
            Integer type = (Integer) user.get("platformType");
            Integer userId = (Integer) user.get("userId");
            Integer parentId = (Integer) user.get("topUserId");
            if (type != null && type.intValue() == 0 && userId != null) {
                if (parentId.intValue() != 0) {
                    supplierId = Long.valueOf(parentId);
                    brand.setSupplierId(supplierId);
                } else {
                    supplierId = Long.valueOf(userId);
                    brand.setSupplierId(supplierId);
                }
            }
        }
        List<Brand> lb = brandMapper.findBrandList(new Brand(){{
            this.setBrandName(brand.getBrandName());
        }});
        if (lb != null && !lb.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌名称已存在");
        
        if (StringUtils.isNotBlank(brand.getBrandNameEn())) {
        	List<Brand> lb2 = brandMapper.findBrandList(new Brand(){{
                this.setBrandNameEn(brand.getBrandNameEn());
            }});
            if (lb2 != null && !lb2.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌英文名称已存在");
		}
        
        brand.setState(0);
        brand.setCreatTime(DateUtil.getCurrentDate());
        int result = brandService.insertSelective(brand);
        if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        messageService.unAuditBrandNumMsg();
    }


    @DeleteMapping("/brand/delete/{id}")
    @ApiOperation(value = "删除品牌", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "品牌唯一id", dataType = "string", paramType = "path", required = true)})
    @AspectContrLog(descrption = "删除品牌",actionType = SysLogActionType.DELETE)
    @CacheEvict(value = "brandCache", allEntries = true)
    public void deleteBrand(@PathVariable String id) {
        if (!ValidatorUtil.isMath(String.valueOf(id))) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        Brand br = brandService.selectByPrimaryKey(Long.valueOf(id));
        if (br == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌id不存在");
        List<CommodityBase> comm = commodityBaseMapper.page(new CommodityBase(){{
            this.setBrandId(Long.valueOf(id));
        }});
        if (!comm.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "该品牌已绑定商品不能删除");
        int result = brandService.deleteByPrimaryKey(Long.valueOf(id));
        if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        messageService.unAuditBrandNumMsg();
    }


    @PostMapping("/brand/update")
    @ApiOperation(value = "更新品牌", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "品牌id", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "supplierId", value = "所属供应商id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "auditDescription", value = "审核描述", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "审核状态，0：待审核，1：审核通过，2：审核失败", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "brandName", value = "品牌名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "brandLogo", value = "品牌logo", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "brandWebsite", value = "品牌网站", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "brandDescribe", value = "品牌描述", dataType = "string", paramType = "query")})
    @RequestRequire(require = "id", parameter = String.class)
    @AspectContrLog(descrption = "更新品牌",actionType = SysLogActionType.UDPATE)
    @CacheEvict(value = "brandCache", allEntries = true)
    public void updateBrand(@ApiIgnore Brand brand) {
        Long supplierId = null;
        if (brand.getSupplierId() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不能为空");
        if (brand.getSupplierId() != 0) {
            RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>(){{
                this.add(brand.getSupplierId());
            }}, 0));
            List<Map> list = RemoteUtil.getList();
            if (list == null || list.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不存在");
            Map user = list.get(0);
            if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商不存在");
            Integer type = (Integer) user.get("platformType");
            Integer userId = (Integer) user.get("userId");
            Integer parentId = (Integer) user.get("topUserId");
            if (type != null && type.intValue() == 0 && userId != null) {
                if (parentId.intValue() != 0) {
                    supplierId = Long.valueOf(parentId);
                    brand.setSupplierId(supplierId);
                } else {
                    supplierId = Long.valueOf(userId);
                    brand.setSupplierId(supplierId);
                }
            }
        }
        if (brand.getState() == null) {
            if (StringUtils.isNotBlank(brand.getBrandName())) {
                List<Brand> lb = brandMapper.page(new Brand(){{
                    this.setBrandName(brand.getBrandName());
                }});
                if (lb != null && !lb.isEmpty() && lb.get(0).getId().intValue() != brand.getId().intValue()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌名称已存在");
            }
            
            if (StringUtils.isNotBlank(brand.getBrandNameEn())) {
            	List<Brand> lb2 = brandMapper.findBrandList(new Brand(){{
                    this.setBrandNameEn(brand.getBrandNameEn());
                }});
                if (lb2 != null && !lb2.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌英文名称已存在");
            }
        }
        if (!ValidatorUtil.isMath(String.valueOf(brand.getId()))) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        Brand br = brandService.selectByPrimaryKey(Long.valueOf(brand.getId()));
        if (br == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌id不存在");
        if (brand.getState() != null && brand.getState().intValue() != 0 && brand.getState().intValue() != 1 && brand.getState().intValue() != 2)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "审核标识错误");
        if (brand.getState() != null && brand.getState().intValue() == 2) {
            if (StringUtils.isBlank(brand.getAuditDescription())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "审核描述不能为空");
        }
        if (brand.getState() == null) brand.setState(0);//恢复待审核
        int result = brandService.updateByPrimaryKeySelective(brand);
        if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        brand.setBrandName(br.getBrandName());
        //发送审核通知
        if (brand.getState() != null && brand.getState().intValue() != 0) {
            brandService.auditNoticMeassage(brand.getState(), brand);
            messageService.unAuditBrandNumMsg();
        }
    }


    @GetMapping("/brand/list")
    @ApiOperation(value = "查询品牌列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "category_level_1", value = "一级分类", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "category_level_2", value = "二级分类", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "category_level_3", value = "三级分类", dataType = "Long", paramType = "query")})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page<Brand> listBrand(String page, String row, Brand brand) {
        Page<Brand> p = brandService.selectBranchList(page,row,brand);
        return p;
    }


    @PostMapping("/attribute/add")
    @ApiOperation(value = "添加属性", notes = "attributeValue,属性值字段，请按照'中文名:英文名|中文名:英文名'格式传参，多个以|分割")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "attributeNameCn", value = "属性中文名", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "attributeNameEn", value = "属性英文名", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "isSku", value = "是否sku，1是，0否", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "inputType", value = "输入方式，1单选，2多选，3文本框", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "attributeValue", value = "属性值", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "attributeDescribe", value = "描述", dataType = "string", paramType = "query")})
    @RequestRequire(require = "attributeNameCn, attributeNameEn", parameter = String.class)
    @AspectContrLog(descrption = "添加属性",actionType = SysLogActionType.ADD)
    public void addattribute(@ApiIgnore Attribute attribute) {
        if (StringUtils.isNotBlank(attribute.getAttributeNameCn()) && attribute.getAttributeNameCn().length() > 50) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性中文名最多50个字符");
        if (StringUtils.isNotBlank(attribute.getAttributeNameEn()) &&  attribute.getAttributeNameEn().length() > 50) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性英文名最多50个字符");
        if (StringUtils.isNotBlank(attribute.getAttributeDescribe()) &&  attribute.getAttributeDescribe().length() > 200) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性描述最多200个字符");
       
        if (attribute.getInputType() != 3 && StringUtils.isBlank(attribute.getAttributeValue())) 
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性值不能为空");
        
        if(ValidatorUtil.isSpecialStr(attribute.getAttributeNameCn())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性中文名不能包含限制字符");
        if(ValidatorUtil.isSpecialStr(attribute.getAttributeNameEn())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性英文名不能包含限制字符");
        
        List list = attributeMapper.page(new Attribute(){{
            this.setAttributeNameCn(attribute.getAttributeNameCn());
            this.setAttributeNameEn(attribute.getAttributeNameEn());
        }});
        if (!list.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性名称重复");
       
        if (StringUtils.isNotBlank(attribute.getAttributeValue())) {
        	String[] attributeValue = attribute.getAttributeValue().split("\\|");
            for (int i=0;i<attributeValue.length;i++) {
                int finalI = i;
                List listAttr = attributeMapper.page(new Attribute(){{
                    this.setAttributeValue(attributeValue[finalI]);
                }});
                if (CollectionUtils.isNotEmpty(listAttr)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性值重复");
            }
        }
        	
        int result = attributeService.insertSelective(attribute);
        if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
    }


    @DeleteMapping("/attribute/delete/{id}")
    @ApiOperation(value = "删除属性", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "属性唯一id", dataType = "string", paramType = "path", required = true)})
    @AspectContrLog(descrption = "删除属性",actionType = SysLogActionType.DELETE)
    public void deleteAttribute(@PathVariable String id) {
        if (!ValidatorUtil.isMath(id)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        List<Category> list = categoryMapper.findFindInSet(Long.valueOf(id));
        if (!list.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "该属性已存在绑定关系，不能删除");
        int result = attributeService.deleteByPrimaryKey(Long.valueOf(id));
        if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
    }


    @PostMapping("/attribute/update")
    @ApiOperation(value = "更新属性", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "属性id", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "attributeNameCn", value = "属性中文名", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "attributeNameEn", value = "属性英文名", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "isSku", value = "是否sku，1是，0否", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "inputType", value = "输入方式，1单选，2多选，3文本框", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "attributeValue", value = "属性值", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "attributeDescribe", value = "描述", dataType = "string", paramType = "query")})
    @RequestRequire(require = "id", parameter = String.class)
    @AspectContrLog(descrption = "更新属性",actionType = SysLogActionType.UDPATE)
    public void updateAttribute(@ApiIgnore Attribute attribute) throws NoSuchFieldException, IllegalAccessException {
        if (!ValidatorUtil.isMath(String.valueOf(attribute.getId()))) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        if (StringUtils.isNotBlank(attribute.getAttributeNameCn()) && attribute.getAttributeNameCn().length() > 50) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性中文名最多50个字符");
        if (StringUtils.isNotBlank(attribute.getAttributeNameEn()) &&  attribute.getAttributeNameEn().length() > 50) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性英文名最多50个字符");
        if (StringUtils.isNotBlank(attribute.getAttributeDescribe()) &&  attribute.getAttributeDescribe().length() > 200) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性描述最多200个字符");
       
        if(ValidatorUtil.isSpecialStr(attribute.getAttributeNameCn())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性中文名不能包含限制字符");
        if(ValidatorUtil.isSpecialStr(attribute.getAttributeNameEn())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性英文名不能包含限制字符");
        
        Attribute ab = attributeMapper.selectByPrimaryKey(attribute.getId());
        if (ab == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性id错误");
        attribute.setVersion(ab.getVersion());
        List<Attribute> list = attributeMapper.page(new Attribute(){{
            this.setAttributeNameCn(attribute.getAttributeNameCn());
            this.setAttributeNameEn(attribute.getAttributeNameEn());
        }});
        for (Attribute at : list) {
            if (at.getId().intValue() != attribute.getId().intValue())
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性名称重复");
        }
        int result = attributeService.updateByPrimaryKeySelective(attribute);
        if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
    }


    @GetMapping("/attribute/list")
    @ApiOperation(value = "查询属性列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page<Attribute> listAttribute(String page, String row, Attribute attribute) throws NoSuchFieldException, IllegalAccessException {
        Page.builder(page, row);
        Page<Attribute> p = attributeService.page(attribute);
        return p;
    }


    @PostMapping("/category/add")
    @ApiOperation(value = "添加分类", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryLevel", value = "分类级别，1、2、3级", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "categoryName", value = "分类名称", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "categoryNameEn", value = "分类英文名称", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "describe", value = "分类描述", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "categoryParentId", value = "父级类目id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sortNum", value = "排序", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "feeRate", value = "佣金百分比，一级分类设置", dataType = "Long", paramType = "query")})
    @RequestRequire(require = "categoryLevel, categoryName", parameter = String.class)
    @AspectContrLog(descrption = "添加分类",actionType = SysLogActionType.ADD)
    public void addCategory(@ApiIgnore Category category) {
        if (!ValidatorUtil.isMath(String.valueOf(category.getCategoryLevel()))) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        if (category.getCategoryLevel() != 1 && category.getCategoryLevel() != 2 && category.getCategoryLevel() != 3 ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        categoryService.addCategorys(category);
    }


    @DeleteMapping("/category/delete")
    @ApiOperation(value = "删除分类", notes = "")
    @AspectContrLog(descrption = "删除分类",actionType = SysLogActionType.DELETE)
    public void deleteCategory(@ApiParam(name = "ids", value = "id数组，多个id以逗号隔开传递", required = true) @RequestParam("ids") List<String> ids) {
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        categoryService.deleteCategorys(ids);
    }


    @PostMapping("/category/update")
    @ApiOperation(value = "更新分类", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "分类id", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "categoryName", value = "分类名称", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "categoryNameEn", value = "分类英文名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "describe", value = "分类描述", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sortNum", value = "排序", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "feeRate", value = "佣金百分比，一级分类设置", dataType = "Long", paramType = "query")
            })
    @RequestRequire(require = "id", parameter = String.class)
    @AspectContrLog(descrption = "更新分类",actionType = SysLogActionType.UDPATE)
    public void updateCategory(@ApiIgnore Category category) throws NoSuchFieldException, IllegalAccessException {
        if (!ValidatorUtil.isMath(String.valueOf(category.getId()))) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        if (category.getCategoryName() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类名称不能为空");
        category.setCategoryLevel(null);
        category.setCategoryParentId(null);
        if (category.getSortNum() != null && category.getSortNum()<=0) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类排序应大于0");
		}
        int result = categoryService.updateCategorys(category);
        if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
    }


    @PostMapping("/category/bind")
    @ApiOperation(value = "分类绑定", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "分类id", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "bindAttributeIds", value = "绑定属性id,多个以逗号隔开", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "id, bindAttributeIds", parameter = String.class)
    @AspectContrLog(descrption = "分类绑定",actionType = SysLogActionType.UDPATE)
    public void bindCategory(@ApiIgnore Category category) throws NoSuchFieldException, IllegalAccessException {
        categoryService.bindCategorySet(category);
    }

    @GetMapping("/category/listForHome")
    @ApiOperation(value = "卖家首页查询分类列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryName", value = "关键词", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page listCategoryForHome(String page, String row, String categoryName) {
        Page.builder(page, row);
        Page p = categoryService.findList(categoryName,1);
        return p;
    }
    
    @GetMapping("/category/list")
    @ApiOperation(value = "查询分类列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryName", value = "关键词", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page listCategory(String page, String row, String categoryName) {
        Page.builder(page, row);
        Page p = categoryService.findList(categoryName,null);
        return p;
    }
    
    @GetMapping("/category/listPage")
    @ApiOperation(value = "查询分类列表(管理后台用)", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryName", value = "关键词", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page listCategoryForCms(String page, String row, String categoryName) {
        Page.builder(page, row);
        Page p = categoryService.findList(categoryName,null);
        return p;
    }
    
    @GetMapping("/category/hasProductCategory")
    @ApiOperation(value = "查询有商品的一级分类", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "saleType", value = "销售类型（1精品，2热卖，3新品）", dataType = "Long", paramType = "query",required = true)})
    @RequestRequire(require = "page, row,saleType", parameter = String.class)
    public Page selectHasProductCategory(String page, String row,Integer saleType) {
    	Map<String, Object> map=new HashMap<String, Object>();
    	//判断是否登录
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
        		if (user.getUserid() != null && user.getTopUserId() != null) {
        			List<String> limitIds=null;
        			if (user.getTopUserId() == 0) {//主账号
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getUserid())));
        			}else {
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getTopUserId())));
    				}
                    if (limitIds != null && limitIds.size()>0) {
                    	map.put("limitIds", limitIds);
            		}
				}
        	}
		}else {//如果没登录，指定卖家的不可搜索
			List<String> belongSellerCommodityIds=commodityBelongSellerMapper.selectAllCommodityId(null);
			if (belongSellerCommodityIds != null && belongSellerCommodityIds.size()>0) {
				map.put("limitIds", belongSellerCommodityIds);
			}
		}
        
        map.put("saleType", saleType);
        
        Page.builder(page, row);
        List<Category> list=categoryMapper.selectHasProductCategory(map);
        PageInfo pageInfo = new PageInfo(list);
        return new Page(pageInfo);
    }


    @GetMapping("/category/{id}")
    @ApiOperation(value = "查询id查询分类详情", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "分类id", dataType = "string", paramType = "path", required = true)})
    public Map<String, Object> idCategory(@PathVariable String id) {
        Map<String, Object> map = new HashMap<>();
        if (!ValidatorUtil.isMath(id)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        Category cate = categoryService.selectByPrimaryKey(Long.valueOf(id));
        if (cate == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类id有误");
        List<Attribute> list = new ArrayList<Attribute>();
        if (StringUtils.isNotBlank(cate.getBindAttributeIds())) {
            String[] atrr = cate.getBindAttributeIds().split(",");
            if (atrr.length > 0) {
                for (int i=0;i<atrr.length;i++) {
                    Attribute attribute = attributeService.selectByPrimaryKey(Long.valueOf(atrr[i]));
                    if (attribute != null)
                        list.add(attribute);
                }
            }
        }
        map.put("attributeList", list);
        map.put("category", cate);
        return map;
    }


    @PostMapping("/commodity/add")
    @ApiOperation(value = "添加商品", notes = "")
    @AspectContrLog(descrption = "添加商品",actionType = SysLogActionType.ADD)
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public void addCommodity(@RequestBody CommodityBase commodityBase) {
        if (commodityBase == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "参数不能为空");
        commodityBase.setCreatTime(DateUtil.getCurrentDate());
        List<CommoditySpec> commoditySpec = commodityBase.getCommoditySpecList();
        CommodityDetails commodityDetails = commodityBase.getCommodityDetails();
        if (commodityDetails == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "详情数据参数不能为空");
        if (commoditySpec.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格不能为空");
        
        if (commodityBase.getCategoryLevel1() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "一级分类不能为空");
        if (commodityBase.getCategoryLevel2() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "二级分类不能为空");
        if (commodityBase.getCategoryLevel3() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类不能为空");
        if (commodityBase.getSupplierId() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不能为空");
        
        if (commodityBase.getBrandId() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌id不能为空");
        if (commodityBase.getDefaultRepository() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "默认仓库不能为空");
        if (commodityBase.getIsPrivateModel() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "是否私模产品不能为空");
        if (commodityBase.getProductLogisticsAttributes() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品物流属性不能为空");
        if (commodityBase.getVendibilityPlatform() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "可售平台不能为空");
        if (commodityBase.getSuggestSalePrice() != null && !ValidatorUtil.isMathFloat(commodityBase.getSuggestSalePrice().toString())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "建议销售价格必须小数点后两位");
        }
        if (commodityBase.getLowestSalePrice() != null && !ValidatorUtil.isMathFloat(commodityBase.getLowestSalePrice().toString())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "最低销售价格必须小数点后两位");
        }

        if (StringUtils.isBlank(commodityDetails.getCommodityDesc())) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品描述不能为空");
        }
        if (StringUtils.isBlank(commodityDetails.getStrength1())) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品亮点1不能为空");
        }
        
        // pic0|pic1|pic2|pic3 ,前端将主图pic0和附图pic1|pic2|pic3放additionalPicture字段一起传过来
        if (StringUtils.isBlank(commodityDetails.getAdditionalPicture())) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品主图不能为空");
        }else {
			String pic=commodityDetails.getAdditionalPicture();
			if (pic.contains("|")) {
				commodityDetails.setMasterPicture(pic.substring(0, pic.indexOf("|")));
				commodityDetails.setAdditionalPicture(pic.substring(pic.indexOf("|")+1));
			}else {
				commodityDetails.setMasterPicture(pic);
				commodityDetails.setAdditionalPicture(null);
			}
		}

        commodityService.addCommodity(commodityBase, commodityDetails, commoditySpec);
        messageService.unAuditSkuNumMsg();
    }


    @PostMapping("/commodity/update")
    @ApiOperation(value = "修改商品", notes = "")
    @AspectContrLog(descrption = "修改商品",actionType = SysLogActionType.UDPATE)
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public void updateCommodity(@RequestBody CommodityBase commodityBase) {
        if (commodityBase == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "参数不能为空");
        if (commodityBase.getId() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品id不能为空");
        CommodityBase commodity = commodityBaseService.selectByPrimaryKey(commodityBase.getId());
        if (commodity == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品id不存在");
        List<CommoditySpec> commoditySpec = commodityBase.getCommoditySpecList();
        CommodityDetails commodityDetails = commodityBase.getCommodityDetails();
        if (commodityDetails == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "详情数据参数不能为空");
        if (commoditySpec.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格不能为空");
        
        if (commodityBase.getCategoryLevel1() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "一级分类不能为空");
        if (commodityBase.getCategoryLevel2() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "二级分类不能为空");
        if (commodityBase.getCategoryLevel3() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类不能为空");
        if (commodityBase.getSupplierId() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不能为空");
        if (commodityBase.getBrandId() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌id不能为空");
        if (commodityBase.getDefaultRepository() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "默认仓库不能为空");
        if (commodityBase.getIsPrivateModel() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "是否私模产品不能为空");
        if (commodityBase.getProductLogisticsAttributes() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品物流属性不能为空");
        if (commodityBase.getVendibilityPlatform() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "可售平台不能为空");
        if (StringUtils.isNotBlank(commodityBase.getProducer()) && commodityBase.getProducer().length()>100) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "生产厂商最多100字符");
		}
        if (commodityBase.getSuggestSalePrice() != null && !ValidatorUtil.isMathFloat(commodityBase.getSuggestSalePrice().toString())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "建议销售价格必须小数点后两位");
        }
        if (commodityBase.getLowestSalePrice() != null && !ValidatorUtil.isMathFloat(commodityBase.getLowestSalePrice().toString())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "最低销售价格必须小数点后两位");
        }

        if (StringUtils.isBlank(commodityDetails.getCommodityDesc())) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品描述不能为空");
        }
        if (StringUtils.isBlank(commodityDetails.getStrength1())) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品亮点1不能为空");
        }
        
        // pic0|pic1|pic2|pic3 ,前端将主图pic0和附图pic1|pic2|pic3放additionalPicture字段一起传过来
        if (StringUtils.isBlank(commodityDetails.getAdditionalPicture())) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品主图不能为空");
        }else {
			String pic=commodityDetails.getAdditionalPicture();
			if (pic.contains("|")) {
				commodityDetails.setMasterPicture(pic.substring(0, pic.indexOf("|")));
				commodityDetails.setAdditionalPicture(pic.substring(pic.indexOf("|")+1));
			}else {
				commodityDetails.setMasterPicture(pic);
				commodityDetails.setAdditionalPicture(null);
			}
		}

        commodityBase.setSpuId(commodity.getSpuId());
        commodityBase.setUpdateTime(DateUtil.getCurrentDate());

        commodityService.updateCommodity(commodityBase, commodityDetails, commoditySpec);
    }


    @PostMapping("/commodity/submit2audit")
    @ApiOperation(value = "提交商品审核", notes = "")
    @AspectContrLog(descrption = "提交商品审核",actionType = SysLogActionType.UDPATE)
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public void submit2audit(@ApiParam(name = "ids", value = "id数组，多个id以逗号隔开传递", required = true) @RequestParam("ids") List<String> ids) throws NoSuchFieldException, IllegalAccessException {
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku的id不能为空");
        
        //日志操作人
    	String optUser="";
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(user.getPlatformType())) {//供应商平台
        		optUser="【供应商】"+user.getUsername();
        	}else if (UserEnum.platformType.CMS.getPlatformType().equals(user.getPlatformType())) {//管理平台
        		optUser="【品连】"+user.getUsername();
        	}
        }
		
		SkuOperateLog skuLog=null;
        for (String id : ids) {
            CommoditySpec com = commoditySpecService.selectByPrimaryKey(Long.valueOf(id));
            if (com == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格不存在");
            if (com.getState() != -1 && com.getState() != 2) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "只能操作待提交未上架的商品");
            com.setState(0);
            commoditySpecService.updateByPrimaryKeySelective(com);
            
            //插入操作日志
            skuLog=new SkuOperateLog();
            skuLog.setOperateBy(optUser);
            skuLog.setOperateInfo(SkuOperateInfoEnum.TO_AUDIT.getMsg());
            skuLog.setSystemSku(com.getSystemSku());
            skuOperateLogService.addSkuLog(skuLog);
            
        }
        messageService.unAuditSkuNumMsg();
    }


    @PostMapping("/commodity/auditSku")
    @ApiOperation(value = "商品审核", notes = "")
    @AspectContrLog(descrption = "商品审核",actionType = SysLogActionType.UDPATE)
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public void auditSku(@ApiParam(name = "ids", value = "id数组，多个id以逗号隔开传递", required = true)
                             @RequestParam("ids") List<String> ids,
                         @ApiParam(name = "audit", value = "审核类型，1：通过，2：拒绝", required = true) @RequestParam("audit")String audit,
                         @ApiParam(name = "auditDesc", value = "审核描述") @RequestParam("auditDesc")String auditDesc){
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku的id不能为空");
        if (!"1".equals(audit) && !"2".equals(audit)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "审核标识只能为1和2");
        commodityService.updateAuditSku(ids, audit, auditDesc);
    }


    @PostMapping("/commodity/upperAndLowerFrames")
    @ApiOperation(value = "商品上下架", notes = "")
    @AspectContrLog(descrption = "商品上下架",actionType = SysLogActionType.UDPATE)
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public void upperAndLowerFrames(@ApiParam(name = "ids", value = "id数组，多个id以逗号隔开传递", required = true)
                         @RequestParam("ids") List<String> ids,
                         @ApiParam(name = "type", value = "上下架，true：上架，false：下架", required = true) @RequestParam("type")Boolean type){
        commodityService.upperAndLowerFrames(ids, type);
    }


    @PostMapping("/commodity/delete")
    @ApiOperation(value = "删除商品", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一id", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "type", value = "类型:spu,sku", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "id, type", parameter = String.class)
    @AspectContrLog(descrption = "删除商品",actionType = SysLogActionType.DELETE)
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public void delete(Long id, String type) {
        if (!"sku".equals(type) && !"spu".equals(type)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "type参数只能是sku或spu");
        commodityService.deleteCommodity(id, type);
        messageService.unAuditSkuNumMsg();
    }


    @PostMapping("/commodity/focus")
    @ApiOperation(value = "关注商品", notes = "")
    @AspectContrLog(descrption = "关注商品",actionType = SysLogActionType.ADD)
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public void focus(@ApiParam(name = "ids", value = "商品id数组，多个id以逗号隔开传递", required = true)
                                    @RequestParam("ids") List<String> ids,
                                    @ApiParam(name = "sellerId", value = "卖家id", required = true) @RequestParam("sellerId")Long sellerId) {
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品的id不能为空");
        commodityService.focusCommodity(ids, sellerId);
    }


    @PostMapping("/commodity/cancelFocus")
    @ApiOperation(value = "取消关注商品", notes = "")
    @AspectContrLog(descrption = "取消关注商品",actionType = SysLogActionType.UDPATE)
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public void cancelFocus(@ApiParam(name = "ids", value = "关注商品id数组，多个id以逗号隔开传递", required = true)
                      @RequestParam("ids") List<String> ids,
                      @ApiParam(name = "sellerId", value = "卖家id", required = true) @RequestParam("sellerId")Long sellerId) {
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品的id不能为空");
        commodityService.cancelFocusCommodity(ids, sellerId);
    }


    @GetMapping("/commodity/details/{id}")
    @ApiOperation(value = "根据商品id查询详情", notes = "", response = CommodityBase.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品id", dataType = "string", paramType = "path", required = true)})
    public Map detailsCommodity(@PathVariable String id,Boolean isUp) {
        CommodityBase comm = commodityBaseMapper.selectCommodityDetailsById(Long.valueOf(id));
        if (comm == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品不存在");
        comm.setIsUp(isUp);
        Map map = commodityService.detailsCommodity(comm);
        return map;
    }


    @GetMapping("/commodity/download/publish")
    @ApiOperation(value = "下载刊登包", notes = "", response = CommoditySpec.class)
    public String download(@ApiParam(name = "ids", value = "id数组，多个id以逗号隔开传递", required = true) @RequestParam("ids") List<Long> ids) throws Exception {
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品id不能为空");
        if (ids.size() > 5) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "一次最多只能下载5个spu刊登包");
        return commodityService.download(ids, response);
    }
    
    @GetMapping("/commodity/download/allPublish")
    @ApiOperation(value = "下载所有刊登包", notes = "")
    public void downloadAll() {
    	Map<String,Object> param=new HashMap<String,Object>();
    	Long belongSellerId = null;
    	String username="";
        //判断是否登录
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	username=user.getLoginName();
        	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
        		if (user.getUserid() != null && user.getTopUserId() != null) {
        			List<String> limitIds=null;
        			if (user.getTopUserId() == 0) {//主账号
        				belongSellerId=Long.parseLong(String.valueOf(user.getUserid()));
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getUserid())));
        			}else {
        				belongSellerId=Long.parseLong(String.valueOf(user.getTopUserId()));
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getTopUserId())));
    				}
                    if (limitIds != null && limitIds.size()>0) {
                    	param.put("limitIds", limitIds);
            		}
                    param.put("belongSellerId", belongSellerId);
				}
        	}
		}
        if (belongSellerId==null) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "获取卖家ID失败");
		}
        param.put("sellerId", belongSellerId);
        param.put("isUp", true);
        param.put("username", username);
        
        new Thread(){
			public void run() {
				commodityService.getAllPublishPack(param);
			}
		}.start();
        
    }


    @GetMapping("/commodity/list/manager")
    @ApiOperation(value = "商品列表管理", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "category_level_1", value = "一级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_2", value = "二级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_3", value = "三级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "创建开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "创建结束时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "autiState", value = "商品状态", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "commodityName", value = "商品名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "isUp", value = "是否上下架:true，false", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "systemSku", value = "系统sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplierSku", value = "供应商sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SPU", value = "系统spu", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplierId", value = "供应商ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "vendibilityPlatform", value = "可售平台", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "brandId", value = "所属品牌id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "freeFreight", value = "是否包邮", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "producer", value = "生产商", dataType = "string", paramType = "query")})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page managerCommodity(String page, String row, Long category_level_1, Long category_level_2, Long category_level_3, String startTime, 
    		String endTime, Integer autiState, Boolean isUp,  String commodityName, String systemSku, String supplierSku,String SPU,
    		Long supplierId,String vendibilityPlatform, Long brandId,Integer freeFreight,
    		@ApiParam(name = "categoryLevelList1", value = "一级分类数组", required = false) @RequestParam(value = "categoryLevelList1", required = false) List<String> categoryLevelList1,
    		@ApiParam(name = "categoryLevelList2", value = "二级分类数组", required = false) @RequestParam(value = "categoryLevelList2", required = false) List<String> categoryLevelList2,
    		@ApiParam(name = "categoryLevelList3", value = "三级分类数组", required = false) @RequestParam(value = "categoryLevelList3", required = false) List<String> categoryLevelList3,
    		String producer){
        Map<String,Object> param=new HashMap<String,Object>();
        
        param.put("page", page);
        param.put("row", row);
        param.put("category_level_1", category_level_1);
        param.put("category_level_2", category_level_2);
        param.put("category_level_3", category_level_3);
        param.put("categoryLevelList1", categoryLevelList1);
        param.put("categoryLevelList2", categoryLevelList2);
        param.put("categoryLevelList3", categoryLevelList3);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("autiState", autiState);
        param.put("isUp", isUp);
        param.put("systemSku", systemSku);
        param.put("supplierSku", supplierSku);
        param.put("SPU", SPU);
        param.put("supplierId", supplierId);
        param.put("vendibilityPlatform", vendibilityPlatform);
        param.put("brandId", brandId);
        param.put("freeFreight", freeFreight);
        param.put("producer", producer);
        //断是中文还是英文商品名称搜索
        if (isEnNameSearch()) {
        	param.put("commodityNameEn", commodityName);
		}else {
			param.put("commodityNameCn", commodityName);
		}
        
        Long belongSellerId = 0L;
        //判断是否登录
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(user.getPlatformType())) {//供应商平台
        		
        		if (user.getTopUserId() == 0) {//主账号
        			param.put("supplierId", user.getUserid());
    			}else {
    				param.put("supplierId", user.getTopUserId());
				}
        	}else if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
        		if (user.getUserid() != null && user.getTopUserId() != null) {
        			List<String> limitIds=null;
        			if (user.getTopUserId() == 0) {//主账号
        				belongSellerId=Long.parseLong(String.valueOf(user.getUserid()));
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getUserid())));
        			}else {
        				belongSellerId=Long.parseLong(String.valueOf(user.getTopUserId()));
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getTopUserId())));
    				}
                    if (limitIds != null && limitIds.size()>0) {
                    	param.put("limitIds", limitIds);
            		}
                    
                    param.put("belongSellerId", belongSellerId);
				}
        	}else if (UserEnum.platformType.CMS.getPlatformType().equals(user.getPlatformType())) {//管理平台
        		
        		if (!(user.getTopUserId() == 0)) {//不是管理平台的管理员(主账号),只能看到自己绑定供应商的商品
        			List<String> supplierIds=new ArrayList<String>();
        			List<UserAccountDTO> accountDTOs=user.getBinds();
        			for (UserAccountDTO dto : accountDTOs) {
						if (dto.getBindType().intValue()==0) {
							supplierIds.addAll(dto.getBindCode());
						}
					}
					if (supplierIds.size()>0) {
						param.put("supplierIds", supplierIds);
					}else {
						//没绑定有供应商的管理员，不给看
						return null;
					}
        		}
			}
		}
    	
    	Page p = commodityService.selectCommodityListBySpec(param);
        return p;
    }



    @GetMapping("/commodity/search")
    @ApiOperation(value = "商品搜索", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "commodityName", value = "商品名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_1", value = "一级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_2", value = "二级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_3", value = "三级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "brand_id", value = "所属品牌id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vendibility_platform", value = "可售平台", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "systemSku", value = "品连SKU", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SPU", value = "品连SPU", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "saleNumOrder", value = "排序条件，销售量排序，asc/desc", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "publishNumOrder", value = "排序条件，刊登数排序，asc/desc", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "inventoryStart", value = "搜索条件，库存开始值", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "inventoryEnd", value = "搜索条件，库存结束值", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "priceStart", value = "搜索条件，商品价开始值", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "priceEnd", value = "搜索条件，商品价结束值", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "warehouseId", value = "仓库ID", dataType = "string", paramType = "query")
            })
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page search(String page, String row, Long category_level_1, Long category_level_2, Long category_level_3, Long brand_id, String vendibility_platform, String commodityName,
                       @ApiParam(name = "spus", value = "spu值数组，多个以逗号隔开传递", required = false) @RequestParam(value = "spus", required = false) List<String> ids,
                       String systemSku,String SPU,String saleNumOrder,String publishNumOrder,Integer inventoryStart,
                       Integer inventoryEnd,String priceStart,String priceEnd,String warehouseId) {
        
    	Page resultPage=null;
    	
    	boolean isIndexExist=commonJestIndexService.indexExist(ComomodityIndexConst.INDEX_NAME);
    	if (isUseEs && isIndexExist) {
    		Long belongSellerId = 0L;
    		List<String> limitIds=null;
    		//判断是否登录
            UserAll userAll=getLoginUserInformationByToken.getUserInfo();
            if (userAll!=null) {
            	UserCommon user = userAll.getUser();
            	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
            		if (user.getUserid() != null && user.getTopUserId() != null) {
            			if (user.getTopUserId() == 0) {//主账号
            				belongSellerId=Long.parseLong(String.valueOf(user.getUserid()));
            				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getUserid())));
            			}else {
            				belongSellerId=Long.parseLong(String.valueOf(user.getTopUserId()));
            				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getTopUserId())));
        				}
    				}
            	}
    		}else {//如果没登录，指定卖家的不可搜索
    			limitIds=commodityBelongSellerMapper.selectAllCommodityId(null);
			}
    		
    		int size=StringUtils.isBlank(row)?12:Integer.parseInt(row);
    		int currentPage=StringUtils.isBlank(page)?1:Integer.parseInt(page);
    		int fromIndex = size * (currentPage - 1);
    		 
    		//组装查询参数
    		JSONObject json1=new JSONObject();
    		JSONObject json2=new JSONObject();
    		JSONObject json3=new JSONObject();
    		JSONArray mustList=new JSONArray();
    		
    		if (category_level_1 != null) {
    			JSONObject json4=new JSONObject();
    			JSONObject json5=new JSONObject();
    			json5.put("commodityBase.categoryLevel1", category_level_1);
    			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
    			mustList.add(json4);
			}
    		if (category_level_2 != null) {
    			JSONObject json4=new JSONObject();
    			JSONObject json5=new JSONObject();
    			json5.put("commodityBase.categoryLevel2", category_level_2);
    			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
    			mustList.add(json4);
			}
    		if (category_level_3 != null) {
    			JSONObject json4=new JSONObject();
    			JSONObject json5=new JSONObject();
    			json5.put("commodityBase.categoryLevel3", category_level_3);
    			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
    			mustList.add(json4);
			}
    		if (brand_id != null) {
    			JSONObject json4=new JSONObject();
    			JSONObject json5=new JSONObject();
    			json5.put("commodityBase.brandId", brand_id);
    			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
    			mustList.add(json4);
			}
    		if (StringUtils.isNotBlank(vendibility_platform)) {
    			JSONObject json4=new JSONObject();
    			JSONObject json5=new JSONObject();
    			json5.put("commodityBase.vendibilityPlatform", vendibility_platform);
    			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
    			mustList.add(json4);
			}
    		if (StringUtils.isNotBlank(systemSku)) {
    			JSONObject json4=new JSONObject();
    			JSONObject json5=new JSONObject();
    			json5.put("commodityBase.systemSku", systemSku);
    			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
    			mustList.add(json4);
			}
    		if (StringUtils.isNotBlank(SPU)) {
    			JSONObject json4=new JSONObject();
    			JSONObject json5=new JSONObject();
    			json5.put("commodityBase.SPU", SPU);
    			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
    			mustList.add(json4);
			}
    		if (StringUtils.isNotBlank(warehouseId)) {
    			JSONObject json4=new JSONObject();
    			JSONObject json5=new JSONObject();
    			json5.put("commodityBase.warehouseId", warehouseId);
    			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
    			mustList.add(json4);
			}
    		
    		if (StringUtils.isNotBlank(commodityName)) {
    			commodityName=commodityName.replaceAll("\'", "&apos;");
    			String [] arr = commodityName.trim().split("\\s+");
        		for(String ss : arr){
        			JSONObject json4=new JSONObject();
        			JSONObject json5=new JSONObject();
        			json5.put("searchKeyWords", ss);
        			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
        			mustList.add(json4);
        		}
			}
    		if (belongSellerId != 981) {
    			JSONObject json4=new JSONObject();
    			JSONObject json5=new JSONObject();
    			json5.put("commodityBase.belongSellerId", 0);
    			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
    			mustList.add(json4);
			}
    		
    		 //有限制的，过滤查询
            if (limitIds!=null && limitIds.size()>0) {
            	JSONArray mustNotList=new JSONArray();
				for (String id : limitIds) {
					JSONObject json4=new JSONObject();
        			JSONObject json5=new JSONObject();
        			json5.put("commodityBase.id", id);
        			json4.put(ComomodityIndexConst.MATCH_TYPE, json5);
        			mustNotList.add(json4);
				}
				json3.put("must_not", mustNotList);
			}
    		
    		//排序
    		JSONArray sortArray=new JSONArray();
    		JSONObject sortJson=new JSONObject();
    		if (StringUtils.isNotBlank(saleNumOrder)) {//销量排序
    			JSONObject saleNumOrderJson=new JSONObject();
    			saleNumOrderJson.put("order", saleNumOrder);
    			sortJson.put("commodityBase.saleNum", saleNumOrderJson);
			}
    		if (StringUtils.isNotBlank(publishNumOrder)) {//刊登数排序
    			JSONObject publishNumOrderJson=new JSONObject();
    			publishNumOrderJson.put("order", publishNumOrder);
				sortJson.put("commodityBase.publishNum", publishNumOrderJson);
			}
    		if(StringUtils.isBlank(saleNumOrder) && StringUtils.isBlank(publishNumOrder)) {//默认创建时间（id）倒序
    			JSONObject idOrderJson=new JSONObject();
    			idOrderJson.put("order", "desc");
				sortJson.put("commodityBase.inventory",idOrderJson);
			}
    		sortArray.add(sortJson);
    		json1.put("sort", sortArray);
    		
    		//范围搜索
    		if (inventoryStart != null || inventoryEnd != null || StringUtils.isNotBlank(priceStart) || StringUtils.isNotBlank(priceEnd)) {
				JSONObject filterJson=new JSONObject();
				JSONObject boolJson=new JSONObject();
				JSONArray mustArray=new JSONArray();
				if (inventoryStart != null || inventoryEnd != null) {//库存
					JSONObject range1=new JSONObject();
					JSONObject range1Json=new JSONObject();
					JSONObject inventoryJson=new JSONObject();
					if (inventoryStart != null) {
						inventoryJson.put("gte", inventoryStart);
					}
					if (inventoryEnd != null) {
						inventoryJson.put("lte", inventoryEnd);
					}
					range1Json.put("commodityBase.inventory", inventoryJson);
					range1.put("range", range1Json);
					
					mustArray.add(range1);
				}
				
				if (StringUtils.isNotBlank(priceStart) || StringUtils.isNotBlank(priceEnd)) {
					JSONObject range2=new JSONObject();
					JSONObject range2Json=new JSONObject();
					JSONObject priceJson=new JSONObject();
					if (StringUtils.isNotBlank(priceStart)) {
						priceJson.put("gte", priceStart);
					}
					if (StringUtils.isNotBlank(priceEnd)) {
						priceJson.put("lte", priceEnd);
					}
					range2Json.put("commodityBase.commodityPriceUs", priceJson);
					range2.put("range", range2Json);
					mustArray.add(range2);
				}
				
				boolJson.put("must", mustArray);
				filterJson.put("bool", boolJson);
				json3.put("filter", filterJson);
			}
    		
    		json3.put("must", mustList);
    		json2.put("bool", json3);
    		json1.put("query", json2);
    		json1.put("from", fromIndex);
    		json1.put("size", size);
    		
    		List<CommodityBase> list=new ArrayList<CommodityBase>();
    		int total=0;
    		Search search = new Search.Builder(json1.toJSONString()).addIndex(ComomodityIndexConst.INDEX_NAME).addType(ComomodityIndexConst.TYPE_NAME).build();
    		try {
    			SearchResult result = jestClient.execute(search);
    			if (result != null && result.getTotal() != null) {
    				total=result.getTotal().intValue();
        			List<Hit<CommoditySearchVo, Void>> hits = result.getHits(CommoditySearchVo.class);
        			for (Hit<CommoditySearchVo, Void> hit : hits) {
        				CommoditySearchVo commodity = hit.source;
        				if (commodity != null) {
        					list.add(commodity.getCommodityBase());
						}
        			}
				}
    		} catch (IOException e) {
    			log.error("商品搜索-->ES查询异常",e);
    		}
    		
    		int pagecount=0;
    		int m=total%size;
		    if  (m>0){
		        pagecount=total/size+1;
		    }else{
		        pagecount=total/size;
		    }
		    if (list.size()>0) {
		    	PageInfo pageInfo = new PageInfo(list);
	    		pageInfo.setTotal(total);
	    		pageInfo.setPages(pagecount);
	    		pageInfo.setIsFirstPage(currentPage == 1);
	    		pageInfo.setIsLastPage(currentPage == pagecount);
	    		pageInfo.setHasPreviousPage(currentPage > 1);
	    		pageInfo.setHasNextPage(currentPage < pagecount);
	    		if (currentPage > 1) {
	    			pageInfo.setPrePage(currentPage - 1);
	            }
	            if (currentPage < pagecount) {
	            	pageInfo.setNextPage(currentPage + 1);
	            }
	            resultPage=new Page(pageInfo);
			}
		}else {
    		Map<String, Object> map=new HashMap<String, Object>();
    		map.put("page", page);
    		map.put("row", row);
    		map.put("category_level_1", category_level_1);
    		map.put("category_level_2", category_level_2);
    		map.put("category_level_3", category_level_3);
    		map.put("brandId", brand_id);
    		map.put("isUp", true);
    		map.put("vendibilityPlatform", vendibility_platform);
    		map.put("spus", ids);
    		map.put("systemSku", systemSku);
    		map.put("SPU", SPU);
            //判断是中文还是英文商品名称搜索
            if (isEnNameSearch()) {
            	map.put("commodityNameEn", commodityName);
			}else {
				map.put("commodityNameCn", commodityName);
			}
            
            //判断是否登录
            UserAll userAll=getLoginUserInformationByToken.getUserInfo();
            if (userAll!=null) {
            	UserCommon user = userAll.getUser();
            	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
            		if (user.getUserid() != null && user.getTopUserId() != null) {
            			List<String> limitIds=null;
            			if (user.getTopUserId() == 0) {//主账号
            				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getUserid())));
            			}else {
            				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getTopUserId())));
        				}
                        if (limitIds != null && limitIds.size()>0) {
                        	map.put("limitIds", limitIds);
                		}
    				}
            	}
    		}else {//如果没登录，指定卖家的不可搜索
    			List<String> belongSellerCommodityIds=commodityBelongSellerMapper.selectAllCommodityId(null);
    			if (belongSellerCommodityIds != null && belongSellerCommodityIds.size()>0) {
    				map.put("limitIds", belongSellerCommodityIds);
				}
			}
            
    		resultPage = commodityService.selectCommodityListBySpec(map);
		}
    	
    	if (resultPage==null) {
    		resultPage=new Page(new PageInfo());
		}
		return resultPage;
    }



    @GetMapping("/commodity/list/batchAttention")
    @ApiOperation(value = "批量关注商品初始化列表管理", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "sellerId", value = "卖家id", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "category_level_1", value = "一级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_2", value = "二级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_3", value = "三级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "创建开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "创建结束时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "autiState", value = "商品状态", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "commodityName", value = "商品名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "isUp", value = "是否上下架:true，false", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "systemSku", value = "系统sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplierSku", value = "供应商sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SPU", value = "系统spu", dataType = "string", paramType = "query")})
    @RequestRequire(require = "page, row, sellerId", parameter = String.class)
    public Page batchAttention(String page, String row, Long category_level_1, Long category_level_2, Long category_level_3, String startTime, String endTime, Integer autiState, Boolean isUp,  String commodityName, String systemSku, String supplierSku, String SPU, String sellerId) throws IllegalAccessException, InstantiationException {
        Page p = commodityService.selectCommodityListBySpecBatch(new HashMap(){{
            this.put("page", page);
            this.put("row", row);
            this.put("category_level_1", category_level_1);
            this.put("category_level_2", category_level_2);
            this.put("category_level_3", category_level_3);
            this.put("startTime", startTime);
            this.put("endTime", endTime);
            this.put("autiState", autiState);
            this.put("isUp", true);
            this.put("systemSku", systemSku);
            this.put("supplierSku", supplierSku);
            this.put("SPU", SPU);
            this.put("sellerId", sellerId);
            
            //判断是中文还是英文商品名称搜索
            if (isEnNameSearch()) {
            	this.put("commodityNameEn", commodityName);
			}else {
				this.put("commodityNameCn", commodityName);
			}
        }});
        return p;
    }




    @GetMapping("/commodity/list/focus")
    @ApiOperation(value = "关注商品列表管理", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "sellerId", value = "卖家id", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "commodityName", value = "商品名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "isUp", value = "是否上下架:true，false", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "systemSku", value = "系统sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplierSku", value = "供应商sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SPU", value = "系统spu", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "freeFreight", value = "是否包邮", dataType = "string", paramType = "query")})
    @RequestRequire(require = "page, row, sellerId", parameter = String.class)
    public Page focus(String page, String row, Boolean isUp,  String commodityName, String systemSku, String supplierSku, String SPU, String sellerId,Integer freeFreight) {
        
    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("page", page);
    	param.put("row", row);
    	param.put("isUp", true);
    	param.put("systemSku", systemSku);
    	param.put("supplierSku", supplierSku);
    	param.put("SPU", SPU);
    	param.put("freeFreight", freeFreight);
        //判断是中文还是英文商品名称搜索
        if (isEnNameSearch()) {
        	param.put("commodityNameEn", commodityName);
		}else {
			param.put("commodityNameCn", commodityName);
		}
        
        RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>(){{
            this.add(Long.parseLong(sellerId));
        }}, 1));
        if (!"100200".equals(RemoteUtil.getErrorCode())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, RemoteUtil.getMsg());
        List<Map> result = RemoteUtil.getList();
        if (result == null || result.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家id不存在");
        Map user = result.get(0);
        if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家不存在");
        Integer type = (Integer) user.get("platformType");
        Integer userId = (Integer) user.get("userId");
        Integer parentId = (Integer) user.get("topUserId");
        List<String> limitIds=null;
        if (type != null && type.intValue() == 1 && userId != null) {
            if (parentId.intValue() != 0) {
            	param.put("sellerId", parentId);
            	limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(parentId)));
            } else {
            	param.put("sellerId", userId);
            	limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(userId)));
            }
            param.put("sortKey", "t8.creat_time");
            param.put("sort", "DESC");
        }
        if (limitIds != null && limitIds.size()>0) {
        	param.put("limitIds", limitIds);
		}
    	
    	Page p = commodityService.selectCommodityListBySpec(param);
        return p;
    }
    

    @GetMapping("/commodity/spuCategory")
    @ApiOperation(value = "SPU分类映射列表", notes = "")
    public List<SpuCategory> listSpuCategory(String spu){
        return commodityService.querySpuCategoryList(spu);
    }
    
    
    @GetMapping("/commodity/siteCategory")
    @ApiOperation(value = "站点分类映射列表", notes = "")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "platform", value = "平台名称", dataType = "string", paramType = "query",required = true),
        @ApiImplicitParam(name = "categoryLevel3", value = "品连商品分类ID", dataType = "Long", paramType = "query",required = true)})
    public List<SiteCategory> listSiteCategory(String platform,Long categoryLevel3){
        return commodityService.querySiteCategoryList(platform,categoryLevel3);
    }
    
    @GetMapping("/commodity/cleanUp")
    @ApiOperation(value = "清除站点分类信息", notes = "")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "platform", value = "平台名称", dataType = "string", paramType = "query",required = true),
        @ApiImplicitParam(name = "categoryLevel3", value = "品连商品分类ID", dataType = "Long", paramType = "query",required = true)})
    @AspectContrLog(descrption = "清除站点分类信息",actionType = SysLogActionType.UDPATE)
    public void cleanUpSiteCategory(String platform,Long categoryLevel3){
        commodityService.cleanUp(platform,categoryLevel3);
    }
    
    @PostMapping("/commodity/updateSiteCategory")
    @ApiOperation(value = "更新站点分类映射", notes = "")
    @AspectContrLog(descrption = "更新站点分类映射",actionType = SysLogActionType.UDPATE)
    public void updateSiteCategory(@RequestBody List<SiteCategory> siteCategoryList){
        commodityService.updateSiteCategory(siteCategoryList);
    }

    
    @GetMapping("/commodity/copy")
    @ApiOperation(value = "复制商品", notes = "")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "商品id", dataType = "Long", paramType = "query",required = true)})
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public Long copyCommondity(Long id){
        return commodityService.copyCommodity(id);
    }
    
}

