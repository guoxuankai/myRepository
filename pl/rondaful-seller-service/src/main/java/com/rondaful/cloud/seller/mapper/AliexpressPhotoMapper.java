package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPhoto;
import com.rondaful.cloud.seller.vo.AliexpressPhotoSearchVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressPhotoMapper extends BaseMapper<AliexpressPhoto> {

    public List<AliexpressPhoto> findPage(AliexpressPhotoSearchVO vo);

    public List<AliexpressPhoto> getAliexpressPhotoList(@Param("empowerId") Long empowerId,@Param("photoIds") List<Long> photoIds);

    /**
     *	 批量写入
     * @param list
     * @return
     */
    Integer insertBatch(List<AliexpressPhoto> list);

    /**
     *	 批量写入
     * @param list
     * @return
     */
    Integer updateBatch(List<AliexpressPhoto> list);

}