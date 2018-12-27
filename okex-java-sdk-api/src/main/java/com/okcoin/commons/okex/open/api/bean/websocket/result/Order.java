package com.okcoin.commons.okex.open.api.bean.websocket.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Setter
@Getter
@ToString
public class Order {
    private String amount;
    private String closeProfit;
    private String contract;
    private String createTime;
    private String dealAmount;
    private String dealValue;
    private String fee;
    private String id;
    private String leverRate;
    private String openMarginHold;
    private String price;
    private String priceAvg;
    private String status;
    private String systemType;
    private String type;
    private String unitAmount;
}
