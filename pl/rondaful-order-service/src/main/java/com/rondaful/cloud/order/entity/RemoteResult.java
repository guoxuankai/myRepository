package com.rondaful.cloud.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RemoteResult implements Serializable {
    private String msg;
    private String data;
    private String success;
    private String errorCode;
}
