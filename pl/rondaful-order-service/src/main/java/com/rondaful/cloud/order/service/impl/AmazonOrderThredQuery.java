package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import com.rondaful.cloud.order.mapper.AmazonOrderMapper;
import com.rondaful.cloud.order.service.IAmazonOrderService;
import com.rondaful.cloud.order.utils.ApplicationContextProvider;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.concurrent.Callable;

@Mapper
public class AmazonOrderThredQuery implements Callable<List<AmazonOrder>> {

    private IAmazonOrderService myService;//需要通过够早方法把对应的业务service传进来 实际用的时候把类型变为对应的类型
    private int bindex;//分页index
    private int num;//数量
    private List<Integer> shopNameIdLists;
    private String orderId;
    private List<Integer> plIdLists;
    private String orderStatus;
    private  Byte plProcessStatuss;
    private String startDate;
    private String endDate;
    private AmazonOrderMapper amazonOrderMapper = ApplicationContextProvider.getApplicationContext().getBean(AmazonOrderMapper.class);

    public AmazonOrderThredQuery(IAmazonOrderService myService,List<Integer> shopNameIdLists,String orderId,
                                List<Integer> plIdLists, String orderStatus,Byte plProcessStatuss,
                                String startDate,String endDate,Integer bindex,Integer num){
        this.myService=myService;
        this.bindex=bindex;
        this.num=num;
        this.shopNameIdLists=shopNameIdLists;
        this.orderId=orderId;
        this.plIdLists=plIdLists;
        this.orderStatus=orderStatus;
        this.plProcessStatuss=plProcessStatuss;
        this.startDate=startDate;
        this.endDate=endDate;

    }
    @Override
    public List<AmazonOrder> call() throws Exception {
        List<AmazonOrder> list = amazonOrderMapper.selectAmazonOrderByMultiCondition(shopNameIdLists, orderId, plIdLists, orderStatus,  plProcessStatuss, startDate, endDate,bindex,num);
        for (AmazonOrder amazonOrder : list) {
            amazonOrder.setOrderStatus(com.rondaful.cloud.common.utils.Utils.translation(amazonOrder.getOrderStatus()));//翻译
        }
        return list;
    }

}

