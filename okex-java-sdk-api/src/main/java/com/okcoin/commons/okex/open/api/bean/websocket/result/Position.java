package com.okcoin.commons.okex.open.api.bean.websocket.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class Position {
    private String availableNum;
    private String contract;
    private String estimateLiquidatePrice;
    private String leverRate;
    private String marginRate;
    private String openPriceAvg;
    private String posMargin;
    private String posQty;
    private String positionMarginRate;
    private String profit;
    private String profitRate;
    private String profitUnReal;
    private String settlePrice;
    private String settleProfit;
    private String side;
    private String unitAmount;
}
