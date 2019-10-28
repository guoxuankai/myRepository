package com.brandslink.cloud.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.constants.CustomerFlowConstant;
import com.brandslink.cloud.finance.mapper.CustomerFlowMapper;
import com.brandslink.cloud.finance.mapper.CustomerMapper;
import com.brandslink.cloud.finance.mapper.SysAccountFlowMapper;
import com.brandslink.cloud.finance.mapper.SysAccountMapper;
import com.brandslink.cloud.finance.pojo.dto.CustomerDto;
import com.brandslink.cloud.finance.pojo.entity.Customer;
import com.brandslink.cloud.finance.pojo.entity.CustomerFlow;
import com.brandslink.cloud.finance.pojo.entity.SysAccount;
import com.brandslink.cloud.finance.pojo.entity.SysAccountFlow;
import com.brandslink.cloud.finance.pojo.feature.RechargeFeature;
import com.brandslink.cloud.finance.pojo.vo.CustomerAddVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerRechargeVo;
import com.brandslink.cloud.finance.pojo.vo.CustomerVo;
import com.brandslink.cloud.finance.service.CustomerService;
import com.brandslink.cloud.finance.utils.FinanceCommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author yangzefei
 * @Classname CustomerServiceImpl
 * @Description 客户帐号信息
 * @Date 2019/8/26 10:08
 */
@Service
@Transactional
public class CustomerServiceImpl extends BaseServiceImpl<Customer> implements CustomerService {

    @Resource
    private FinanceCommonUtil financeCommonUtil;

    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private CustomerFlowMapper customerFlowMapper;
    @Resource
    private SysAccountMapper sysAccountMapper;
    @Resource
    private SysAccountFlowMapper sysAccountFlowMapper;
            ;
    public CustomerDto get(String customerCode){
        return customerMapper.getByCustomerCode(customerCode);
    }

    public List<CustomerDto> getList(CustomerVo param){
        //默认上报时间降序
        if(!param.isValidSort("createTime", CustomerDto.class)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"排序字段或者排序方式不正确");
        }
        List<CustomerDto> list=customerMapper.getList(param);
        list.stream().forEach(p->p.setSerialNo(list.indexOf(p)+1));
        return list;
    }

    public void add(CustomerAddVo param){
        Customer customer=new Customer();
        customer.setOperate(financeCommonUtil.getOperate());
        customer.setCustomerName(param.getCustomerName());
        customer.setCustomerCode(param.getCustomerCode());
        customerMapper.insertSelective(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    public void recharge(CustomerRechargeVo param){
        if(StringUtils.isEmpty(param.getCustomerCode())){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"客户编码不能为空");
        }
        if(StringUtils.isEmpty(param.getCertificateUrl())||!Pattern.compile(CustomerFlowConstant.REGEXP_IMG).matcher(param.getCertificateUrl()).find()){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"凭证格式不正确");
        }
        if(BigDecimal.ZERO.compareTo(param.getMoney())==1){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"充值金额必须大于零");
        }
        CustomerDto customerDto=  customerMapper.getByCustomerCode(param.getCustomerCode());
        if(customerDto==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"客户不存在");
        }

        //1、客户账户充值金额、收入、账户余额、可用余额增加
        Boolean isRecharge=customerMapper.updateByRecharge(param.getCustomerCode(),param.getMoney());
        if(!isRecharge){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"充值失败");
        }
        //2、客户交易明细增加充值记录
        CustomerFlow customerFlow=getCustomerFlow(param);
        customerFlowMapper.insertSelective(customerFlow);

        //3、平台账号总收入、可用余额增加
        Integer sysId=1;
        isRecharge=sysAccountMapper.updateByRecharge(sysId,param.getMoney());
        if(!isRecharge){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"增加平台金额失败");
        }
        //4、系统账号余额流水增加充值记录
        SysAccountFlow sysAccountFlow=getSysAccountFlow(sysId,customerFlow);
        sysAccountFlowMapper.insertSelective(sysAccountFlow);
    }

    /**
     * 获取客户流水
     * @param param
     * @return
     */
    private CustomerFlow getCustomerFlow(CustomerRechargeVo param){
        CustomerDto customerDto=  customerMapper.getByCustomerCode(param.getCustomerCode());
        CustomerFlow customerFlow=new CustomerFlow();
        customerFlow.setOperate(financeCommonUtil.getOperate());
        customerFlow.setCustomerCode(param.getCustomerCode());
        customerFlow.setOrderNo(financeCommonUtil.getOrderNo(CustomerFlowConstant.BILL_NO_CZ));
        customerFlow.setBillTime(new Date());
        customerFlow.setBeforeMoney(customerDto.getBalanceMoney().subtract(param.getMoney()));
        customerFlow.setOriginalCost(param.getMoney());
        customerFlow.setDiscount(1.0);
        customerFlow.setDiscountCost(param.getMoney());
        customerFlow.setAfterMoney(customerDto.getBalanceMoney());
        customerFlow.setUsableMoney(customerDto.getUsableMoney());
        customerFlow.setFreezeMoney(customerDto.getFreezeMoney());
        customerFlow.setOrderType(2);
        customerFlow.setCostType(CustomerFlowConstant.COST_TYPE_RECHARGE);
        RechargeFeature feature=new RechargeFeature();
        feature.setCertificateUrl(param.getCertificateUrl());
        customerFlow.setFeatureJson(JSON.toJSONString(feature));
        return customerFlow;
    }

    /**
     * 获取平台账号流水
     * @param sysId
     * @param customerFlow
     * @return
     */
    private SysAccountFlow getSysAccountFlow(Integer sysId,CustomerFlow customerFlow){
        SysAccount sysAccount=sysAccountMapper.selectByPrimaryKey((long)sysId);
        SysAccountFlow sysAccountFlow=new SysAccountFlow();
        sysAccountFlow.setOperate(financeCommonUtil.getOperate());
        sysAccountFlow.setOrderNo(financeCommonUtil.getOrderNo(CustomerFlowConstant.BILL_NO_PT));
        sysAccountFlow.setSourceNo(customerFlow.getOrderNo());
        sysAccountFlow.setBeforeMoney(sysAccount.getUsableMoney().subtract(customerFlow.getDiscountCost()));
        sysAccountFlow.setBillMoney(customerFlow.getDiscountCost());
        sysAccountFlow.setAfterMoney(sysAccount.getUsableMoney());
        sysAccountFlow.setCostType(CustomerFlowConstant.COST_TYPE_RECHARGE);
        sysAccountFlow.setOrderType(2);
        sysAccountFlow.setSysAccountId(sysId);
        return sysAccountFlow;
    }
}
