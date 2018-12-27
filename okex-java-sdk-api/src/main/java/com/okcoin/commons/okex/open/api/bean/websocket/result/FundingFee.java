package com.okcoin.commons.okex.open.api.bean.websocket.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FundingFee {

    private String contract;
    private String fundingRate;
    private String time;

}
