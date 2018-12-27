package com.okcoin.commons.okex.open.api.bean.websocket.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Candle {
    private String contract;
    private List<String> candle;
//    private String time;
//    private String open;
//    private String high;
//    private String low;
//    private String close;
//    private String volume;
//    private String coinVolume;
}
