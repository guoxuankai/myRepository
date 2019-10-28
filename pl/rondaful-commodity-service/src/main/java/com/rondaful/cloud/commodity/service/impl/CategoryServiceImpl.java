package com.rondaful.cloud.commodity.service.impl;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.constant.CommonConstant;
import com.rondaful.cloud.commodity.dto.WmsCategory;
import com.rondaful.cloud.commodity.entity.*;
import com.rondaful.cloud.commodity.enums.CountryCodeEnum;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.enums.WarehouseFirmEnum;
import com.rondaful.cloud.commodity.mapper.*;
import com.rondaful.cloud.commodity.remote.RemoteSupplierService;
import com.rondaful.cloud.commodity.service.ICategoryService;
import com.rondaful.cloud.commodity.service.WmsPushService;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.RemoteUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category> implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private AttributeMapper attributeMapper;

    @Autowired
    private CommodityBaseMapper commodityBaseMapper;

    @Autowired
    private CommoditySpecMapper commoditySpecMapper;
    
    @Autowired
    private SiteCategoryMapper siteCategoryMapper;
    
    @Autowired
	private WmsPushService wmsPushService;
    
    @Autowired
	private RemoteSupplierService remoteSupplierService;
    
    
    

    @Override
    public int deleteCategoryByIds(List list) {
        return categoryMapper.deleteCategoryByIds(list);
    }

    /**
     * 删除分类
     * */
    @Override
    public void deleteCategorys(List<String> ids) {
        for (String id : ids) {
            Category ca = categoryMapper.selectByPrimaryKey(Long.valueOf(id));
            List deleteid = new ArrayList();
            if (ca != null) {
                if (ca.getCategoryLevel() == 1) {
                    List<CommodityBase> commodityBases = commodityBaseMapper.page(new CommodityBase(){{
                        this.setCategoryLevel1(ca.getId());
                    }});
                    if (!commodityBases.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能删除该分类，spu或商品引用到");
                    //删除下面所有的2、3级分类
                    Page.builder("1", String.valueOf(Integer.MAX_VALUE));
                    List<Category> p1 = categoryMapper.page(new Category() {{
                        this.setCategoryLevel(2);
                        this.setCategoryParentId(ca.getId());
                    }});
                    for (Category caa : p1) {
                        deleteid.add(caa.getId());
                        categoryMapper.deleteByPrimaryKey(caa.getId());//删除所有的二级分类
                    }
                    if (!deleteid.isEmpty()) {
                       /* List<Category> categories = categoryMapper.selectCategoryListByParentId(deleteid);
                        for (Category c1 : categories) {
                            if (c1.getIsBindAttribute() != null && c1.getIsBindAttribute().intValue() == 1)
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "该分类下面的三级分类已绑定属性，不能删除");
                        }*/
                        categoryMapper.deleteCategoryByIds(deleteid);//删除所有的三级分类
                    }
                    categoryMapper.deleteByPrimaryKey(ca.getId());//删除当前一级分类
                } else if (ca.getCategoryLevel() == 2) {
                    List<CommodityBase> commodityBases = commodityBaseMapper.page(new CommodityBase(){{
                        this.setCategoryLevel2(ca.getId());
                    }});
                    if (!commodityBases.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能删除该分类，spu或商品引用到");
                    
                    /*List<Category> categories = categoryMapper.selectCategoryListByParentId(new ArrayList(){{
                        this.add(ca.getId());
                    }});
                    for (Category c1 : categories) {
                        if (c1.getIsBindAttribute() != null && c1.getIsBindAttribute().intValue() == 1)
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "该分类下面的三级分类已绑定属性，不能删除");
                    }*/
                    //删除所有的三级分类
                    categoryMapper.deleteCategoryByIds(new ArrayList() {{
                        add(ca.getId());
                    }});
                    categoryMapper.deleteByPrimaryKey(ca.getId());//删除当前二级分类
                } else if (ca.getCategoryLevel() == 3) {
                    /*if (ca.getIsBindAttribute() != null && ca.getIsBindAttribute().intValue() == 1)
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "该分类已绑定属性，不能删除");*/
                    
                    List<CommodityBase> commodityBases = commodityBaseMapper.page(new CommodityBase(){{
                        this.setCategoryLevel3(Long.valueOf(ca.getId()));
                    }});
                    if (!commodityBases.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能删除该分类，spu或商品引用到");
                    //删除下面所有的3级分类
                    categoryMapper.deleteByPrimaryKey(ca.getId());//删除当前三级分类
                    
                    //删除对应的站点分类映射
                    siteCategoryMapper.deleteByCategorylevel3(ca.getId());
                }
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类id错误");
            }
        }
    }


    /**
     * 根据商品基础列表信息判断下面是否有审核通过的sku
     * @param commodityBase
     * @return
     */
    public boolean isUsed (List<CommodityBase> commodityBase) {
        boolean b = false;
        for (CommodityBase co : commodityBase) {
            List<CommoditySpec> list1 = commoditySpecMapper.page(new CommoditySpec(){{
                this.setCommodityId(co.getId());
            }});
            if (list1.isEmpty()) continue;
            //判断是否审核通过
            for (CommoditySpec coo : list1) {
                if (coo.getState() == 2 || coo.getState() == 3 || coo.getState() == 4 || coo.getState() == 5) {
                    b = true;
                    break;
                }
            }
            if (b) break;
        }
        return b;
    }


    /**
     * 添加分类
     * @param category
     */
    @Override
    public void addCategorys(Category category) {
        if (category.getCategoryLevel() == 1) {
            List list = categoryMapper.findCategoryList(new Category(){{
                this.setCategoryLevel(category.getCategoryLevel());
                this.setCategoryName(category.getCategoryName());
            }});
            if (!list.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类名称重复");
            category.setCategoryParentId(0L);
            
            if (category.getFeeRate()==null) {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "佣金率不能为空");
			}
            if (category.getFeeRate().intValue()<0 || category.getFeeRate().intValue()>99) {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "佣金率范围错误");
			}
        }
        if (category.getCategoryLevel() == 2) {
            //查询父类目
            Category parentca = categoryMapper.selectByPrimaryKey(category.getCategoryParentId());
            if (parentca == null || parentca.getCategoryLevel() != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "父类目id错误");
            Page.builder("1", "1");
            List l2 = categoryMapper.findCategoryList(new Category(){{
                this.setCategoryLevel(2);
                this.setCategoryParentId(parentca.getId());
                this.setCategoryName(category.getCategoryName());
            }});
            if (!l2.isEmpty() || category.getCategoryName().equals(parentca.getCategoryName()))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "二级分类名称重复");
        }
        if (category.getCategoryLevel() == 3) {
            //查询父类目
            Category parentca = categoryMapper.selectByPrimaryKey(category.getCategoryParentId());
            if (parentca == null || parentca.getCategoryLevel() != 2) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "父类目id错误");
            Category parentcaa = categoryMapper.selectByPrimaryKey(parentca.getCategoryParentId());
            if (parentcaa == null || parentcaa.getCategoryLevel() != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "一级父类目错误");
            Page.builder("1", "1");
            List l3 = categoryMapper.findCategoryList(new Category(){{
                this.setCategoryLevel(3);
                this.setCategoryParentId(parentca.getId());
                this.setCategoryName(category.getCategoryName());
            }});
            if (!l3.isEmpty() || category.getCategoryName().equals(parentca.getCategoryName()) || category.getCategoryName().equals(parentcaa.getCategoryName()))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类名称重复");
        }
        category.setIsBindAttribute(0);
        category.setIsBindWarehouse(0);
        int result = categoryMapper.insertSelective(category);
        if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        
        //批量插入分类站点映射
        if (category.getCategoryLevel() == 3) {
        	Long categoryLevel3=category.getId();
        	List<SiteCategory> list=new ArrayList<SiteCategory>();
        	for (CountryCodeEnum country : CountryCodeEnum.values()) {
        		SiteCategory siteCategory=new SiteCategory();
        		siteCategory.setPlatform(CommonConstant.PLATFORM_AMAOZN);
        		siteCategory.setSiteCode(country.getNameEn());
        		siteCategory.setSiteName(country.getNameCn());
        		siteCategory.setCategoryLevel3(categoryLevel3);
        		list.add(siteCategory);
        	}
        	siteCategoryMapper.insertBatch(list);
        }
        
        //新增分类到wms
        List<WmsCategory> wmsCaList=new ArrayList<WmsCategory>();
        WmsCategory wmsCategory=new WmsCategory();
		wmsCategory.setCategoryCode(String.valueOf(category.getId()));
		wmsCategory.setCategoryLevel(category.getCategoryLevel());
		wmsCategory.setCategoryName(category.getCategoryName());
		wmsCategory.setCategoryNameEn(category.getCategoryNameEn());
		wmsCategory.setDataSources("1");
		wmsCategory.setParentCode(String.valueOf(category.getCategoryParentId()));
		wmsCaList.add(wmsCategory);
		
		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
		RemoteUtil.invoke(remoteSupplierService.getAuth(1));
		List<Map> resultList = RemoteUtil.getList();
		if (resultList != null && !resultList.isEmpty()) {
			for (int i = 0; i < resultList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", ((Map) resultList.get(i)).get("id"));
				map.put("code", ((Map) resultList.get(i)).get("code"));
				map.put("appKey", ((Map) resultList.get(i)).get("appKey"));
				map.put("appToken", ((Map) resultList.get(i)).get("appToken"));
				accountList.add(map);
			}
		}
		if (accountList.size() > 0) {
			for (Map map : accountList) {
				if (map.get("code") != null && WarehouseFirmEnum.WMS.getCode().equals(map.get("code"))) {
					wmsPushService.addCategory((String)map.get("appKey"),(String)map.get("appToken"),wmsCaList);
				}
			}
		}
		
    }


    @Override
    public int updateCategorys(Category category) {
        Category parentcategory = categoryMapper.selectByPrimaryKey(category.getId());//当前分类
        category.setVersion(parentcategory.getVersion());
        if (parentcategory == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类id错误");
        if (parentcategory.getCategoryLevel() == 1) {
            List<Category> list1 = categoryMapper.findCategoryList(new Category(){{
                this.setCategoryLevel(parentcategory.getCategoryLevel());
                this.setCategoryName(category.getCategoryName());
            }});
            if (!list1.isEmpty()) {
                for (Category cag : list1) {
                    if (cag.getId().intValue() != parentcategory.getId().intValue())
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "一级分类名称重复");
                }
            }
            List list2 = categoryMapper.findCategoryList(new Category(){{
                this.setCategoryParentId(parentcategory.getId());
                this.setCategoryLevel(2);
                this.setCategoryName(category.getCategoryName());
            }});
            if (!list2.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "和二级分类名称重复");
            List<Category> list3 = categoryMapper.selectCategory1ByCategoryName3(category.getCategoryName());
            for (Category cc : list3) {
                if (cc.getId().intValue() == parentcategory.getId().intValue())
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "和三级分类名称重复");
            }
            
            //一级被关闭/开启，下级也全关闭/开启
            if (category.getStatus() != null ) {
            	Map<String, Object> param=new HashMap<String, Object>();
            	List<Category> paramList=new ArrayList<Category>();
            	paramList.add(parentcategory);
            	param.put("list", paramList);
            	param.put("status", category.getStatus());
            	categoryMapper.updateStatusByParentId(param);
            	
            	param.clear();
            	List<Category> paramList2 = categoryMapper.findCategoryList(new Category(){{
                    this.setCategoryParentId(parentcategory.getId());
                }});
            	if (paramList2 != null && paramList2.size()>0) {
            		param.put("list", paramList2);
                	param.put("status", category.getStatus());
            		categoryMapper.updateStatusByParentId(param);
				}
			}
        }
        if (parentcategory.getCategoryLevel() == 2) {
            //查询父类目
            Category parentca = categoryMapper.selectByPrimaryKey(parentcategory.getCategoryParentId());
            if (parentca == null || parentca.getCategoryLevel() != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "父类目id错误");
            if (parentca.getCategoryName().equals(category.getCategoryName())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "和一级分类名称重复");
            List<Category> l2 = categoryMapper.findCategoryList(new Category(){{
                this.setCategoryLevel(2);
                this.setCategoryParentId(parentca.getId());
                this.setCategoryName(category.getCategoryName());
            }});
            if (!l2.isEmpty()) {
                for (Category c2 : l2) {
                    if (c2.getId().intValue() != category.getId().intValue())
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "二级分类名称重复");
                }
            }
            List<Category> l3 = categoryMapper.findCategoryList(new Category(){{
                this.setCategoryLevel(3);
                this.setCategoryParentId(parentcategory.getId());
                this.setCategoryName(category.getCategoryName());
            }});
            if (!l3.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "和三级分类名称重复");
            
            //二级被关闭/开启，下级也全关闭/开启
            if (category.getStatus() != null ) {
            	Map<String, Object> param=new HashMap<String, Object>();
            	List<Category> paramList=new ArrayList<Category>();
            	paramList.add(parentcategory);
            	param.put("list", paramList);
            	param.put("status", category.getStatus());
            	categoryMapper.updateStatusByParentId(param);
			}
        }
        if (parentcategory.getCategoryLevel() == 3) {
            //查询父类目
            Category parentca = categoryMapper.selectByPrimaryKey(parentcategory.getCategoryParentId());
            if (parentca == null || parentca.getCategoryLevel() != 2) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "父类目id错误");
            if (parentca.getCategoryName().equals(category.getCategoryName())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "和二级分类名称重复");
            Category parentcaa = categoryMapper.selectByPrimaryKey(parentca.getCategoryParentId());
            if (parentcaa == null || parentcaa.getCategoryLevel() != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "一级父类目错误");
            if (parentcaa.getCategoryName().equals(category.getCategoryName())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "和一级分类名称重复");
            List<Category> l3 = categoryMapper.findCategoryList(new Category(){{
                this.setCategoryLevel(3);
                this.setCategoryParentId(parentca.getId());
                this.setCategoryName(category.getCategoryName());
            }});
            if (!l3.isEmpty()) {
                for (Category c3 : l3) {
                    if (c3.getId().intValue() != category.getId().intValue())
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类名称重复");
                }
            }
        }
        
        int result = categoryMapper.updateByPrimaryKeySelective(category);
        if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        
        //更新分类到wms
        WmsCategory wmsCategory=new WmsCategory();
		wmsCategory.setCategoryCode(String.valueOf(parentcategory.getId()));
		wmsCategory.setCategoryLevel(parentcategory.getCategoryLevel());
		wmsCategory.setCategoryName(category.getCategoryName());
		wmsCategory.setCategoryNameEn(category.getCategoryNameEn());
		wmsCategory.setDataSources("1");
		wmsCategory.setParentCode(String.valueOf(parentcategory.getCategoryParentId()));
		
		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
		RemoteUtil.invoke(remoteSupplierService.getAuth(1));
		List<Map> resultList = RemoteUtil.getList();
		if (resultList != null && !resultList.isEmpty()) {
			for (int i = 0; i < resultList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", ((Map) resultList.get(i)).get("id"));
				map.put("code", ((Map) resultList.get(i)).get("code"));
				map.put("appKey", ((Map) resultList.get(i)).get("appKey"));
				map.put("appToken", ((Map) resultList.get(i)).get("appToken"));
				accountList.add(map);
			}
		}
		if (accountList.size() > 0) {
			for (Map map : accountList) {
				if (map.get("code") != null && WarehouseFirmEnum.WMS.getCode().equals(map.get("code"))) {
					wmsPushService.updateCategory((String)map.get("appKey"),(String)map.get("appToken"),wmsCategory);
				}
			}
		}
        
        return result;
    }


    /**
     * 分类绑定设置
     * @param category
     */
    @Override
    public void bindCategorySet(Category category) throws NoSuchFieldException, IllegalAccessException {
        String[] attrarr = category.getBindAttributeIds().split(",");
        if (attrarr.length == 0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性id错误");
        for (int i=0;i<attrarr.length;i++) {
            Attribute attr = attributeMapper.selectByPrimaryKey(Long.valueOf(attrarr[i]));
            if (attr == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "属性id错误");
        }
        Category cate = categoryMapper.selectByPrimaryKey(category.getId());
        if (cate == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类id错误");
        if (cate.getCategoryLevel() != 3) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "只能对三级菜单进行绑定设置");
        /*if (cate.getCategoryLevel() != 2 && cate.getCategoryLevel() != 3) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "只能对二、三级菜单进行绑定设置");
        if (cate.getCategoryLevel() == 2) {
            List p1 = categoryMapper.page(new Category(){{
                this.setCategoryLevel(3);
                this.setCategoryParentId(cate.getId());
            }});
            if (!p1.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当前分类下面有子分类，不能进行绑定设置");
        }*/
        category.setIsBindAttribute(1);
        int result = this.updateByPrimaryKeySelective(category);
        if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
    }

    @Override
    public Page findList(String key,Integer status) {
		if (StringUtils.isNotBlank(key)) {
			// 1.先匹配一级，如果匹配到一级，则返回一级的所有二级、三级
			List<Category> list1 = categoryMapper.page(new Category() {
				{
					this.setCategoryLevel(1);
					this.setStatus(status);
					this.setCategoryName(key);
				}
			});
			
			if (list1 != null && list1.size() > 0) {
				for (Category ca1 : list1) {
					List<Category> list2 = categoryMapper.page(new Category() {
						{
							this.setCategoryLevel(2);
							this.setStatus(status);
							this.setCategoryParentId(ca1.getId());
						}
					});

					if (list2 != null && list2.size() > 0) {
						ca1.setChildren(list2);

						for (Category ca2 : list2) {
							List<Category> list3 = categoryMapper.page(new Category() {
								{
									this.setCategoryLevel(3);
									this.setStatus(status);
									this.setCategoryParentId(ca2.getId());
								}
							});

							if (list3 != null && list3.size() > 0) {
								ca2.setChildren(list3);
							}
						}
					}
				}
			}else {
				// 2.一级没有匹配到，则匹配二级，如果二级匹配到，需要反查一级，返回所有的三级 
				List<Category> list2 = categoryMapper.page(new Category() {
					{
						this.setCategoryLevel(2);
						this.setStatus(status);
						this.setCategoryName(key);
					}
				});
				if (list2 != null && list2.size()>0) {
					list1=new ArrayList<Category>();
					
					Set<Long> ca1Ids=new HashSet<Long>();
					for (Category ca2 : list2) {
						ca1Ids.add(ca2.getCategoryParentId());
						
						List<Category> list3 = categoryMapper.page(new Category() {{
							this.setCategoryLevel(3);
							this.setStatus(status);
							this.setCategoryParentId(ca2.getId());
						}});
						ca2.setChildren(list3);
					}
					for (Long ca1Id : ca1Ids) {
						Category ca1=categoryMapper.selectByPrimaryKey(ca1Id);
						list1.add(ca1);
					}
					
					for (Category ca1 : list1) {
			            ca1.setChildren(new ArrayList<>());
			            for (Category ca2 : list2) {
			                if (ca2.getCategoryParentId().intValue() == ca1.getId().intValue()) {
			                    ca1.getChildren().add(ca2);
			                }
			            }
			        }
					
				}else {
					// 3.一级没有匹配到，二级也没匹配到，则匹配三级，如果三级匹配到，需要反查一级、二级，返回查到的三级
					List<Category> list3 = categoryMapper.page(new Category() {
						{
							this.setCategoryLevel(3);
							this.setStatus(status);
							this.setCategoryName(key);
						}
					});
					if (list3 != null && list3.size()>0) {
						list2=new ArrayList<Category>();
						
						Set<Long> ca2Ids=new HashSet<Long>();
						for (Category ca3 : list3) {
							ca2Ids.add(ca3.getCategoryParentId());
						}
						for (Long ca2Id : ca2Ids) {
							Category ca2=categoryMapper.selectByPrimaryKey(ca2Id);
							list2.add(ca2);
						}
						
						Set<Long> ca1Ids=new HashSet<Long>();
						for (Category ca2 : list2) {
							ca1Ids.add(ca2.getCategoryParentId());
						}
						for (Long ca1Id : ca1Ids) {
							Category ca1=categoryMapper.selectByPrimaryKey(ca1Id);
							list1.add(ca1);
						}
						
						for (Category ca1 : list1) {
				            ca1.setChildren(new ArrayList<>());
				            for (Category ca2 : list2) {
				                ca2.setChildren(new ArrayList<>());
				                if (ca2.getCategoryParentId().intValue() == ca1.getId().intValue()) {
				                    ca1.getChildren().add(ca2);
				                }
				                for (Category ca3 : list3) {
				                    if (ca3.getCategoryParentId().intValue() == ca2.getId().intValue()) {
				                        ca2.getChildren().add(ca3);
				                    }
				                }
				            }
				        }
					}
				}
			}
			
			PageInfo pageInfo = new PageInfo(list1);
		    return new Page(pageInfo);
		}else {
			List<Category> list1 = categoryMapper.page(new Category(){{
	                this.setCategoryLevel(1);
	                this.setStatus(status);
	            }});
	        List<Category> list2 = categoryMapper.page(new Category(){{
	            this.setCategoryLevel(2);
	            this.setStatus(status);
	        }});
	        List<Category> list3 = categoryMapper.page(new Category(){{
	            this.setCategoryLevel(3);
	            this.setStatus(status);
	        }});

	        for (Category ca1 : list1) {
	            ca1.setChildren(new ArrayList<>());
	            for (Category ca2 : list2) {
	                ca2.setChildren(new ArrayList<>());
	                if (ca2.getCategoryParentId().intValue() == ca1.getId().intValue()) {
	                    ca1.getChildren().add(ca2);
	                }
	                for (Category ca3 : list3) {
	                    if (ca3.getCategoryParentId().intValue() == ca2.getId().intValue()) {
	                        ca2.getChildren().add(ca3);
	                    }
	                }
	            }
	        }
	        
	        PageInfo pageInfo = new PageInfo(list1);
	        return new Page(pageInfo);
		}
    }

}
