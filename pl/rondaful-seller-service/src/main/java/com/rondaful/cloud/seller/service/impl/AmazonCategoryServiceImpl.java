package com.rondaful.cloud.seller.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.seller.entity.amazon.AmazonCategory;
import com.rondaful.cloud.seller.mapper.AmazonCategoryMapper;
import com.rondaful.cloud.seller.service.AmazonCategoryService;

@Service
public class AmazonCategoryServiceImpl implements AmazonCategoryService {

    @Autowired
    AmazonCategoryMapper amazonCategoryMapper;

    @Override
    public List<AmazonCategory> queryCategoryList(Long categoryId, String siteName) {

        AmazonCategory actegory = new AmazonCategory();
        actegory.setParentId(categoryId);
        actegory.setSite(siteName);
        return amazonCategoryMapper.selectCategory(actegory);
    }

    @Override
    public List<AmazonCategory> queryCategoryListByCategoryId(Long[] ids, String siteName) {
        return amazonCategoryMapper.selectCategoryListByCategoryId(ids, siteName);
    }

    @Override
    public List<AmazonCategory> selectCategoryListById(Integer[] ids) {
        return amazonCategoryMapper.selectCategoryListById(ids);
    }

    
    @Override
    public List<AmazonCategory> queryCategoryListSiteNameAndKeyWord(String keyWord, String siteName) {
        //多于10条则保留并返回10条数据，少于10条则返回全部数据
        List<AmazonCategory> actegoryList = new ArrayList<AmazonCategory>();
        //List<AmazonCategory>
        AmazonCategory actegory = new AmazonCategory();
        actegory.setSite(siteName);
        actegory.setName(keyWord);
        actegory.setChildCount(0);
        //先查询childCount为0分类
        List<AmazonCategory> actegoryListNoChild = amazonCategoryMapper.selectCategoryListBySiteAndKeyWord(actegory);
        
        //如果总数大于10，保留10条并返回
        if (actegoryListNoChild != null && actegoryListNoChild.size() >= 10) {
            actegoryList = actegoryListNoChild.subList(0, 10);
        }
        //如果总数少于10则查询其他
        if (actegoryListNoChild != null && actegoryListNoChild.size() < 10) {
            actegory.setChildCount(null);
            List<AmazonCategory> actegoryListHasChild = amazonCategoryMapper.selectCategoryListBySiteAndKeyWord(actegory);
            HashMap<String, AmazonCategory> resulteSet = new HashMap<>();
            actegoryList.forEach(a -> resulteSet.put(String.valueOf(a.getId()), a));
            //AmazonCategory category;
            List<AmazonCategory> results=new LinkedList<>();
            for(AmazonCategory a : actegoryListHasChild){
            	results.addAll(getLastChild(a));
            }
            for (AmazonCategory category : results) {
        		resulteSet.put(String.valueOf(category.getId()),category);
                if(resulteSet.size() >= 10)
                    break;
			}
            actegoryList = new ArrayList<>(resulteSet.values());
            actegoryList.addAll(actegoryListHasChild);
        }
        
        List<AmazonCategory> listLink=new LinkedList<>();
        for (AmazonCategory amazonCategory : actegoryList) {
        	if(keyWord.equals(amazonCategory.getName())) {
        		listLink.add(amazonCategory);
        	}
		}
        if(listLink.size() >0) {
        	actegoryList.removeAll(listLink);
        }
        //排序
        Collections.sort(actegoryList,new SortByLengthComparator());
        listLink.addAll(actegoryList);
        
        return listLink;
    }

    
    
    static class SortByLengthComparator implements Comparator<AmazonCategory> {
    	@Override
    	public int compare(AmazonCategory var1, AmazonCategory var2) {
    		if (var1.getName().length() > var2.getName().length()) {
    			return 1;
    		} else if (var1.getName().length() == var2.getName().length()) {
    			return 0;
    		} else {
    			return -1;
    		}
    	}
    }


    private List<AmazonCategory> getLastChild(AmazonCategory category) {
    	List<AmazonCategory> result=new LinkedList<>();
    	if (category.getChildCount() == 0) {
    		result.add(category);
            return result;
    	}    
        List<AmazonCategory> child = getChild(category);
        if(child == null) {
        	result.add(category);
            return result;
        }
        for (AmazonCategory amazonCategory : child) {
        	if(amazonCategory.getChildCount() == 0) {
        		if(result.size()==10) {
        			return result;
        		}
        		result.add(amazonCategory);
        	}else {
        		this.getLastChild(amazonCategory);
        	}
		}
        return result;
    }

    private List<AmazonCategory> getChild(AmazonCategory category) {
        List<AmazonCategory> amazonCategories = amazonCategoryMapper.selectCategory(new AmazonCategory() {{
            setParentId((long) category.getId());
            setSite(category.getSite());
        }});
        if(amazonCategories != null && amazonCategories.size() >0 ){
            //return amazonCategories.get(0);
        	return amazonCategories;
        }else
            return null;

    }


}
