package com.okcoin.commons.okex.open.api.bean.websocket.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Setter
@Getter
@ToString
public class Account {

    private String profitUnReal;
    private String posMargin;
    private String accountRights;
    private String openMarginHold;
    private String contract;
    private String profitReal;
    private String availableMargin;
    private String positionMarginRate;
    private String marginRate;


}
