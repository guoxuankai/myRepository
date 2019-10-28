package com.rondaful.cloud.seller.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.Upcmanage;
import com.rondaful.cloud.seller.entity.upcResult;

public interface UpcmanageMapper extends BaseMapper<Upcmanage> {


    /**
     * 通过id停用upc码
     * @param status
     * @param id
     * @return
     */
    int updateStatusById(@Param("usestatus")Integer useStatus,@Param("id")Integer id);

    /**
     * 停用或启用全部upc码
     * @return
     */
    int updateAllStatus(@Param("usestatus")Integer useStatus,@Param("account")String account);


    /**
     * 通过批次停用或启用全部upc码
     * @param status
     * @param id
     * @return
     */
    int updateStatusByNumberBatch(@Param("usestatus")Integer useStatus,
    		                      @Param("numberBatch")String[] numberBatch,
    		                      @Param("account")String account);



    /**
     * 通过类型停用或启用全部upc码
     * @param status
     * @param id
     * @return
     */
    int updateStatusByNumberType(@Param("usestatus")Integer useStatus,
    		                     @Param("numberType")String numberType,
    		                     @Param("account")String account);



    /**
     * 修改已使用的upc码状态
     * @param status
     * @param numbers
     */
    void updateStatusNumbers(@Param("status")Integer status,@Param("list")List numbers);


    /**
     * 模糊查询upc列表信息
     * @param numberBatch
     * @param numberType
     * @return
     */
    List<Upcmanage> fuzzyFindAll(@Param("numberbatch")String numberbatch,
                                 @Param("numbertype")String numbertype,
                                 @Param("number")String number,
                                 @Param("account")String account);


    /**
     * 查询upc列表信息
     * @param numberBatch
     * @param numberType
     * @return
     */
    List<Upcmanage> findAll(String account);


    /**
     * 统计数据查询
     * @return
     */
    List<upcResult> selectUpcResult(String account);


    /**
     * 查询一条数据
     * @param status
     * @param numbertype
     * @return
     */
    List<String> selectObject(@Param("numberType")String numberType,
                              @Param("number")Integer number,
                              @Param("account")String account,
                              @Param("usedplatform")Integer usedplatform,
                              @Param("oneselfType")Integer oneselfType);

    /**
     * 查询UPC可用数量
     * @param status
     * @param numbertype
     * @return
     */
    Integer selectEableCounts(@Param("numberType")String numberType,
                              @Param("account")String account,
                              @Param("usedplatform")Integer usedplatform,
                              @Param("oneselfType")Integer oneselfType);


    /**
     * 刊登时upc码的状态修改
     */
    void updateUPCStatus(
            @Param("status")Integer status,
            @Param("usedplatform")Integer usedplatform,
            @Param("number")String number);

    /**
     * 查询UPC使用平台信息
     * @return
     */
    Integer selectUsedplatform(String number);


    /**
     * 查询upc统计总数
     * @param account
     * @return
     */
    Integer selectTotal(String account);

    Integer selectEbayUPCUsed(String account);
    Integer selectEbayUPCUseable(String account);
    Integer selectEbayEANUsed(String account);
    Integer selectEbayEANUseable(String account);
    Integer selectEbayISANUsed(String account);
    Integer selectEbayISANUseable(String account);

    Integer selectAmazonUPCUsed(String account);
    Integer selectAmazonUPCUseable(String account);
    Integer selectAmazonEANUsed(String account);
    Integer selectAmazonEANUseable(String account);
    Integer selectAmazonISANUsed(String account);
    Integer selectAmazonISANUseable(String account);
    
    
    Integer selectUPCDead(String account);
    Integer selectEANDead(String account);
    Integer selectISBNDead(String account);

    

    /**
     * 校验upc码是否重复
     * @param number
     * @return
     */
    Integer checkNumber(String number);

    
    /**
     * 查询所有商品编码批次
     * @param account
     * @return
     */
    List<String> checkNumberBatch(String account);





}
