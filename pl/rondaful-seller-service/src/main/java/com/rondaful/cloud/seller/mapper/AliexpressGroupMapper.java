package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressGroupMapper extends BaseMapper<AliexpressGroup> {

    public List<AliexpressGroup> getAliexpressGroupByPlAccountList(@Param("plAccount")String plAccount, @Param("groupId")Long groupId,@Param("empowerId")Long empowerId);

    public int deleteAliexpressGroupByEmpowerId(@Param("empowerId")Long empowerId);

}