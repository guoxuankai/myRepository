package com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe;

import lombok.Data;

@Data
public class GoodCangAccepDto {

    private String AppToken;

    private String Sign;

    private String MessageType;

    private String Message;

    private String MessageId;

    private String SendTime;
}
