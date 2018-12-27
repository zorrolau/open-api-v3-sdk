package com.okcoin.commons.okex.open.api.bean.websocket.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PriceRange {
    private String contract;
    private String high;
    private String low;
    private String usdCnyRate;
}
