package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.SellerCompanyInfo;
import org.apache.ibatis.annotations.Param;

public interface SellerCompanyInfoMapper extends BaseMapper<SellerCompanyInfo> {

    /**
     * 根据用户id获取卖家公司信息
     * @param userId
     * @return
     */
    SellerCompanyInfo getByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户删除公司
     * @param userId
     * @return
     */
    Integer deleteByUserId(@Param("userId") Integer userId);

    /**
     * 修改主营类目
     * @param userId
     * @param mainCategory
     * @return
     */
    Integer updateMainCategory(@Param("userId") Integer userId,@Param("mainCategory") String mainCategory);
}