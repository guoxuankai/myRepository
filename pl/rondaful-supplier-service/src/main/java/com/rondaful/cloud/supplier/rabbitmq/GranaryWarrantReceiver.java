package com.rondaful.cloud.supplier.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.dto.GranarySendRequest;
import com.rondaful.cloud.supplier.entity.WarehouseWarrant;
import com.rondaful.cloud.supplier.entity.WarehouseWarrantDetail;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.mapper.WarehouseWarrantDetailMapper;
import com.rondaful.cloud.supplier.mapper.WarehouseWarrantMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class GranaryWarrantReceiver {

    @Autowired
    private WarehouseWarrantMapper warehouseWarrantMapper;

    @Autowired
    private WarehouseWarrantDetailMapper warrantDetailMapper;


    private final static Logger log = LoggerFactory.getLogger(GranaryWarrantReceiver.class);

    ExecutorService service = Executors.newFixedThreadPool(50);

    /**
     * 监听新增的商品 @Title: process @Description: TODO(这里用一句话描述这个方法的作用) @param @param
     * message 参数 @return void 返回类型 @throws
     */
    // 监听队列queue-a
    @RabbitListener(queues = "queue-goodcang-sendreceiving")
    public void process(String message) {

        log.info("接收的谷仓入库单信息==>{}", message);
        try {

            JSONObject body = JSONObject.parseObject(message);
            GranarySendRequest request = JSONObject.parseObject(body.getString("message"), GranarySendRequest.class);
            String receivingCode = request.getReceiving_code();
            // 更新入库单表'入库单状态'
            WarehouseWarrant warrant = warehouseWarrantMapper.selectWarehouseWarrantDetailByReceivingCode(receivingCode);
            if (warrant != null) {
                WarehouseWarrant update = new WarehouseWarrant();
                update.setId(warrant.getId());
                update.setReceivingStatus(new Byte("3"));
                warehouseWarrantMapper.updateByPrimaryKeySelective(update);
            }
            // 更新入库单商品明细表
            List<GranarySendRequest.ReceivingDetail> detailList = request.getReceivingDetail();
            if (CollectionUtils.isNotEmpty(detailList)) {
                String sequenceNumber = warrant.getSequenceNumber();
                List<WarehouseWarrantDetail> warrantDetailList = warrantDetailMapper.selectByParentSequenceNumber(sequenceNumber);
                if (warrantDetailList.size() != detailList.size()) {
                    log.error("谷仓入库单推送异常，推送入库明细商品数量与创建入库单商品数量不一致，无法同步!");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100409, "谷仓入库单推送同步失败!");
                }
                List<WarehouseWarrantDetail> insertWarehouseWarrantDetail = new ArrayList<>();
                for (GranarySendRequest.ReceivingDetail detail : detailList) {
                    for (WarehouseWarrantDetail warrantDetail : warrantDetailList) {
                        // 拿到商品SKU对应的商品明细
                        if (StringUtils.equals(detail.getProduct_sku(), warrantDetail.getProductSku())) {
                            warrantDetail.setOverseasPreCount(detail.getDeliveryQty());
                            warrantDetail.setOverseasReceivingCount(detail.getReceiptQty());
                            warrantDetail.setOverseasShelvesCount(detail.getPutawayQty());
                            warrantDetail.setOverseasUnsellableQty(detail.getUnsellableQty());
                            warrantDetail.setOverseasSellableQty(detail.getSellableQty());
                            insertWarehouseWarrantDetail.add(warrantDetail);
                            break;
                        }
                    }
                }
                // 添加更新后的入库单商品明细
                Map<String, Object> map = new HashMap<>();
                map.put("listColumn", insertWarehouseWarrantDetail.get(0));
                map.put("listData", insertWarehouseWarrantDetail);
                warrantDetailMapper.deleteByParentSequenceNumber(sequenceNumber);
                warrantDetailMapper.insertList(map);
                log.info("谷仓入库单推送 Success...");
            }
        } catch (Exception e) {
            log.error("谷仓入库单推送，更新入库单状态异常");
            e.printStackTrace();
        }
    }
}
