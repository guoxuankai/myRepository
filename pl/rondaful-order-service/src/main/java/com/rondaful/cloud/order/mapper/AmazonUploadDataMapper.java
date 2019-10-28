package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.Amazon.AmazonUploadData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AmazonUploadDataMapper extends BaseMapper<AmazonUploadData> {
    String selectSubMaxTimeBySiteIdAndSellerId(@Param("marketplaceId") String marketplaceId, @Param("amazonSellerId") String amazonSellerId);
    String selectSubMinTimeBySiteIdAndSellerId(@Param("marketplaceId") String marketplaceId, @Param("amazonSellerId") String amazonSellerId);

    void updateProcessStatus(@Param("feedSubmissionId") String feedSubmissionId, @Param("feedProcessingStatus")String feedProcessingStatus);

    List<AmazonUploadData> selectData(@Param("amazonSellerId") String amazonSellerId, @Param("marketplaceId")String marketplaceId);

    void updateUploadStatusSuccessful(@Param("feedSubmissionId") String feedSubmissionId,@Param("messageIdList_Fail") List<Integer> messageIdList_Fail);

    void updateUploadStatusFail(@Param("amazonOrderID")String amazonOrderID,
                                @Param("amazonOrderItemCode")String amazonOrderItemCode,
                                @Param("feedSubmissionId")String feedSubmissionId,
                                @Param("messageId")Integer messageId,
                                @Param("resultCode")String resultCode,
                                @Param("resultMessageCode")Integer resultMessageCode,
                                @Param("resultDescription")String resultDescription);

    void updateUploadResultInfoOnly(@Param("amazonOrderID")String amazonOrderID,
                                @Param("amazonOrderItemCode")String amazonOrderItemCode,
                                @Param("feedSubmissionId")String feedSubmissionId,
                                @Param("messageId")Integer messageId,
                                @Param("resultCode")String resultCode,
                                @Param("resultMessageCode")Integer resultMessageCode,
                                @Param("resultDescription")String resultDescription);


}