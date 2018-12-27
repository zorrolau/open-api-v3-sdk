package com.okcoin.commons.okex.open.api.bean.websocket.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@Setter
@ToString
public class MarkCandle {
    private String contract;
    private List<String> candle;
//    private String time;
//    private String low;
//    private String high;
//    private String open;
//    private String close;
//    private String volume;
//    private String currency_volume;
}
