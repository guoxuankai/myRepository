package com.rondaful.cloud.user.kafka;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.user.entity.SysLog;
import com.rondaful.cloud.user.service.ILoggerService;

/**
 * kafka日志操作记录监听
 */
@Component
public class OperationLogListener {

    protected final Logger logger = LoggerFactory.getLogger(OperationLogListener.class);

    @Autowired
    private ILoggerService loggerService;

    @KafkaListener(topics = {"systemOperationLogKafka"})
    public void listen(ConsumerRecord<?, ?> record) {
        Map map = JSON.parseObject(record.value().toString(), Map.class);
        SysLog sys = JSON.parseObject(map.get("systemLog").toString(), SysLog.class);
        if (map.get("user")==null){
            return;
        }
        UserAll user = JSON.parseObject(((JSONObject)map.get("user")).toString(), UserAll.class);
        if ( user != null ) {
            sys.setLoginName(user.getUser().getLoginName());
            sys.setUsername(user.getUser().getUsername());
            sys.setPlatformType(user.getUser().getPlatformType());
        }else {
            return;
        }
        if ( loggerService.insert(sys)!=1 ) {
            logger.error("操作日志信息添加失败");
        }
    }

}
