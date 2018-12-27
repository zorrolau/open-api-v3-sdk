package com.okcoin.commons.okex.open.api.bean.websocket.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MarkTicker {

    private String close;
    private String coinVolume;
    private String contract;
    private String high;
    private String holdAmount;
    private Long id;
    private String low;
    private String open;
    private String timestamp;
    private String volume;

}
