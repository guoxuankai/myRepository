package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.dto.CellsUnitDto;
import com.brandslink.cloud.finance.pojo.dto.CustomerDto;
import com.brandslink.cloud.finance.pojo.dto.SkuTypeDto;
import com.brandslink.cloud.finance.pojo.entity.Customer;
import com.brandslink.cloud.finance.pojo.entity.CustomerConfigEntity;
import com.brandslink.cloud.finance.pojo.entity.StandardQuoteDetail;
import com.brandslink.cloud.finance.pojo.vo.CustomerVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerMapper extends BaseMapper<Customer> {
    CustomerDto getByCustomerCode(@Param("customerCode") String customerCode);

    List<CustomerDto> getList(CustomerVo param);

    Boolean updateByRecharge(@Param("customerCode")String customerCode,@Param("money") BigDecimal money);

    CustomerConfigEntity getConfig(@Param("customerCode")String customerCode);

    SkuTypeDto getSkuType(@Param("cellsType")Integer cellsType ,@Param("value")Double value);

    Integer getCellIdByCellsType(@Param("cellsType")Integer cellsType);

    StandardQuoteDetail getQuoteDetail(@Param("rowId")Integer rowId, @Param("cellsId")Integer cellsId, @Param("quoteType")Integer quoteType);



    CellsUnitDto getCellUnit(@Param("value") Integer value);

    /**
     * 支出
     * @param customerCode
     * @param money
     * @return
     */
    Boolean updateByExpend(@Param("customerCode")String customerCode, @Param("money") BigDecimal money);

    /**
     * 冻结
     * @param customerCode
     * @param money
     * @return
     */
    Boolean updateByFreeze(@Param("customerCode")String customerCode, @Param("money") BigDecimal money);

    /**
     * 解结并支出
     * @param customerCode
     * @param money
     * @param money
     * @return
     */
    Boolean updateByUnFreeze(@Param("customerCode")String customerCode, @Param("money") BigDecimal money,@Param("freezeMoney")BigDecimal freezeMoney);
}