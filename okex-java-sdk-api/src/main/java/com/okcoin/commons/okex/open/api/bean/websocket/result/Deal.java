package com.okcoin.commons.okex.open.api.bean.websocket.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Deal {
    private String trade_id;
    private String price;
    private String size;
    private String side;
    private String time;
    private String contract;
}
