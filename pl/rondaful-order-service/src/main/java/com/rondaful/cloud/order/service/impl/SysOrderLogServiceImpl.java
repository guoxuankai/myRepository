package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.mapper.SysOrderLogMapper;
import com.rondaful.cloud.order.mapper.SysOrderMapper;
import com.rondaful.cloud.order.mapper.SysOrderNewMapper;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-01-14 20:38
 * 包名: com.rondaful.cloud.order.service.impl
 * 描述:
 */
@Service
public class SysOrderLogServiceImpl extends BaseServiceImpl<SysOrderLog> implements ISysOrderLogService {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SysOrderLogServiceImpl.class);
    @Autowired
    private SysOrderNewMapper sysOrderNewMapper;
    @Autowired
    private SysOrderLogMapper sysOrderLogMapper;
    @Autowired
    private SysOrderMapper sysOrderMapper;
    @Override
    public List<SysOrderLog> selectSysOrderLogByOrderId(String sysOrderId) {
        return sysOrderLogMapper.selectSysOrderLogByOrderId(sysOrderId);
    }

    /**
     * 批量插入日志
     *
     * @param list
     * @return
     */
    @Override
    public Integer inserts(List<SysOrderLog> list) {
        return sysOrderLogMapper.inserts(list);
    }

    @Override
    public Map<String, Object> queryOrderSchedule(String orderId) {
        Map<String, Object> map = new HashMap<>();
        SysOrderNew sysOrderNew = sysOrderNewMapper.queryOrderByOrderId(orderId);
        Byte orderStatus = sysOrderNew.getOrderDeliveryStatus();
//        SysOrder order = sysOrderMapper.getSysOrderDetailByPlOrderId(orderId);
//        Byte orderStatus = order.getOrderDeliveryStatus();
        //1待发货,2缺货,3配货中,4已拦截,5已发货,6部分发货,7已作废,8已完成',
        if (orderStatus == 0 || orderStatus == 7||orderStatus==null) {
            //已拆分的主订单和已合并的子订单orderStatus为0，全部置为灰色  还有已取消的订单也是全部置为灰色
            return getStringObjectMap(map);
        }
        //将订单操作日志按照订单状态分类
        Map<Byte, List<SysOrderLog>> collect1 = sysOrderLogMapper.selectSysOrderLogByOrderId(orderId).stream().collect(Collectors.groupingBy(SysOrderLog::getOrderStatus));
        if (collect1 == null) {
            logger.error("查询系统订单日志为Null，订单号为" + orderId);
            return getStringObjectMap(map);
        }
        switch (orderStatus) {
            case 1://待发货
                for (Byte aByte : collect1.keySet()) {
                    //TODO BUG：目前拆分后的订单第一条日志记录订单状态为已拆分而不是待发货，所以这里要加10 合并后的订单同样如此，所以加了11     后期需改善拆合单日志记录
                    if (aByte == orderStatus||aByte==10||aByte==11) {
                        map.put("1", collect1.get(aByte).get(collect1.get(aByte).size()-1).getCreateDate());//待发货
                        map.put("2", null);
                        map.put("3", null);
                        map.put("4", null);
                        map.put("5", null);
                    }
                }
                return map;
            case 3://配货中
                for (Byte aByte : collect1.keySet()) {
                    if (aByte == 1) {
                        map.put("1", collect1.get(aByte).get(collect1.get(aByte).size()-1).getCreateDate()); //待发货
                    }
                    if (aByte == orderStatus) {
                        map.put("2", collect1.get(aByte).get(0).getCreateDate());//已付款
                        map.put("3", collect1.get(aByte).get(0).getCreateDate());//配货中
                        map.put("4", null);
                        map.put("5", null);
                    }
                }
                return map;
            case 4://已拦截
                for (Byte aByte : collect1.keySet()) {
                    if (aByte == 1) {
                        map.put("1", collect1.get(aByte).get(collect1.get(aByte).size()-1).getCreateDate()); //待发货
                    }
                    if (aByte == 3) {
                        map.put("2", collect1.get(aByte).get(0).getCreateDate());//已付款，取配货中的最后时间
                        map.put("3", null);
                        map.put("4", null);
                        map.put("5", null);
                    }
                }
                return map;
            case 5://已发货
                for (Byte aByte : collect1.keySet()) {
                    if (aByte == 1) {
                        map.put("1", collect1.get(aByte).get(collect1.get(aByte).size()-1).getCreateDate()); //待发货
                    }
                    if (aByte == 3) {
                        map.put("2", collect1.get(aByte).get(0).getCreateDate()); //已付款
                        map.put("3", collect1.get(aByte).get(0).getCreateDate()); //配货中
                    }
                    if (aByte == 5) {
                        map.put("4", collect1.get(aByte).get(0).getCreateDate()); //已发货
                        map.put("5", null);
                    }
                }
                return map;
            case 6:
                for (Byte aByte : collect1.keySet()) {
                    if (aByte == 1) {
                        map.put("1", collect1.get(aByte).get(collect1.get(aByte).size()-1).getCreateDate()); //待发货
                    }
                    if (aByte == 3) {
                        map.put("2", collect1.get(aByte).get(0).getCreateDate()); //已付款
                        map.put("3", collect1.get(aByte).get(0).getCreateDate()); //配货中
                    }
                    if (aByte == 5) {
                        map.put("4", collect1.get(aByte).get(0).getCreateDate()); //已发货
                        map.put("5", null);
                    }
                }
                return map;
            case 8://已完成
                for (Byte aByte : collect1.keySet()) {
                    if (aByte == 1) {
                        map.put("1", collect1.get(aByte).get(collect1.get(aByte).size()-1).getCreateDate()); //待发货
                    }
                    if (aByte == 3) {
                        map.put("2", collect1.get(aByte).get(0).getCreateDate()); //已付款
                        map.put("3", collect1.get(aByte).get(0).getCreateDate()); //配货中
                    }
                    if (aByte == 5) {
                        map.put("4", collect1.get(aByte).get(0).getCreateDate()); //已发货
                    }
                    if (aByte == 8) {
                        map.put("5", collect1.get(aByte).get(0).getCreateDate());//已完成
                    }
                }
               /* if (map.get("5") ==null) {  // TODO  BUG:订单操作日志没有  该时间取的是订单的最后更新时间
                map.put("5", order.getUpdateDate());
                }*/
                return map;
        }
        return map;
    }
    public Map<String, Object> getStringObjectMap(Map<String, Object> map) {
        map.put("1", null);
        map.put("2", null);
        map.put("3", null);
        map.put("4", null);
        map.put("5", null);
        return map;
    }

    public SysOrderLog findSysOrderLogByMessage(String sysOrderId, String content) {
        return sysOrderLogMapper.findSysOrderLogByMessage(sysOrderId, content);
    }
}
